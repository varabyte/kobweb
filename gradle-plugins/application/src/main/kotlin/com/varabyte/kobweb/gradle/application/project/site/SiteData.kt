package com.varabyte.kobweb.gradle.application.project.site

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import com.varabyte.kobweb.gradle.application.project.PackageUtils.prefixQualifiedPath
import com.varabyte.kobweb.gradle.application.project.PackageUtils.resolvePackageShortcut
import com.varabyte.kobweb.gradle.application.project.PsiUtils
import com.varabyte.kobweb.gradle.application.project.Reporter
import com.varabyte.kobweb.gradle.application.project.parseKotlinFile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import java.io.File

class SiteData {
    var app: AppEntry? = null
        internal set

    private val _pages = mutableListOf<PageEntry>()
    /** A collection of methods annotated with `@Page` and relevant metadata */
    val pages: List<PageEntry> = _pages

    private val _kobwebInits = mutableListOf<InitKobwebEntry>()
    /** A collection of methods annotated with `@InitKobweb` and relevant metadata */
    val kobwebInits: List<InitKobwebEntry> = _kobwebInits

    private val _silkInits = mutableListOf<InitSilkEntry>()
    /** A collection of methods annotated with `@InitSilk` and relevant metadata */
    val silkInits: List<InitSilkEntry> = _silkInits

    private val _silkStyleFqcns = mutableListOf<String>()
    /** The fully-qualified names of properties assigned to `ComponentStyle`s */
    val silkStyleFqcns: List<String> = _silkStyleFqcns

    private val _silkVariantFqcns = mutableListOf<String>()
    /** The fully-qualified names of properties assigned to `ComponentVariant`s */
    val silkVariantFqcns: List<String> = _silkVariantFqcns

    companion object {
        private fun processComponentStyle(
            file: File,
            filePackage: String,
            property: KtProperty,
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val propertyName = property.name ?: return false

            // Only top-level properties are allowed for now, so getting the fully qualified path is easy
            if (property.parent !is KtFile) {
                reporter.report("${file.absolutePath}: Not registering component style `val $propertyName`, as only top-level component styles are supported at this time")
                return false
            }

            return if (property.isPublic) {
                siteData._silkStyleFqcns.add(prefixQualifiedPath(filePackage, propertyName))
                true
            } else {
                reporter.report("${file.absolutePath}: Not registering component style `val $propertyName`, as it is not public")
                false
            }
        }

        /** Process a line like `val CustomStyle = ComponentStyle("custom") { ... }` */
        private fun processComponentStyle(
            file: File,
            filePackage: String,
            element: KtCallExpression,
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val property = element.parent as? KtProperty ?: return false
            return processComponentStyle(file, filePackage, property, siteData, reporter)
        }

        /** Process a line like `val CustomStyle = ComponentStyle.base("custom") { ... }` */
        private fun processComponentBaseStyle(
            file: File,
            filePackage: String,
            element: KtCallExpression,
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val qualifiedExpression = element.parent as? KtDotQualifiedExpression ?: return false
            if (qualifiedExpression.receiverExpression.text != "ComponentStyle") return false
            val property = qualifiedExpression.parent as? KtProperty ?: return false
            return processComponentStyle(file, filePackage, property, siteData, reporter)
        }

        private fun processComponentVariant(
            file: File,
            filePackage: String,
            property: KtProperty,
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val propertyName = property.name ?: return false

            // Only top-level properties are allowed for now, so getting the fully qualified path is easy
            if (property.parent !is KtFile) {
                reporter.report("${file.absolutePath}: Not registering component variant `val $propertyName`, as only top-level component variants are supported at this time")
                return false
            }

            return if (property.isPublic) {
                siteData._silkVariantFqcns.add(prefixQualifiedPath(filePackage, propertyName))
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
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val qualifiedExpression = element.parent as? KtDotQualifiedExpression ?: return false
            val property = qualifiedExpression.parent as? KtProperty ?: return false
            return processComponentVariant(file, filePackage, property, siteData, reporter)
        }

        /** Process a line like `val CustomVariant = CustomStyle.addBaseVariant("variant") { ... }` */
        private fun processComponentBaseVariant(
            file: File,
            filePackage: String,
            element: KtCallExpression,
            siteData: SiteData,
            reporter: Reporter
        ): Boolean {
            val qualifiedExpression = element.parent as? KtDotQualifiedExpression ?: return false
            val property = qualifiedExpression.parent as? KtProperty ?: return false
            return processComponentVariant(file, filePackage, property, siteData, reporter)
        }

        /**
         * @param group The group of this project, e.g. "org.example.mysite", required for prefixing relative package
         *   names (e.g. ".blog" -> "org.example.mysite.blog")
         * @param pagesPackage The relative package path that represents the root of the user's pages, e.g. ".pages"
         *   Annotated methods in source files not under this package will be ignored.
         * @param siteSources Kotlin jsMain source files that drive this site's frontend.
         */
        fun from(
            group: String,
            pagesPackage: String,
            siteSources: List<File>,
            reporter: Reporter,
        ): SiteData {
            val siteData = SiteData()
            val kotlinProject = PsiUtils.createKotlinProject()
            siteSources.forEach { file ->
                val ktFile = kotlinProject.parseKotlinFile(file)

                var currPackage = ""
                var appSimpleName = APP_SIMPLE_NAME
                var pageSimpleName = PAGE_SIMPLE_NAME
                var initKobwebSimpleName = INIT_KOBWEB_SIMPLE_NAME
                var initSilkSimpleName = INIT_SILK_SIMPLE_NAME
                ktFile.visitAllChildren { element ->
                    when (element) {
                        is KtPackageDirective -> {
                            currPackage = element.fqName.asString()
                        }
                        is KtImportDirective -> {
                            // It's unlikely this will happen but catch the "import as" case,
                            // e.g. `import com.varabyte.kobweb.core.Page as MyPage`
                            when (element.importPath?.fqName?.asString()) {
                                APP_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { appSimpleName = it }
                                    }
                                }
                                PAGE_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { pageSimpleName = it }
                                    }
                                }
                                INIT_KOBWEB_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { initKobwebSimpleName = it }
                                    }
                                }
                                INIT_SILK_FQN -> {
                                    element.alias?.let { alias ->
                                        alias.name?.let { initSilkSimpleName = it }
                                    }
                                }
                            }
                        }
                        is KtNamedFunction -> {
                            val annotations = element.annotationEntries.toList()
                            annotations.forEach { entry ->
                                when (entry.shortName?.asString()) {
                                    appSimpleName -> {
                                        val appFqn = when {
                                            currPackage.isNotEmpty() -> "$currPackage.${element.name}"
                                            else -> element.name
                                        }
                                        appFqn?.let { siteData.app = AppEntry(it) }
                                    }
                                    pageSimpleName -> {
                                        if (!annotations.mapNotNull { it.shortName?.asString() }.contains("Composable")) {
                                            reporter.report("${file.absolutePath}: `fun ${element.name}` annotated with `@$pageSimpleName` must also be `@Composable`.")
                                        }

                                        val qualifiedPagesPackage = resolvePackageShortcut(group, pagesPackage)
                                        if (currPackage.startsWith(qualifiedPagesPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(qualifiedPagesPackage)
                                                .replace('.', '/')

                                            val slug = when (val maybeSlug =
                                                file.nameWithoutExtension.toLowerCase()) {
                                                "index" -> ""
                                                else -> maybeSlug
                                            }

                                            // This file should be somewhere underneath the pages package
                                            check(currPackage.isNotEmpty())
                                            siteData._pages.add(
                                                PageEntry(
                                                    "$currPackage.${element.name}",
                                                    "$slugPrefix/$slug"
                                                )
                                            )

                                        } else {
                                            reporter.report("${file.absolutePath}: Skipped over `@$pageSimpleName fun ${element.name}`. It is defined under package `$currPackage` but must exist under `$qualifiedPagesPackage`")
                                        }
                                    }
                                    initKobwebSimpleName -> {
                                        element.name
                                            ?.let { name -> prefixQualifiedPath(currPackage, name) }
                                            ?.let { initFqn ->
                                                siteData._kobwebInits.add(
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
                                            ?.let { initFqn -> siteData._silkInits.add(InitSilkEntry(initFqn)) }
                                    }
                                }
                            }
                        }
                        is KtCallExpression -> {
                            when (element.calleeExpression?.text) {
                                "ComponentStyle" ->
                                    processComponentStyle(file, currPackage, element, siteData, reporter)
                                "base" -> processComponentBaseStyle(file, currPackage, element, siteData, reporter)
                                "addVariant" -> processComponentVariant(file, currPackage, element, siteData, reporter)
                                "addBaseVariant" ->
                                    processComponentBaseVariant(file, currPackage, element, siteData, reporter)
                            }
                        }
                    }
                }
            }

            return siteData
        }
    }
}