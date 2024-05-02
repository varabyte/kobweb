package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.ksp.common.COMPONENT_STYLE_FQN
import com.varabyte.kobweb.ksp.common.COMPONENT_STYLE_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.COMPONENT_VARIANT_FQN
import com.varabyte.kobweb.ksp.common.COMPONENT_VARIANT_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.CSS_NAME_FQN
import com.varabyte.kobweb.ksp.common.CSS_PREFIX_FQN
import com.varabyte.kobweb.ksp.common.CSS_STYLE_FQN
import com.varabyte.kobweb.ksp.common.CSS_STYLE_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.INIT_KOBWEB_FQN
import com.varabyte.kobweb.ksp.common.INIT_SILK_FQN
import com.varabyte.kobweb.ksp.common.KEYFRAMES_FQN
import com.varabyte.kobweb.ksp.common.KEYFRAMES_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.LEGACY_COMPONENT_STYLE_FQN
import com.varabyte.kobweb.ksp.common.LEGACY_COMPONENT_VARIANT_FQN
import com.varabyte.kobweb.ksp.common.LEGACY_KEYFRAMES_FQN
import com.varabyte.kobweb.ksp.common.PACKAGE_MAPPING_PAGE_FQN
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.getPackageMappings
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.symbol.suppresses
import com.varabyte.kobweb.project.frontend.ComponentStyleEntry
import com.varabyte.kobweb.project.frontend.ComponentVariantEntry
import com.varabyte.kobweb.project.frontend.CssStyleEntry
import com.varabyte.kobweb.project.frontend.FrontendData
import com.varabyte.kobweb.project.frontend.InitKobwebEntry
import com.varabyte.kobweb.project.frontend.InitSilkEntry
import com.varabyte.kobweb.project.frontend.KeyframesEntry
import com.varabyte.kobweb.project.frontend.assertValid
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FrontendProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    private val qualifiedPagesPackage: String,
) : SymbolProcessor {
    private val frontendVisitor = FrontendVisitor() // ComponentStyle, ComponentVariant, Keyframes

    private val pageDeclarations = mutableListOf<KSFunctionDeclaration>()

    private val kobwebInits = mutableListOf<InitKobwebEntry>()
    private val silkInits = mutableListOf<InitSilkEntry>()
    private val cssStyles = mutableListOf<CssStyleEntry>()
    private val componentStyles = mutableListOf<ComponentStyleEntry>()
    private val componentVariants = mutableListOf<ComponentVariantEntry>()
    private val keyframesList = mutableListOf<KeyframesEntry>()

    // fqPkg to subdir, e.g. "blog._2022._01" to "01"
    private val packageMappings = mutableMapOf<String, String>()

    // We track all files we depend on so that ksp can perform smart recompilation
    // Even though our output is aggregating so generally requires full reprocessing, this at minimum means processing
    // will be skipped if the only change is deleted file(s) that we do not depend on.
    private val fileDependencies = mutableSetOf<KSFile>()

    // TODO: is this the best way to have this set up
    @OptIn(KspExperimental::class)
    private class KSTypes(resolver: Resolver) {
        val cssStyleType = resolver.getKotlinClassByName(CSS_STYLE_FQN)!!.asType(emptyList())
        val componentStyleType = resolver.getKotlinClassByName(COMPONENT_STYLE_FQN)!!.asStarProjectedType()
        val componentVariantType = resolver.getKotlinClassByName(COMPONENT_VARIANT_FQN)!!.asStarProjectedType()
        val keyframesType = resolver.getKotlinClassByName(KEYFRAMES_FQN)!!.asType(emptyList())
        val legacyComponentStyleType = resolver.getKotlinClassByName(LEGACY_COMPONENT_STYLE_FQN)!!.asType(emptyList())
        val legacyComponentVariantType = resolver.getKotlinClassByName(LEGACY_COMPONENT_VARIANT_FQN)!!.asType(emptyList())
        val legacyKeyframesType = resolver.getKotlinClassByName(LEGACY_KEYFRAMES_FQN)!!.asType(emptyList())
    }
    private lateinit var types: KSTypes

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // TODO: handle non-Silk usages

        types = KSTypes(resolver)

        kobwebInits += resolver.getSymbolsWithAnnotation(INIT_KOBWEB_FQN).map { annotatedFun ->
            fileDependencies.add(annotatedFun.containingFile!!)
            val name = (annotatedFun as KSFunctionDeclaration).qualifiedName!!.asString()
            InitKobwebEntry(name, acceptsContext = annotatedFun.parameters.size == 1)
        }

        silkInits += resolver.getSymbolsWithAnnotation(INIT_SILK_FQN).map { annotatedFun ->
            fileDependencies.add(annotatedFun.containingFile!!)
            val name = (annotatedFun as KSFunctionDeclaration).qualifiedName!!.asString()
            InitSilkEntry(name)
        }

        // only process files that are new to this round, since prior declarations are unchanged
        resolver.getNewFiles().forEach { file ->
            file.accept(frontendVisitor, Unit)

            // TODO: consider including this as part of the first visitor? does that matter for performance?
            packageMappings += getPackageMappings(file, qualifiedPagesPackage, PACKAGE_MAPPING_PAGE_FQN, logger).toMap()
                .also { if (it.isNotEmpty()) fileDependencies.add(file) }
        }

        pageDeclarations += resolver.getSymbolsWithAnnotation(PAGE_FQN).map { it as KSFunctionDeclaration }

        return emptyList()
    }

    private inner class FrontendVisitor : KSVisitorVoid() {
        private val cssStyleDeclaration = DeclarationType(
            name = CSS_STYLE_SIMPLE_NAME,
            qualifiedName = COMPONENT_STYLE_FQN,
            function = "ctx.theme.registerStyle",
        )
        private val componentStyleDeclaration = DeclarationType(
            name = COMPONENT_STYLE_SIMPLE_NAME,
            qualifiedName = COMPONENT_STYLE_FQN,
            function = "ctx.theme.registerStyle",
        )
        private val componentVariantDeclaration = DeclarationType(
            name = COMPONENT_VARIANT_SIMPLE_NAME,
            qualifiedName = COMPONENT_VARIANT_FQN,
            function = "ctx.theme.registerVariants",
        )
        private val keyframesDeclaration = DeclarationType(
            name = KEYFRAMES_SIMPLE_NAME,
            qualifiedName = KEYFRAMES_FQN,
            function = "ctx.stylesheet.registerKeyframes",
        )
        private val legacyComponentStyleDeclaration = DeclarationType(
            name = componentStyleDeclaration.name,
            qualifiedName = LEGACY_COMPONENT_STYLE_FQN,
            function = componentStyleDeclaration.function,
        )
        private val legacyComponentVariantDeclaration = DeclarationType(
            name = componentVariantDeclaration.name,
            qualifiedName = COMPONENT_VARIANT_FQN,
            function = componentVariantDeclaration.function,
        )
        private val legacyKeyframesDeclaration = DeclarationType(
            name = KEYFRAMES_SIMPLE_NAME,
            qualifiedName = KEYFRAMES_FQN,
            function = keyframesDeclaration.function,
        )
        private val declarations =
            listOf(
                cssStyleDeclaration,
                componentStyleDeclaration,
                componentVariantDeclaration,
                keyframesDeclaration,
                legacyComponentStyleDeclaration,
                legacyComponentVariantDeclaration,
                legacyKeyframesDeclaration
            )

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            val type = property.type.toString()

            val matchingByType = declarations.firstOrNull { type.startsWith(it.name) }
            if (matchingByType != null && !validateOrWarnAboutDeclaration(property, matchingByType)) {
                return
            }

            val propertyType = property.type.resolve()
            var addFileDependency = true
            when {
                types.cssStyleType.isAssignableFrom(propertyType) -> cssStyles.add(processCssStyle(property))
                types.componentStyleType.isAssignableFrom(propertyType) -> componentStyles.add(processComponentStyle(property))
                types.componentVariantType.isAssignableFrom(propertyType) -> componentVariants.add(processComponentVariant(property))
                types.keyframesType == propertyType -> keyframesList.add(processKeyframes(property))
                types.legacyComponentStyleType == propertyType -> componentStyles.add(ComponentStyleEntry(property.qualifiedName!!.asString()))
                types.legacyComponentVariantType == propertyType -> componentVariants.add(ComponentVariantEntry(property.qualifiedName!!.asString()))
                types.legacyKeyframesType == propertyType -> keyframesList.add(KeyframesEntry(property.qualifiedName!!.asString()))
                else -> addFileDependency = false
            }

            if (addFileDependency) {
                fileDependencies.add(property.containingFile!!)
            }
        }

        private fun validateOrWarnAboutDeclaration(
            property: KSPropertyDeclaration,
            declarationInfo: DeclarationType
        ): Boolean {
            val propertyName = property.simpleName.asString()
            if (property.parent !is KSFile) {
                val topLevelSuppression = "TOP_LEVEL_${declarationInfo.suppressionName}"
                if (!property.suppresses(topLevelSuppression)) {
                    logger.warn(
                        "Not registering ${declarationInfo.displayString} `val $propertyName`, as only top-level component styles are supported at this time. Although fixing this is recommended, you can manually register your ${declarationInfo.displayString} inside an @InitSilk block instead (`${declarationInfo.function}($propertyName)`). Suppress this message by adding a `@Suppress(\"$topLevelSuppression\")` annotation.",
                        property
                    )
                }
                return false
            }
            if (!property.isPublic()) {
                val privateSuppression = "PRIVATE_${declarationInfo.suppressionName}"
                if (!property.suppresses(privateSuppression)) {
                    logger.warn(
                        "Not registering ${declarationInfo.displayString} `val $propertyName`, as it is not public. Although fixing this is recommended, you can manually register your ${declarationInfo.displayString} inside an @InitSilk block instead (`${declarationInfo.function}($propertyName)`). Suppress this message by adding a `@Suppress(\"$privateSuppression\")` annotation.",
                        property
                    )
                }
                return false
            }
            if (declarationInfo.name == COMPONENT_STYLE_SIMPLE_NAME) {
                val variantKindDeclaration = property.type.resolve().arguments.first().type!!.resolve().declaration
                if (property.containingFile != variantKindDeclaration.containingFile) {
                    val kindTypeName = variantKindDeclaration.simpleName.asString()
                    logger.error(
                        "`val $propertyName = ${COMPONENT_STYLE_SIMPLE_NAME}<$kindTypeName>` is using a kind type defined in a different file, which is not allowed. Please declare a new `ComponentKind` interface in the same file and use that one, or declare your `ComponentStyle` in the same file as the `$kindTypeName` definition.",
                        property
                    )
                    return false
                }
            }

            return true
        }

        private inner class DeclarationType(
            val name: String,
            val qualifiedName: String,
            val function: String,
            val displayString: String = name.splitCamelCase().joinToString(" ") { it.lowercase() },
            val providerName: String = "${name}Provider",
            val suppressionName: String = displayString.split(" ").joinToString("_") { it.uppercase() }
        )

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }
    }

    override fun finish() {
        val (path, extension) = genFile.split('.')
        val result = getProcessorResult()
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *result.fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(Json.encodeToString<FrontendData>(result.data))
        }
    }

    /**
     * Get the finalized metadata acquired over all rounds of processing.
     *
     * This function should only be called from [SymbolProcessor.finish] as it relies on all rounds of processing being
     * complete.
     *
     * @return A [Result] containing the finalized [FrontendData] and the file dependencies that should be
     * passed in when using KSP's [CodeGenerator] to store the data.
     */
    fun getProcessorResult(): Result {
        // pages are processed here as they rely on packageMappings, which may be populated over several rounds
        val pages = pageDeclarations.mapNotNull { annotatedFun ->
            processPagesFun(
                annotatedFun = annotatedFun,
                qualifiedPagesPackage = qualifiedPagesPackage,
                packageMappings = packageMappings,
                logger = logger,
            )?.also { fileDependencies.add(annotatedFun.containingFile!!) }
        }

        // TODO: maybe this should be in the gradle plugin(s) itself to also account for library+site definitions?
        val finalRouteNames = pages.map { page -> page.route }.toSet()
        pages.forEach { page ->
            if (page.route.endsWith('/')) {
                val withoutSlashSuffix = page.route.removeSuffix("/")
                if (finalRouteNames.contains(withoutSlashSuffix)) {
                    logger.warn("Your site defines both \"$withoutSlashSuffix\" and \"${page.route}\", which may be confusing to users. Unless this was intentional, removing one or the other is suggested.")
                }
            }
        }

        val frontendData = FrontendData(
            pages,
            kobwebInits,
            silkInits,
            componentStyles,
            componentVariants,
            keyframesList,
            cssStyles,
        ).also {
            it.assertValid(throwError = { msg -> logger.error(msg) })
        }

        return Result(frontendData, fileDependencies.toList())
    }

    /**
     * Represents the result of [FrontendProcessor]'s processing, consisting of the generated [FrontendData] and the
     * files that contained relevant declarations.
     */
    data class Result(val data: FrontendData, val fileDependencies: List<KSFile>)
}

fun KSAnnotated.getAnnotationValue(fqn: String): String? {
    return this.getAnnotationsByName(fqn).firstOrNull()?.let { it.arguments.first().value.toString() }
}

/**
 * Get the CSS name for some target property, e.g. "BoldItalicStyle" -> "bold-italic"
 *
 * This method takes callbacks which further process the name transformation steps; however, neither will be called if
 * the property has a `@CssName` annotation tied to it.
 */
private fun KSPropertyDeclaration.getCssName(
    processCssName: (String) -> String = { it },
    processPropertyName: (String) -> String,
): String {
    return this.getAnnotationValue(CSS_NAME_FQN)
        ?: this.simpleName.asString().let(processPropertyName).titleCamelCaseToKebabCase().let(processCssName)
}

private fun KSAnnotated.getCssPrefix(): String? {
    return this.getAnnotationValue(CSS_PREFIX_FQN)
}

private fun String.prefixed(prefix: String?, containedName: String? = null): String {
    val self = this
    return buildString {
        prefix?.let { append("$it-") }
        containedName?.let { append("${it}_") }
        append(self)
    }
}

private fun processCssStyle(property: KSPropertyDeclaration): CssStyleEntry {
    val propertyCssName = property.getCssName { it.removeSuffix("Style") }

    fun getCssStyleEntry(prefix: String?, containedName: String? = null): CssStyleEntry {
        return CssStyleEntry(property.qualifiedName!!.asString(), propertyCssName.prefixed(prefix, containedName))
    }

    val propertyPrefix = property.getCssPrefix()

    val parent = property.parentDeclaration as? KSClassDeclaration
        ?: return getCssStyleEntry(propertyPrefix)

    val parentNameOverride = parent.getAnnotationValue(CSS_NAME_FQN)

    val containerName = if (parentNameOverride != null) {
        parentNameOverride
    } else {
        val relevantParent = if (parent.isCompanionObject) parent.parentDeclaration!! else parent
        relevantParent.simpleName.asString().titleCamelCaseToKebabCase()
    }

    return getCssStyleEntry(propertyPrefix ?: parent.getCssPrefix(), containerName)
}

fun processComponentStyle(property: KSPropertyDeclaration): ComponentStyleEntry {
    val propertyCssName = property.getCssName { it.removeSuffix("Style") }

    fun getComponentStyleEntry(prefix: String?): ComponentStyleEntry {
        return ComponentStyleEntry(property.qualifiedName!!.asString(), propertyCssName.prefixed(prefix))
    }

    val propertyPrefix = property.getCssPrefix()

    return getComponentStyleEntry(propertyPrefix)
}

fun processComponentVariant(property: KSPropertyDeclaration): ComponentVariantEntry {
    // Remove the kind part from the variant name, e.g. "val RedButtonVariant = ComponentVariant<ButtonKind>" -> "red"
    // (after removing "button"). In this way, we can append the variant on top of the style without repeating the kind,
    // e.g. "button-red" and not "button-button-red"
    val variantKindName = run {
        val variantKindType = property.type.resolve().arguments.first().type!!.resolve()
        val variantKindFqn = variantKindType.declaration.qualifiedName!!.asString()
        variantKindFqn.substringAfterLast(".").removeSuffix("Kind")
    }

    val propertyCssName = property.getCssName(
        processCssName = { "-$it" }, // Indicate the variant should extend the base style name
    ) { propertyName ->
        val withoutVariantSuffix = propertyName.removeSuffix("Variant")
        // Given a variant of kind "ExampleKind" (which gets stripped to just "Example"), we want to support the
        // following variant name simplifications:
        // - "OutlinedExampleVariant" -> "outlined" // Preferred variant naming style
        // - "ExampleOutlinedVariant" -> "outlined" // Acceptable variant naming style
        // - "OutlinedVariant"        -> "outlined" // But really the user should have kept "Example" in the name
        // - "ExampleVariant"         -> "example" // In other words, protect against empty strings!
        withoutVariantSuffix.removePrefix(variantKindName).removeSuffix(variantKindName).takeIf { it.isNotEmpty() }
            ?: withoutVariantSuffix
    }

    fun getComponentVariantEntry(prefix: String?): ComponentVariantEntry {
        return ComponentVariantEntry(property.qualifiedName!!.asString(), propertyCssName.prefixed(prefix))
    }

    val propertyPrefix = property.getCssPrefix()

    return getComponentVariantEntry(propertyPrefix)
}

fun processKeyframes(property: KSPropertyDeclaration): KeyframesEntry {
    val propertyCssName = property.getCssName {
        it.removeSuffix("Anim")
            .removeSuffix("Animation")
            .removeSuffix("Keyframes")
    }

    fun getKeyframesEntry(prefix: String?): KeyframesEntry {
        return KeyframesEntry(property.qualifiedName!!.asString(), propertyCssName.prefixed(prefix))
    }

    val propertyPrefix = property.getCssPrefix()
    return getKeyframesEntry(propertyPrefix)
}


/**
 * Convert a String for a name that is using TitleCamelCase into kebab-case.
 *
 * For example, "ExampleText" to "example-text"
 *
 * Same as [camelCaseToKebabCase], there is special handling for acronyms. See those docs for examples.
 */
// Note: There's really no difference between title case and camel case when going to kebab case, but both are
// provided for symmetry with the reverse methods, and also for expressing intention clearly.
// Note 2: Copied from frontend/browser-ext/.../util/StringExtensions.kt
fun String.titleCamelCaseToKebabCase() = camelCaseToKebabCase()
