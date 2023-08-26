package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.JvmPlatformInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.varabyte.kobweb.ksp.backend.BackendProcessor
import com.varabyte.kobweb.ksp.frontend.FrontendProcessor

class KobwebProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return when (val platform = environment.platforms.singleOrNull()) {
            is JsPlatformInfo -> FrontendProcessor(environment.codeGenerator, environment.logger, environment.options)
            is JvmPlatformInfo -> BackendProcessor(environment.codeGenerator, environment.logger, environment.options)
            else -> { // TODo: what to do about this? make predefined EmptySymbolProcessor?
                environment.logger.warn("KobwebProcessorProvider: Unknown platform: $platform")
                return object : SymbolProcessor {
                    override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
                }
            }
        }
    }
}
