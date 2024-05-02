package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.JvmPlatformInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.backendFile
import com.varabyte.kobweb.frontendFile
import com.varabyte.kobweb.ksp.backend.BackendProcessor
import com.varabyte.kobweb.ksp.frontend.AppProcessor
import com.varabyte.kobweb.ksp.frontend.FrontendProcessor

class KobwebProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val processorMode = environment.options[KSP_PROCESSOR_MODE_KEY]?.let { ProcessorMode.valueOf(it) }
            ?: error("KobwebProcessorProvider: Missing processor mode ($KSP_PROCESSOR_MODE_KEY)")

        return when (val platform = environment.platforms.singleOrNull()) {
            is JsPlatformInfo -> {
                val pagesPackage = environment.options[KSP_PAGES_PACKAGE_KEY]
                    ?: error("KobwebProcessorProvider: Missing pages package ($KSP_PAGES_PACKAGE_KEY)")

                val genFile = processorMode.frontendFile
                when (processorMode) {
                    ProcessorMode.APP -> {
                        AppProcessor(
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = genFile,
                            qualifiedPagesPackage = pagesPackage,
                        )
                    }

                    ProcessorMode.LIBRARY -> {
                        FrontendProcessor(
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = genFile,
                            qualifiedPagesPackage = pagesPackage,
                            defaultCssPrefix = environment.options[KSP_DEFAULT_CSS_PREFIX_KEY],
                        )
                    }
                }
            }

            is JvmPlatformInfo -> {
                val apiPackage = environment.options[KSP_API_PACKAGE_KEY]
                    ?: error("KobwebProcessorProvider: Missing api package ($KSP_API_PACKAGE_KEY)")

                BackendProcessor(
                    codeGenerator = environment.codeGenerator,
                    logger = environment.logger,
                    genFile = processorMode.backendFile,
                    qualifiedApiPackage = apiPackage,
                )
            }

            else -> {
                environment.logger.warn("KobwebProcessorProvider: Unknown platform: $platform")
                return object : SymbolProcessor {
                    override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
                }
            }
        }
    }
}
