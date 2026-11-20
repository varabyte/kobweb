package com.varabyte.kobweb.ksp.frontend

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.APP_FQN
import com.varabyte.kobweb.ksp.util.isComposable
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.common.nonStrictDefault
import com.varabyte.kobweb.project.frontend.AppFrontendData
import com.varabyte.kobweb.project.frontend.AppEntry
import kotlinx.serialization.json.Json

internal class AppFrontendProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    projectGroup: PackageName,
    pagesPackage: PackageName,
    defaultCssPrefix: String? = null,
) : SymbolProcessor {
    private val fileDependencies = mutableListOf<KSFile>()
    private var appFqn: String? = null

    // use single processor so that results are stored between round during multi-round processing
    private val frontendProcessor = FrontendProcessor(
        isLibrary = false,
        codeGenerator = codeGenerator,
        logger = logger,
        genFile = "",
        projectGroup = projectGroup,
        pagesPackage = pagesPackage,
        defaultCssPrefix = defaultCssPrefix,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val appFun = resolver.getSymbolsWithAnnotation(APP_FQN).toList()

        if (appFun.size > 1 || (appFqn != null && appFun.isNotEmpty())) {
            logger.error("At most one @App function is allowed per project.")
        } else {
            appFun.singleOrNull()?.let {
                val appMethod = it as KSFunctionDeclaration
                val fqn = appMethod.qualifiedName!!.asString()
                if (!appMethod.isComposable) {
                    logger.error("`fun $fqn` annotated with `@App` must also be `@Composable`.", appMethod)
                }
                // The generated code calls the app method with a trailing content lambda
                val takesContentCallback = appMethod.parameters.singleOrNull()
                    ?.type?.resolve()?.declaration?.qualifiedName?.asString() == "kotlin.Function0"
                if (!takesContentCallback) {
                    logger.error("`$fqn` is tagged with the `@App` annotation but doesn't take a composable content callback as its only parameter.", appMethod)
                }

                fileDependencies.add(appMethod.containingFile!!)
                appFqn = fqn
            }
        }

        frontendProcessor.process(resolver)

        return emptyList()
    }

    override fun finish() {
        val frontendResult = frontendProcessor.getProcessorResult()
        fileDependencies.addAll(frontendResult.fileDependencies)
        val appFrontendData = AppFrontendData(appFqn?.let { AppEntry(it) }, frontendResult.data)

        val (path, extension) = genFile.split('.')
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(Json.nonStrictDefault.encodeToString(appFrontendData))
        }
    }
}
