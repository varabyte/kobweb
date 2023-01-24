package com.varabyte.kobweb.gradle.core.processors

import com.varabyte.kobweb.gradle.core.project.common.PackageUtils.prefixQualifiedPath
import com.varabyte.kobweb.gradle.core.project.common.RouteUtils
import com.varabyte.kobweb.gradle.core.project.common.getStringValue
import com.varabyte.kobweb.gradle.core.project.frontend.*
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.gradle.core.util.visitAllChildren
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import java.io.File

private fun processComponentStyle(
    file: File,
    filePackage: String,
    property: KtProperty,
    styleName: String,
    silkStyles: MutableList<ComponentStyleEntry>,
    reporter: Reporter
): Boolean {
    val propertyName = property.name ?: return false // Null name not expected in practice; fail silently

    // Only top-level properties are allowed for now, so getting the fully qualified path is easy
    if (property.parent !is KtFile) {
        reporter.report("${file.absolutePath}: Not registering component style `val $propertyName`, as only top-level component styles are supported at this time")
        return false
    }

    return if (property.isPublic) {
        silkStyles.add(ComponentStyleEntry(prefixQualifiedPath(filePackage, propertyName), styleName))
        true
    } else {
        reporter.report("${file.absolutePath}: Not registering component style `val $propertyName`, as it is not public")
        false
    }
}

private fun String.elideLinesAfterFirst(): String {
    val lines = this.split("\n")
    return if (lines.size > 1) "${lines.first()}..." else this
}

/** Process a line like `val CustomStyle = ComponentStyle("custom") { ... }` */
private fun processComponentStyle(
    file: File,
    filePackage: String,
    element: KtCallExpression,
    silkStyles: MutableList<ComponentStyleEntry>,
    reporter: Reporter
): Boolean {
    val property = element.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val styleName = element.getStringValue(0) ?: run {
        reporter.report("${file.absolutePath}: Skipping over \"${element.text.elideLinesAfterFirst()}\" as the style name must be specified as a raw string.")
        return false
    }
    return processComponentStyle(file, filePackage, property, styleName, silkStyles, reporter)
}

/** Process a line like `val CustomStyle = ComponentStyle.base("custom") { ... }` */
private fun processComponentStyleBase(
    file: File,
    filePackage: String,
    element: KtCallExpression,
    silkStyles: MutableList<ComponentStyleEntry>,
    reporter: Reporter
): Boolean {
    val qualifiedExpression = element.parent as? KtDotQualifiedExpression ?: return false
    if (qualifiedExpression.receiverExpression.text != "ComponentStyle") return false
    val property = qualifiedExpression.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val styleName = element.getStringValue(0) ?: run {
        reporter.report("${file.absolutePath}: Skipping over \"${qualifiedExpression.text.elideLinesAfterFirst()}\" as the style name must be specified as a raw string.")
        return false
    }
    return processComponentStyle(file, filePackage, property, styleName, silkStyles, reporter)
}

private fun processComponentVariant(
    file: File,
    filePackage: String,
    property: KtProperty,
    variantName: String,
    silkVariants: MutableList<ComponentVariantEntry>,
    reporter: Reporter
): Boolean {
    val propertyName = property.name ?: return false

    // Only top-level properties are allowed for now, so getting the fully qualified path is easy
    if (property.parent !is KtFile) {
        reporter.report("${file.absolutePath}: Not registering component variant `val $propertyName`, as only top-level component variants are supported at this time")
        return false
    }

    return if (property.isPublic) {
        silkVariants.add(ComponentVariantEntry(prefixQualifiedPath(filePackage, propertyName), variantName))
        true
    } else {
        reporter.report("${file.absolutePath}: Not registering component variant `val $propertyName`, as it is not public")
        false
    }
}


/** Process a line like `val CustomVariant = CustomStyle.addVariant("variant") { ... }` */
private fun processComponentVariant(
    file: File,
    filePackage: String,
    element: KtCallExpression,
    silkVariants: MutableList<ComponentVariantEntry>,
    reporter: Reporter
): Boolean {
    val qualifiedExpression = element.parent as? KtDotQualifiedExpression ?: return false
    val property = qualifiedExpression.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val variantName = element.getStringValue(0) ?: run {
        reporter.report("${file.absolutePath}: Skipping over \"${qualifiedExpression.text.elideLinesAfterFirst()}\" as the variant name must be specified as a raw string.")
        return false
    }
    return processComponentVariant(file, filePackage, property, variantName, silkVariants, reporter)
}

/** Process a line like `val CustomVariant = CustomStyle.addVariantBase("variant") { ... }` */
private fun processComponentVariantBase(
    file: File,
    filePackage: String,
    element: KtCallExpression,
    silkVariants: MutableList<ComponentVariantEntry>,
    reporter: Reporter
): Boolean {
    // Despite the different method name, processing `addVariantBase` is the same as processing `addVariant`
    return processComponentVariant(file, filePackage, element, silkVariants, reporter)
}

/** Process a line like `val AnimName = Keyframes("name") { ... }` */
private fun processKeyframes(
    file: File,
    filePackage: String,
    element: KtCallExpression,
    keyframesList: MutableList<KeyframesEntry>,
    reporter: Reporter
): Boolean {
    val property = element.parents.filterIsInstance<KtProperty>().firstOrNull() ?: return false
    val propertyName = property.name ?: return false // Null name not expected in practice; fail silently

    // Only top-level properties are allowed for now, so getting the fully qualified path is easy
    if (property.parent !is KtFile) {
        reporter.report("${file.absolutePath}: Not registering keyframes definition `val $propertyName`, as only top-level definitions are supported at this time")
        return false
    }

    return if (property.isPublic) {
        keyframesList.add(KeyframesEntry(prefixQualifiedPath(filePackage, propertyName)))
        true
    } else {
        reporter.report("${file.absolutePath}: Not registering keyframes definition `val $propertyName`, as it is not public")
        false
    }
}

/**
 * A processor that runs over a site's frontend code, searching for Kobweb hooks like `@Page` annotations and
 * component styles.
 *
 * See also: [FrontendData].
 *
 * @param qualifiedPagesPackage The fully qualified path to the root of the pages folder. For example,
 *   "com.site.pages"
 */
class FrontendDataProcessor(
    private val reporter: Reporter,
    private val qualifiedPagesPackage: String
) : TokenProcessor<FrontendData> {
    private class PageToProcess(
        val funName: String,
        val pkg: String,
        val slugFromFile: String,
        // See @Page annotation header docs for `routeOverride` behavior
        val routeOverride: String?
    )

    private val pages = mutableListOf<PageEntry>()
    private val kobwebInits = mutableListOf<InitKobwebEntry>()
    private val silkInits = mutableListOf<InitSilkEntry>()
    private val silkStyles = mutableListOf<ComponentStyleEntry>()
    private val silkVariants = mutableListOf<ComponentVariantEntry>()
    private val keyframesList = mutableListOf<KeyframesEntry>()

    // fqPkg to subdir, e.g. "blog._2022._01" to "01"
    private val packageMappings = mutableMapOf<String, String>()

    // We need to collect all package mappings before we start processing pages, so we store them in this
    // intermediate structure for a while
    private val pagesToProcess = mutableListOf<PageToProcess>()

    override fun handle(file: File, ktFile: KtFile) {
        var currPackage = ""
        var packageMappingSimpleName = PACKAGE_MAPPING_SIMPLE_NAME
        var pageSimpleName = PAGE_SIMPLE_NAME
        var initKobwebSimpleName = INIT_KOBWEB_SIMPLE_NAME
        var initSilkSimpleName = INIT_SILK_SIMPLE_NAME

        // This value needs to be processed after all children are visited, because `currPackage`
        // gets set AFTER @file annotations. Wild.
        var packageMappingAnnotation: KtAnnotationEntry? = null
        ktFile.visitAllChildren { element ->
            when (element) {
                is KtPackageDirective -> {
                    currPackage = element.fqName.asString()
                }

                is KtFileAnnotationList -> {
                    val annotations = element.annotationEntries.toList()
                    annotations.forEach { entry ->
                        when (entry.shortName?.asString()) {
                            packageMappingSimpleName ->
                                packageMappingAnnotation = entry
                        }
                    }
                }

                is KtImportDirective -> {
                    // It's unlikely this will happen but catch the "import as" case,
                    // e.g. `import com.varabyte.kobweb.core.Page as MyPage`
                    when (element.importPath?.fqName?.asString()) {
                        PACKAGE_MAPPING_FQN -> {
                            element.alias?.let { alias -> alias.name?.let { packageMappingSimpleName = it } }
                        }

                        PAGE_FQN -> {
                            element.alias?.let { alias -> alias.name?.let { pageSimpleName = it } }
                        }

                        INIT_KOBWEB_FQN -> {
                            element.alias?.let { alias -> alias.name?.let { initKobwebSimpleName = it } }
                        }

                        INIT_SILK_FQN -> {
                            element.alias?.let { alias -> alias.name?.let { initSilkSimpleName = it } }
                        }
                    }
                }

                is KtNamedFunction -> {
                    val annotations = element.annotationEntries.toList()
                    annotations.forEach { entry ->
                        when (entry.shortName?.asString()) {
                            pageSimpleName -> {
                                if (!annotations.mapNotNull { it.shortName?.asString() }.contains("Composable")) {
                                    reporter.report("${file.absolutePath}: `fun ${element.name}` annotated with `@$pageSimpleName` must also be `@Composable`.")
                                }

                                val routeOverride = entry.getStringValue(0)?.takeIf { it.isNotBlank() }
                                if (routeOverride?.startsWith("/") == true || currPackage.startsWith(
                                        qualifiedPagesPackage
                                    )
                                ) {
                                    // For simplicity for now, we reject route overrides which use the dynamic
                                    // route syntax in any part except for the last, e.g. in
                                    // "/dynamic/{}/route/{}/example/{}" the last "{}" is OK but the previous
                                    // ones are not currently supported.
                                    if (routeOverride == null || !routeOverride.substringBeforeLast(
                                            "/",
                                            missingDelimiterValue = ""
                                        ).contains("{}")
                                    ) {
                                        pagesToProcess.add(
                                            PageToProcess(
                                                funName = element.name!!.toString(),
                                                pkg = currPackage,
                                                slugFromFile = file.nameWithoutExtension.toLowerCase(),
                                                routeOverride = routeOverride
                                            )
                                        )
                                    } else {
                                        reporter.report("${file.absolutePath}: Skipped over `@$pageSimpleName fun ${element.name}`. Route override is invalid.")
                                    }
                                } else {
                                    reporter.report("${file.absolutePath}: Skipped over `@$pageSimpleName fun ${element.name}`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`")
                                }
                            }

                            initKobwebSimpleName -> {
                                element.name
                                    ?.let { name -> prefixQualifiedPath(currPackage, name) }
                                    ?.let { initFqn ->
                                        kobwebInits.add(
                                            InitKobwebEntry(
                                                initFqn,
                                                acceptsContext = element.valueParameters.size == 1
                                            )
                                        )
                                    }
                            }

                            initSilkSimpleName -> {
                                element.name
                                    ?.let { name -> prefixQualifiedPath(currPackage, name) }
                                    ?.let { initFqn -> silkInits.add(InitSilkEntry(initFqn)) }
                            }
                        }
                    }
                }

                is KtCallExpression -> {
                    when (element.calleeExpression?.text) {
                        "ComponentStyle" ->
                            processComponentStyle(file, currPackage, element, silkStyles, reporter)

                        "base" ->
                            processComponentStyleBase(file, currPackage, element, silkStyles, reporter)

                        "addVariant" ->
                            processComponentVariant(file, currPackage, element, silkVariants, reporter)

                        "addVariantBase" ->
                            processComponentVariantBase(file, currPackage, element, silkVariants, reporter)

                        "Keyframes", "keyframes" ->
                            processKeyframes(file, currPackage, element, keyframesList, reporter)
                    }
                }
            }
        }

        @Suppress("NAME_SHADOWING")
        packageMappingAnnotation?.let { packageMappingAnnotation ->
            if (currPackage.startsWith(qualifiedPagesPackage)) {
                packageMappings[currPackage] = packageMappingAnnotation.getStringValue(0)!!.let { value ->
                    // {} is a special value which means infer from the current package,
                    // e.g. `a.b.pkg` -> `"{pkg}"`
                    if (value != "{}") value else "{${currPackage.substringAfterLast('.')}}"
                }
            } else {
                reporter.report("${packageMappingAnnotation.containingFile.virtualFile.path}: Skipped over `@file:$packageMappingSimpleName`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`")
            }
        }
    }

    override fun finish(): FrontendData {
        for (pageToProcess in pagesToProcess) {
            val routeOverride = pageToProcess.routeOverride
            val slugPrefix = if (routeOverride != null && routeOverride.startsWith("/")) {
                // If route override starts with "/" it means the user set the full route explicitly
                routeOverride.substringBeforeLast('/')
            } else {
                RouteUtils
                    .resolve(packageMappings, pageToProcess.pkg)
                    .removePrefix(qualifiedPagesPackage.replace('.', '/'))
            }

            val prefixExtra =
                if (routeOverride != null && !routeOverride.startsWith("/") && routeOverride.contains("/")) {
                    // If route override did NOT begin with slash, but contains at least one subdir, it means append
                    // subdir to base route
                    "/" + routeOverride.substringBeforeLast("/")
                } else {
                    ""
                }

            val slug = if (routeOverride != null && routeOverride.last() != '/') {
                routeOverride.substringAfterLast("/").let { value ->
                    // {} is a special value which means infer from the current file,
                    // e.g. `Slug.kt` -> `"{slug}"`
                    if (value != "{}") value else "{${pageToProcess.slugFromFile}}"
                }
            } else {
                pageToProcess.slugFromFile
            }.takeIf { it != "index" } ?: ""

            pages.add(
                PageEntry(
                    "${pageToProcess.pkg}.${pageToProcess.funName}",
                    "$slugPrefix$prefixExtra/$slug"
                )
            )
        }

        return FrontendData(pages, kobwebInits, silkInits, silkStyles, silkVariants, keyframesList).also { it.assertValid() }
    }
}