package com.varabyte.kobweb.gradle.application.project.site

import com.varabyte.kobweb.gradle.application.extensions.visitAllChildren
import com.varabyte.kobweb.gradle.application.project.KobwebProject
import com.varabyte.kobweb.gradle.application.project.PsiUtils
import com.varabyte.kobweb.gradle.application.project.parseKotlinFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class SiteData {
    var app: AppEntry? = null
        internal set

    private val _pages = mutableListOf<PageEntry>()
    val pages: List<PageEntry> = _pages

    private val _kobwebInits = mutableListOf<InitKobwebEntry>()
    val kobwebInits: List<InitKobwebEntry> = _kobwebInits

    private val _silkInits = mutableListOf<InitSilkEntry>()
    val silkInits: List<InitSilkEntry> = _silkInits

    companion object {
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
                            element.annotationEntries.forEach { entry ->
                                when (entry.shortName?.asString()) {
                                    appSimpleName -> {
                                        val appFqn = when {
                                            currPackage.isNotEmpty() -> "$currPackage.${element.name}"
                                            else -> element.name
                                        }
                                        appFqn?.let { siteData.app = AppEntry(it) }
                                    }
                                    pageSimpleName -> {
                                        val qualifiedPackagesPackage =
                                            KobwebProject.prefixQualifiedPackage(group, pagesPackage)
                                        if (currPackage.startsWith(qualifiedPackagesPackage)) {
                                            // e.g. com.example.pages.blog -> blog
                                            val slugPrefix = currPackage
                                                .removePrefix(qualifiedPackagesPackage)
                                                .replace('.', '/')

                                            val slug = when (val maybeSlug =
                                                file.nameWithoutExtension.toLowerCase()) {
                                                "index" -> ""
                                                else -> maybeSlug
                                            }

                                            siteData._pages.add(
                                                PageEntry(
                                                    "$currPackage.${element.name}",
                                                    "$slugPrefix/$slug"
                                                )
                                            )
                                        }
                                    }
                                    initKobwebSimpleName -> {
                                        val initFqn = when {
                                            currPackage.isNotEmpty() -> "$currPackage.${element.name}"
                                            else -> element.name
                                        }
                                        initFqn?.let { siteData._kobwebInits.add(
                                            InitKobwebEntry(
                                                initFqn,
                                                acceptsContext = element.valueParameters.size == 1
                                            )
                                        ) }
                                    }
                                    initSilkSimpleName -> {
                                        siteData._silkInits.add(InitSilkEntry("$currPackage.${element.name}"))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return siteData
        }
    }
}
