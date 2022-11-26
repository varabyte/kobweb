package com.varabyte.kobweb.gradle.application.processors

import com.varabyte.kobweb.gradle.application.project.app.APP_FQN
import com.varabyte.kobweb.gradle.application.project.app.APP_SIMPLE_NAME
import com.varabyte.kobweb.gradle.application.project.app.AppData
import com.varabyte.kobweb.gradle.application.project.app.AppEntry
import com.varabyte.kobweb.gradle.core.processors.FrontendDataProcessor
import com.varabyte.kobweb.gradle.core.processors.TokenProcessor
import com.varabyte.kobweb.gradle.core.util.Reporter
import com.varabyte.kobweb.gradle.core.util.visitAllChildren
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.io.File

class AppDataProcessor(
    reporter: Reporter,
    qualifiedPagesPackage: String
) : TokenProcessor<AppData> {
    var appEntry: AppEntry? = null
    val frontendDataProcessor = FrontendDataProcessor(reporter, qualifiedPagesPackage)

    override fun handle(file: File, ktFile: KtFile) {
        frontendDataProcessor.handle(file, ktFile)

        var currPackage = ""
        var appSimpleName = APP_SIMPLE_NAME

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
                            element.alias?.let { alias -> alias.name?.let { appSimpleName = it } }
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
                                appFqn?.let { appEntry = AppEntry(it) }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun finish(): AppData {
        return AppData(
            appEntry,
            frontendDataProcessor.finish()
        )
    }
}