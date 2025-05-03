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
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Variance
import com.varabyte.kobweb.common.text.camelCaseToKebabCase
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.ksp.common.APP_FQN
import com.varabyte.kobweb.ksp.common.CSS_KIND_COMPONENT_FQN
import com.varabyte.kobweb.ksp.common.CSS_KIND_RESTRICTED_FQN
import com.varabyte.kobweb.ksp.common.CSS_LAYER_FQN
import com.varabyte.kobweb.ksp.common.CSS_NAME_FQN
import com.varabyte.kobweb.ksp.common.CSS_PREFIX_FQN
import com.varabyte.kobweb.ksp.common.CSS_STYLE_FQN
import com.varabyte.kobweb.ksp.common.CSS_STYLE_VARIANT_FQN
import com.varabyte.kobweb.ksp.common.INIT_KOBWEB_FQN
import com.varabyte.kobweb.ksp.common.INIT_ROUTE_FQN
import com.varabyte.kobweb.ksp.common.INIT_SILK_FQN
import com.varabyte.kobweb.ksp.common.KEYFRAMES_FQN
import com.varabyte.kobweb.ksp.common.LAYOUT_FQN
import com.varabyte.kobweb.ksp.common.NO_LAYOUT_FQN
import com.varabyte.kobweb.ksp.common.PACKAGE_MAPPING_PAGE_FQN
import com.varabyte.kobweb.ksp.common.PAGE_CONTEXT_FQN
import com.varabyte.kobweb.ksp.common.PAGE_FQN
import com.varabyte.kobweb.ksp.common.getDefaultLayout
import com.varabyte.kobweb.ksp.common.getPackageMappings
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.symbol.suppresses
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobweb.project.frontend.CssStyleEntry
import com.varabyte.kobweb.project.frontend.CssStyleVariantEntry
import com.varabyte.kobweb.project.frontend.FrontendData
import com.varabyte.kobweb.project.frontend.InitKobwebEntry
import com.varabyte.kobweb.project.frontend.InitSilkEntry
import com.varabyte.kobweb.project.frontend.KeyframesEntry
import com.varabyte.kobweb.project.frontend.LayoutEntry
import com.varabyte.kobweb.project.frontend.assertValid
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(KspExperimental::class)
class FrontendProcessor(
    private val isLibrary: Boolean,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    private val projectGroup: String,
    pagesPackage: String,
    private val defaultCssPrefix: String? = null,
) : SymbolProcessor {
    private val pageDeclarations = mutableListOf<KSFunctionDeclaration>()
    private val initRouteDeclarations = mutableListOf<KSFunctionDeclaration>()

    private val qualifiedPagesPackage = PackageUtils.resolvePackageShortcut(projectGroup, pagesPackage)

    private val layoutMethods = mutableSetOf<KSFunctionDeclaration>()
    private val noLayoutsFor = mutableSetOf<KSFunctionDeclaration>()
    private val layoutsFor = mutableMapOf<KSFunctionDeclaration, KSFunctionDeclaration>()
    // fqPkg to layout fqn, e.g. "pages.blog" to "com.example.components.layouts.BlogLayout"
    // sorted by length (descending), so more specific packages will be checked before less specific ones
    private val defaultLayouts = mutableMapOf<String, KSFunctionDeclaration>().toSortedMap(compareBy({ -it.length }, { it }))
    private val kobwebInits = mutableListOf<InitKobwebEntry>()
    private val silkInits = mutableListOf<InitSilkEntry>()
    private val cssStyles = mutableListOf<CssStyleEntry>()
    private val cssStyleVariants = mutableListOf<CssStyleVariantEntry>()
    private val keyframesList = mutableListOf<KeyframesEntry>()
    // fqPkg to subdir, e.g. "blog._2022._01" to "01"
    private val packageMappings = mutableMapOf<String, String>()

    // We track all files we depend on so that ksp can perform smart recompilation
    // Even though our output is aggregating so generally requires full reprocessing, this at minimum means processing
    // will be skipped if the only change is deleted file(s) that we do not depend on.
    private val fileDependencies = mutableSetOf<KSFile>()

    private fun KSFunctionDeclaration.hasPageAnnotation(): Boolean {
        return getAnnotationsByName(PAGE_FQN).toList().isNotEmpty()
    }

    private fun KSFunctionDeclaration.hasLayoutAnnotation(): Boolean {
        return getAnnotationsByName(LAYOUT_FQN).toList().isNotEmpty()
    }

    private fun KSFunctionDeclaration.isLayoutMethod(resolver: Resolver): Boolean {
        if (!hasLayoutAnnotation()) return false
        // @Layout defined on a @Page means the method is a page, not a layout
        if (hasPageAnnotation()) return false

        val pageContextName = resolver.getKSNameFromString(PAGE_CONTEXT_FQN)

        // A valid layout method takes an optional first parameter of type `PageContext` and a final
        // parameter of type `@Composable () -> Unit`
        return (
            ((parameters.size == 2 && parameters[0].type.resolve().declaration.qualifiedName == pageContextName)
                || parameters.size == 1)
                && parameters.last().type.resolve().declaration.qualifiedName!!.asString() == "kotlin.Function0"
            )
    }

    /**
     * Assert that the target [layoutMethodName] is a valid layout method, or log an error (and return null) if not.
     */
    private fun Resolver.checkLayoutMethod(context: KSNode, layoutMethodName: KSName): KSFunctionDeclaration? {
        val matchesByName = getFunctionDeclarationsByName(layoutMethodName, includeTopLevel = true)
        return matchesByName
            .singleOrNull { it.isLayoutMethod(this) }
            .also { layoutMethod ->
                if (layoutMethod == null) {
                    logger.error(buildString {
                        append("@Layout(\"${layoutMethodName.asString()}\") does not reference a valid layout method.")

                        if (matchesByName.all { !it.hasLayoutAnnotation() }) {
                            append(" Did you forget to add the @Layout annotation on the target method?")
                        } else if (matchesByName.any()) {
                            append(" Please check that the @Layout method takes the expected arguments (optionally, a PageContext, and a composable lambda).")
                        } else {
                            append(" No methods with that name were found. Please check the spelling.")
                        }
                    }, context)
                }
            }
    }
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Layout annotated methods are not (necessarily) layout methods themselves; but they point at layout methods.
        resolver.getSymbolsWithAnnotation(LAYOUT_FQN)
            // Ignore layout annotations attached to files
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.hasPageAnnotation() || it.isLayoutMethod(resolver) }
            .forEach { annotatedFun ->
                fileDependencies.add(annotatedFun.containingFile!!)

                if (!annotatedFun.hasPageAnnotation()) {
                    if (annotatedFun.annotations.none { it.shortName.asString() == "Composable" }) {
                        logger.warn("`fun ${annotatedFun.qualifiedName!!.asString()}` annotated with @Layout must also be @Composable.", annotatedFun)
                    }

                    layoutMethods += annotatedFun
                }

                annotatedFun.getAnnotationsByName(LAYOUT_FQN).first().arguments.first().value?.toString().orEmpty()
                    .let { layoutFqn ->
                        // Users should NOT put the NO_LAYOUT_FQN in their layout, but we catch it here anyway to avoid
                        // showing them a potentially confusing error from the else branch. However, we should not
                        // advertise they can do this, and we reserve the right to turn this into a warning / error
                        // later.
                        if (layoutFqn.isBlank() || layoutFqn == NO_LAYOUT_FQN) {
                            noLayoutsFor.add(annotatedFun)
                        } else {
                            val resolvedFqn = PackageUtils.resolvePackageShortcut(projectGroup, layoutFqn)
                            val layoutParent = resolver.getKSNameFromString(resolvedFqn)
                            resolver.checkLayoutMethod(annotatedFun, layoutParent)?.let { layoutMethod ->
                                layoutsFor[annotatedFun] = layoutMethod
                            }
                        }
                    }
            }

        resolver.getSymbolsWithAnnotation(NO_LAYOUT_FQN)
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.hasPageAnnotation() } // Only pages support `@NoLayout`
            .forEach { annotatedFun -> noLayoutsFor.add(annotatedFun) }

        noLayoutsFor.forEach { pageMethod ->
            if (layoutsFor.containsKey(pageMethod)) {
                logger.error("`${pageMethod.qualifiedName!!.asString()}` is tagged with both conflicting @NoLayout and @Layout annotations. Please remove one of them.", pageMethod)
            }
        }

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

        if (isLibrary) {
            resolver.getSymbolsWithAnnotation(APP_FQN).toList().forEach { appMethod ->
                logger.error("@App functions cannot be defined in library projects.", appMethod)
            }
        }

        // only process files that are new to this round, since prior declarations are unchanged

        val silkVisitor = run {
            // It's rare but some projects may just use Kobweb core for routing and no silk at all
            // Here, we prevent KSP from crashing trying to resolve types that won't be found.
            val hasSilkDependency = resolver.getKotlinClassByName(CSS_STYLE_FQN) != null
            if (hasSilkDependency) {
                SilkVisitor(resolver)
            } else null
        }
        resolver.getNewFiles().forEach { file ->
            silkVisitor?.let { file.accept(it, Unit) }

            getDefaultLayout(
                file,
                projectGroup,
                qualifiedPagesPackage,
                LAYOUT_FQN,
                logger
            )?.let { (pkg, layoutFqn) ->
                resolver.checkLayoutMethod(file, resolver.getKSNameFromString(layoutFqn))?.let { layoutMethod ->
                    defaultLayouts[pkg] = layoutMethod
                    fileDependencies.add(file)
                }
            }

            packageMappings += getPackageMappings(
                file,
                qualifiedPagesPackage,
                PACKAGE_MAPPING_PAGE_FQN,
                logger
            ).toMap()
                .also { if (it.isNotEmpty()) fileDependencies.add(file) }
        }

        pageDeclarations += resolver.getSymbolsWithAnnotation(PAGE_FQN).map { it as KSFunctionDeclaration }
            .also { pages ->
                pages
                    .filter { page -> page.hasLayoutAnnotation() }
                    .map { page -> page to page.getAnnotationsByName(LAYOUT_FQN).first() }
                    .filter { (_, layout) -> layout.arguments.first().value?.toString().orEmpty().isEmpty() }
                    .forEach { (page, layout) ->
                        logger.error(
                            "@Page method \"${page.qualifiedName!!.asString()}\" cannot apply a `@Layout` annotation without specifying a target path. Please add a valid layout path to the layout annotation or remove it.",
                            layout
                        )
                    }
            }

        initRouteDeclarations += resolver.getSymbolsWithAnnotation(INIT_ROUTE_FQN).map { it as KSFunctionDeclaration }
            .also { initRouteDeclarations ->
                initRouteDeclarations.forEach { initRouteMethod ->
                    val file = initRouteMethod.containingFile!!
                    val foundPageOrLayout = file.declarations.any { decl ->
                        decl is KSFunctionDeclaration && decl !== initRouteMethod && (decl.hasPageAnnotation() || decl.hasLayoutAnnotation())
                    }

                    if (!foundPageOrLayout) {
                        logger.error(
                            "@InitRoute method \"${initRouteMethod.qualifiedName!!.asString()}\" must be defined in the same file as a @Page or @Layout method.",
                            initRouteMethod
                        )
                    }
                }
            }
        

        return emptyList()
    }

    private class SilkTypes(resolver: Resolver) {
        private val cssStyleClassDeclaration = resolver.getKotlinClassByName(CSS_STYLE_FQN)!!

        val cssStyleType = cssStyleClassDeclaration.asStarProjectedType()
        val cssStyleVariantType = resolver.getKotlinClassByName(CSS_STYLE_VARIANT_FQN)!!.asStarProjectedType()
        val keyframesType = resolver.getKotlinClassByName(KEYFRAMES_FQN)!!.asType(emptyList())

        val componentKindType = resolver.getKotlinClassByName(CSS_KIND_COMPONENT_FQN)!!.asType(emptyList())
        val restrictedKindType = resolver.getKotlinClassByName(CSS_KIND_RESTRICTED_FQN)!!.asType(emptyList())

        // Not CssStyle<*> but CssStyle<out ComponentKind> specifically.
        val cssStyleComponentKindType = run {
            val typeRef = resolver.createKSTypeReferenceFromKSType(componentKindType)
            val typeArg = resolver.getTypeArgument(typeRef, Variance.COVARIANT)
            cssStyleClassDeclaration.asType(listOf(typeArg))
        }
    }

    /**
     * Search the user's code for references to silk types and generate metadata for them
     */
    private inner class SilkVisitor(resolver: Resolver) : KSVisitorVoid() {
        private val types = SilkTypes(resolver)
        private val declarations = run {
            val cssStyleDeclaration = DeclarationType(
                types.cssStyleType,
                "ctx.theme.registerStyle",
            )
            val cssStyleVariantDeclaration = DeclarationType(
                types.cssStyleVariantType,
                "ctx.theme.registerVariant",
            )
            val keyframesDeclaration = DeclarationType(
                types.keyframesType,
                "ctx.stylesheet.registerKeyframes",
            )

            listOf(
                cssStyleDeclaration,
                cssStyleVariantDeclaration,
                keyframesDeclaration,
            )
        }

        // Map of KindType to the CssStyle property it is associated with
        // e.g. `val ExampleStyle = CssStyle<ExampleKind>`: ExampleKind -> ExampleStyle
        // There should only ever be one style per component kind.
        // This cache only has to be valid over the lifetime of visiting a single file, since kinds and styles are
        // enforced to be declared in the same file.
        private val consumedComponentKinds = mutableMapOf<KSType, KSPropertyDeclaration>()

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

        private fun KSAnnotated.getCssPrefix(): String? = listOf(this).getCssPrefix()

        // The order of the list should be in the order of precedence, with the first element being the highest
        // precedence. So, property first, then container, for example.
        private fun List<KSAnnotated>.getCssPrefix(): String? {
            return (
                this.firstNotNullOfOrNull { it.getAnnotationValue(CSS_PREFIX_FQN) } ?: defaultCssPrefix
                )?.takeIf { it.isNotEmpty() } // If the CssPrefix annotation is set to "", that should disable the prefix
        }

        private fun String.prefixed(prefix: String?, containedName: String? = null): String {
            val self = this
            return buildString {
                prefix?.let { append("$it-") }
                containedName?.let { append("${it}_") }
                append(self)
            }
        }

        /**
         * Given a class context which represents an object or a companion object, return the relevant container class.
         *
         * More specifically, the class is an object, then return it. Otherwise, if a companion object, return the
         * parent class itself.
         *
         * This is useful because we fetch the name from this container class and use it to namespace the CSS style
         * name.
         */
        private fun KSClassDeclaration.asSingletonContainer(): KSClassDeclaration? {
            return when {
                this.isCompanionObject -> this.parentDeclaration as KSClassDeclaration
                this.classKind == ClassKind.OBJECT -> this
                else -> null
            }
        }

        private val KSNode.owningSingletonClassDeclaration: KSClassDeclaration?
            get() = (parent as? KSClassDeclaration)?.asSingletonContainer()

        /**
         * Returning the relevant singleton container for an extension property.
         *
         * Although expected to be rare, we support code like this:
         *
         * ```
         * //--------------------------------
         * package com.somelib.styles
         *
         * object SomeLibStyling { ... }
         *
         * //--------------------------------
         * package com.example
         *
         * import com.somelib.styles.*
         * import com.varabyte.kobweb.silk.components.forms.ButtonSize
         *
         * private val _XXL = ButtonSize(...)
         * val ButtonSize.Companion.XXL get() = _XXL // container name = "button-size"
         *
         * private val _MyVariant = SomeLibStyle.addVariant { ... }
         * val SomeLibStyling.MyVariant get() = _MyVariant // container name = "some-lib-styling"
         * ```
         *
         * Note that callers that reference such an extension object will need to include appropriate imports in the
         * final generated code, because Kotlin requires that to use extension methods.
         *
         * For example, for the XXL property above, the final generated code should look like this:
         *
         * ```
         * import com.example.XXL
         *
         * registerStyle("button-size_xxl", com.varabyte.kobweb.silk.components.forms.ButtonSize.XXL)
         * ```
         */
        private val KSPropertyDeclaration.receiverSingletonClassDeclaration: KSClassDeclaration?
            get() {
                val receiverTypeReference = this.extensionReceiver ?: return null
                val receiverType = receiverTypeReference.resolve()
                val receiverClassDeclaration = (receiverType.declaration as? KSClassDeclaration) ?: return null
                return receiverClassDeclaration.asSingletonContainer()
            }

        private fun KSPropertyDeclaration.findCssPrefixAndContainerName(parentSingleton: KSClassDeclaration?): Pair<String?, String?> {
            if (parentSingleton == null) return this.getCssPrefix() to null

            val containerName =
                parentSingleton.getAnnotationValue(CSS_NAME_FQN)
                    ?: parentSingleton.simpleName.asString().titleCamelCaseToKebabCase()

            return listOf(this, parentSingleton).getCssPrefix() to containerName
        }

        // Common functionality that generate data useful for all cases that need to create metadata entries
        private inline fun <T> KSPropertyDeclaration.createEntry(callback: (import: String?, fqcn: String, prefix: String?, containerName: String?) -> T): T {
            val property = this // for readability
            property.receiverSingletonClassDeclaration?.let { receiverSingleton ->
                property.findCssPrefixAndContainerName(receiverSingleton).let { (prefix, containerName) ->
                    return callback(
                        property.qualifiedName!!.asString(),
                        "${receiverSingleton.qualifiedName!!.asString()}.${property.simpleName.asString()}",
                        prefix,
                        containerName
                    )
                }
            }

            property.findCssPrefixAndContainerName(property.owningSingletonClassDeclaration)
                .let { (prefix, containerName) ->
                    return callback(
                        null,
                        property.qualifiedName!!.asString(),
                        prefix, containerName
                    )
                }
        }

        private fun processCssStyle(property: KSPropertyDeclaration): CssStyleEntry {
            val propertyCssName = property.getCssName { it.removeSuffix("Style") }
            return property.createEntry { import, fqcn, prefix, containerName ->
                CssStyleEntry(
                    import = import,
                    fqcn = fqcn,
                    name = propertyCssName.prefixed(prefix, containerName),
                    layer = property.getAnnotationValue(CSS_LAYER_FQN)
                )
            }
        }

        fun processCssStyleVariant(property: KSPropertyDeclaration): CssStyleVariantEntry {
            // Remove the kind part from the variant name, e.g. "val RedButtonVariant = CssStyleVariant<ButtonKind>" -> "red"
            // (after removing "button"). In this way, we can append the variant on top of the style without repeating the kind,
            // e.g. "button-red" and not "button-button-red"
            val variantKindName = buildString {
                // Note that we support nested component kinds, e.g.
                // sealed interface SwitchKind : ComponentKind {
                //    sealed interface Track : ComponentKind
                //    sealed interface Thumb : ComponentKind
                // }
                // which should generate three kind names: "Switch", "SwitchTrack", and "SwitchThumb"
                var variantKindType: KSType? = property.type.resolve().arguments.first().type!!.resolve()
                while (variantKindType != null) {
                    insert(
                        0,
                        variantKindType.declaration.simpleName.asString()
                            .removeSuffix("ComponentKind").removeSuffix("Kind")
                    )
                    variantKindType = (variantKindType.declaration.parentDeclaration as? KSClassDeclaration)
                        ?.takeIf { types.componentKindType.isAssignableFrom(it.asType(emptyList())) }
                        ?.asType(emptyList())
                }
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
                withoutVariantSuffix.removePrefix(variantKindName).removeSuffix(variantKindName)
                    .takeIf { it.isNotEmpty() }
                    ?: withoutVariantSuffix
            }

            // If extending a base style name, then we don't need to check anything else for the prefix or container
            // name, as we'll just use what the style itself is using.
            val isRelativeCssName = propertyCssName.startsWith('-')
            return property.createEntry { import, fqcn, prefix, containerName ->
                CssStyleVariantEntry(
                    import = import,
                    fqcn = fqcn,
                    name = if (isRelativeCssName) propertyCssName else propertyCssName.prefixed(prefix, containerName),
                    layer = property.getAnnotationValue(CSS_LAYER_FQN)
                )
            }
        }

        fun processKeyframes(property: KSPropertyDeclaration): KeyframesEntry {
            val propertyCssName = property.getCssName {
                it.removeSuffix("Anim")
                    .removeSuffix("Animation")
                    .removeSuffix("Keyframes")
            }

            return property.createEntry { import, fqcn, prefix, containerName ->
                KeyframesEntry(
                    import = import,
                    fqcn = fqcn,
                    name = propertyCssName.prefixed(prefix, containerName)
                )
            }
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            val propertyType = property.type.resolve()
            // If "qualifiedName" is ever null, we will definitely crash later, so just abort now. This can happen due
            // to temporarily unresolvable code, e.g. something that might have produced a compile error; however, KSP
            // is throwing the error first, which hides the real issue.
            if (propertyType.declaration.qualifiedName == null) return
            if (propertyType.declaration.qualifiedName!!.asString() == "kotlin.Any") {
                // In Kotlin/JS, the dynamic type is showing up as "Any" is KSP. However, if we let it passed here, it
                // ends up returning true on a bunch of our type checks. So abort early; we never need to support "Any"
                // anyway. There is a `KSDynamicReference` class in KSP, but I can't figure out how to check it here.
                // To hit this case yourself, you can declare `val test = js("123")` as a top level property.
                return
            }

            val matchingByType = declarations.firstOrNull { it.type.isAssignableFrom(propertyType) } ?: return
            if (!validateOrWarnAboutDeclaration(property, matchingByType)) {
                return
            }

            when (matchingByType.type) {
                types.cssStyleType -> cssStyles.add(processCssStyle(property))
                types.cssStyleVariantType -> cssStyleVariants.add(processCssStyleVariant(property))
                types.keyframesType -> keyframesList.add(processKeyframes(property))
            }
            fileDependencies.add(property.containingFile!!)
        }

        private fun validateOrWarnAboutDeclaration(
            property: KSPropertyDeclaration,
            declarationInfo: DeclarationType
        ): Boolean {
            val propertyName = property.simpleName.asString()
            if (property.parent !is KSFile && (property.owningSingletonClassDeclaration == null)) {
                val localSuppression = "LOCAL_${declarationInfo.suppressionName}"
                if (!property.suppresses(localSuppression)) {
                    logger.warn(
                        "Not registering ${declarationInfo.displayString} `val $propertyName`, as local style block declarations are not supported. You can only declare them globally, either at a top level or inside an object singleton. Although fixing this is recommended, you can manually register your ${declarationInfo.displayString} inside an @InitSilk block instead (`${declarationInfo.function}($propertyName)`). Suppress this message by adding a `@Suppress(\"$localSuppression\")` annotation.",
                        property
                    )
                }
                return false
            }
            if (!property.isPublic()) {
                // If this looks like a backing property, ignore it! We expect another property to follow up soon
                // after that will be public. For example:
                // private val _XXL = ButtonSize(...)
                // val ButtonSize.Companion.XXL: ButtonSize get() = _XXL
                if (propertyName.startsWith('_')) return false

                val privateSuppression = "PRIVATE_${declarationInfo.suppressionName}"
                if (!property.suppresses(privateSuppression)) {
                    logger.warn(
                        "Not registering ${declarationInfo.displayString} `val $propertyName`, as it is not public. Although fixing this is recommended, you can manually register your ${declarationInfo.displayString} inside an @InitSilk block instead (`${declarationInfo.function}($propertyName)`). Suppress this message by adding a `@Suppress(\"$privateSuppression\")` annotation or prepending the name with an underscore.",
                        property
                    )
                }
                return false
            }
            if (declarationInfo.type == types.cssStyleType) {
                // If a CssStyle is associated with a ComponentKind, make sure it was declared in the same file as the
                // kind.
                //
                // For example, in:
                // ```
                // sealed interface ButtonKind : ComponentKind
                // val ButtonStyle = CssStyle<ButtonKind>
                // ```
                // we are checking that ButtonKind and the ButtonStyle property live near each other.
                // You can think of this as a poor man's sealed class kind of implementation.

                val propertyType = property.type.resolve()
                if (!types.cssStyleComponentKindType.isAssignableFrom(propertyType)) {
                    // This CssStyle implementation is either a CssStyle subclass of CssStyle.Base or, more likely, it
                    // was created by a non-typed `CssStyle { ... }` block. These cases are safe.
                    return true
                }

                // If here, we're sure we have a CssStyle<ComponentKind>. Make sure they're in the same file
                val variantKindType = propertyType.arguments.first().type!!.resolve()
                val variantKindDeclaration = variantKindType.declaration
                val kindTypeName = variantKindDeclaration.simpleName.asString()

                (consumedComponentKinds.putIfAbsent(variantKindType, property))?.let { previousProperty ->
                    val previousPropertyName = previousProperty.simpleName.asString()
                    logger.error(
                        "`${kindTypeName}` can only be associated with a single CSS style. `val $propertyName = ${declarationInfo.name}<$kindTypeName>` was declared after `val $previousPropertyName = ${declarationInfo.name}<$kindTypeName>`. Either remove one of these style declarations, or create a new `${types.componentKindType.declaration.simpleName.asString()}` type for one of them.",
                        property
                    )
                    return false
                }

                if (property.containingFile != variantKindDeclaration.containingFile) {
                    logger.error(
                        "`val $propertyName = ${declarationInfo.name}<$kindTypeName>` is using a kind type defined in a different file, which is not allowed. Please declare a new `${types.componentKindType.declaration.simpleName.asString()}` implementation in the same file and use that one, or declare your `${declarationInfo.name}<$kindTypeName>` in the same file as the `$kindTypeName` definition.",
                        property
                    )
                    return false
                }
            }

            return true
        }

        private inner class DeclarationType(
            val type: KSType,
            val function: String,
        ) {
            val name = type.declaration.simpleName.asString()
            val displayString: String = name.splitCamelCase().joinToString(" ") { it.lowercase() }
            val suppressionName: String = displayString.split(" ").joinToString("_") { it.uppercase() }
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
            consumedComponentKinds.clear()
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

    private fun reportErrorIfLayoutCycleDetected() {
        // Detect cycles in layout dependencies
        fun detectLayoutCycle(
            decl: KSFunctionDeclaration,
            path: MutableList<KSFunctionDeclaration> = mutableListOf(),
            errorCtx: KSFunctionDeclaration = decl
        ): Boolean {
            path.add(decl)
            if (path.size > 1) {
                if (path.dropLast(1).any { it == decl }) {
                    if (path.size == 2) {
                        logger.error(
                            "Layout \"${decl.simpleName.asString()}\" has a circular dependency with itself.",
                            errorCtx
                        )
                    } else {
                        // Only report the error if we are the start and end of the cycle; otherwise, just wait, because
                        // the other layout will trigger this case as well and report the error itself.
                        if (path.last() == errorCtx) {
                            logger.error(
                                "Circular dependency detected: ${path.joinToString(" -> ") { "\"${it.simpleName.asString()}\"" }}",
                                errorCtx
                            )
                        }
                    }
                    return true
                }
            }

            return layoutsFor[decl]?.let { parentLayout ->
                val hasChildCycle = detectLayoutCycle(layoutsFor[decl]!!, path, errorCtx)
                path.removeLast()
                hasChildCycle
            } ?: false
        }

        // Check for cycles starting from each layout
        layoutsFor.keys.forEach { layout -> detectLayoutCycle(layout) }
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
        reportErrorIfLayoutCycleDetected()

        // Use filePath instead of containingFile, because equality doesn't seem to work as expected for KSFile
        val initRoutesMap = initRouteDeclarations.associateBy { it.containingFile!!.filePath }
        val layouts = run {
            layoutMethods.map { layout ->
                LayoutEntry(
                    layout.qualifiedName!!.asString(),
                    acceptsContext = layout.parameters.size == 2,
                    parentLayoutFqn = layoutsFor[layout]?.qualifiedName?.asString(),
                    initRouteFqn = initRoutesMap[layout.containingFile!!.filePath]?.qualifiedName?.asString()
                )
            }
        }

        val pages = pageDeclarations.mapNotNull { annotatedFun ->
            processPagesFun(
                annotatedFun = annotatedFun,
                layoutFqn = if (noLayoutsFor.contains(annotatedFun)) NO_LAYOUT_FQN else (layoutsFor[annotatedFun]?.qualifiedName?.asString()
                    ?: defaultLayouts.asSequence().mapNotNull { (pkg, layoutMethod) ->
                        layoutMethod.takeIf { annotatedFun.packageName.asString().startsWith(pkg) }
                    }.firstOrNull()?.qualifiedName?.asString()),
                initRoutes = initRoutesMap,
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
            layouts,
            pages,
            kobwebInits,
            silkInits,
            keyframesList,
            cssStyles,
            cssStyleVariants,
        ).also {
            it.assertValid(throwError = { msg -> logger.error(msg) })
        }

        return Result(frontendData, fileDependencies)
    }

    /**
     * Represents the result of [FrontendProcessor]'s processing, consisting of the generated [FrontendData] and the
     * files that contained relevant declarations.
     */
    data class Result(val data: FrontendData, val fileDependencies: Set<KSFile>)
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
