package com.varabyte.kobweb.ksp.backend

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.varabyte.kobweb.ksp.common.API_INTERCEPTOR_FQN
import com.varabyte.kobweb.ksp.common.RESPONSE_FQN
import com.varabyte.kobweb.project.backend.ApiInterceptorEntry
import com.varabyte.kobweb.project.backend.AppBackendData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppBackendProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    qualifiedApiPackage: String,
) : SymbolProcessor {
    private val fileDependencies = mutableSetOf<KSFile>()

    private var apiInterceptorMethod: ApiInterceptorEntry? = null

    // use single processor so that results are stored between round during multi-round processing
    private val backendProcessor = BackendProcessor(
        isLibrary = false,
        codeGenerator = codeGenerator,
        logger = logger,
        genFile = "",
        qualifiedApiPackage = qualifiedApiPackage,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(API_INTERCEPTOR_FQN).toList().let { apiInterceptorMethods ->
            if (apiInterceptorMethods.size > 1 || (apiInterceptorMethod != null && apiInterceptorMethods.isNotEmpty())) {
                logger.error("At most one @ApiInterceptor function is allowed per project.")
            } else {
                apiInterceptorMethods.singleOrNull()?.let {
                    val funDeclaration = (it as KSFunctionDeclaration)
                    funDeclaration.returnType?.resolve()?.let { returnType ->
                        if (returnType.declaration.qualifiedName?.asString() != RESPONSE_FQN || returnType.isMarkedNullable) {
                            logger.error("The method annotated with @ApiInterceptor must return `Response`, got `${returnType.declaration.qualifiedName?.asString()}${if (returnType.isMarkedNullable) "?" else ""}`")
                        }
                    }

                    fileDependencies.add(it.containingFile!!)
                    apiInterceptorMethod =
                        funDeclaration.qualifiedName?.asString()?.let { fqn -> ApiInterceptorEntry(fqn) }
                }
            }
        }

        backendProcessor.process(resolver)

        return emptyList()
    }

    override fun finish() {
        val backendResult = backendProcessor.getProcessorResult()
        fileDependencies.addAll(backendResult.fileDependencies)
        val appBackendData = AppBackendData(
            apiInterceptorMethod,
            backendResult.data
        )

        val (path, extension) = genFile.split('.')
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(Json.encodeToString(appBackendData))
        }
    }
}
