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
import com.varabyte.kobweb.project.frontend.AppFrontendData
import com.varabyte.kobweb.project.frontend.AppEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppFrontendProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    qualifiedPagesPackage: String,
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
        qualifiedPagesPackage = qualifiedPagesPackage,
        defaultCssPrefix = defaultCssPrefix,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val appFun = resolver.getSymbolsWithAnnotation(APP_FQN).toList()

        if (appFun.size > 1 || (appFqn != null && appFun.isNotEmpty())) {
            logger.error("At most one @App function is allowed per project.")
        } else {
            appFun.singleOrNull()?.let {
                fileDependencies.add(it.containingFile!!)
                appFqn = (it as KSFunctionDeclaration).qualifiedName?.asString()
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
            writer.write(Json.encodeToString(appFrontendData))
        }
    }
}
