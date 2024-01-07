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
import com.varabyte.kobweb.project.frontend.AppData
import com.varabyte.kobweb.project.frontend.AppEntry
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    private val qualifiedPagesPackage: String,
) : SymbolProcessor {
    private val fileDependencies = mutableListOf<KSFile>()
    private var appFqn: String? = null
    private lateinit var frontendData: FrontendData

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val appFun = resolver.getSymbolsWithAnnotation(APP_FQN).toList()

        if (appFun.size > 1) {
            logger.error("At most one @App function is allowed per project.")
        } else {
            appFun.singleOrNull()?.let {
                fileDependencies.add(it.containingFile!!)
                appFqn = (it as KSFunctionDeclaration).qualifiedName?.asString()
            }
        }

        val frontendProcessor = FrontendProcessor(
            codeGenerator = codeGenerator,
            logger = logger,
            genFile = "",
            qualifiedPagesPackage = qualifiedPagesPackage,
        ).also { it.process(resolver) }
        fileDependencies.addAll(frontendProcessor.fileDependencies)
        frontendData = frontendProcessor.getData()

        return emptyList()
    }

    override fun finish() {
        val encodedData = Json.encodeToString(AppData(appFqn?.let { AppEntry(it) }, frontendData))

        val (path, extension) = genFile.split('.')
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(encodedData)
        }
    }
}
