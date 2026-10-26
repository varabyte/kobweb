package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.JvmPlatformInfo
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.backendFile
import com.varabyte.kobweb.frontendFile
import com.varabyte.kobweb.ksp.backend.BackendProcessor
import com.varabyte.kobweb.ksp.backend.AppBackendProcessor
import com.varabyte.kobweb.ksp.frontend.AppFrontendProcessor
import com.varabyte.kobweb.ksp.frontend.FrontendProcessor
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.kobweb.project.common.PackageShortcut

class KobwebProcessorProvider : SymbolProcessorProvider {
    /** A processor which does nothing, for when we can't (or shouldn't) process the project. */
    private object NoOpProcessor : SymbolProcessor {
        override fun process(resolver: Resolver): List<KSAnnotated> = emptyList()
    }

    /**
     * Report any problems with this package, which was resolved from the Gradle `kobweb` block property named
     * [propertyName] (whose value was [configuredValue]).
     *
     * @return true if any problems were reported.
     */
    private fun PackageName.reportProblems(logger: KSPLogger, propertyName: String, configuredValue: String): Boolean {
        val problems = findProblems()
        val resolvedFrom = if (configuredValue != value) " (resolved from \"$configuredValue\")" else ""
        problems.forEach { problem ->
            logger.error("The `kobweb.$propertyName` package \"$value\"$resolvedFrom configured in Gradle is not valid. $problem")
        }
        return problems.isNotEmpty()
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val processorMode = environment.options[KSP_PROCESSOR_MODE_KEY]?.let { ProcessorMode.valueOf(it) }
            ?: error("KobwebProcessorProvider: Missing processor mode ($KSP_PROCESSOR_MODE_KEY)")

        val projectGroup = environment.options[KSP_PROJECT_GROUP_KEY]?.let { PackageName(it) }
            ?: error("KobwebProcessorProvider: Missing project group ($KSP_PROJECT_GROUP_KEY)")

        return when (val platform = environment.platforms.singleOrNull()) {
            is JsPlatformInfo -> {

                val pagesPackageValue = environment.options[KSP_PAGES_PACKAGE_KEY]
                    ?: error("KobwebProcessorProvider: Missing pages package ($KSP_PAGES_PACKAGE_KEY)")
                val pagesPackage = projectGroup.resolve(PackageShortcut(pagesPackageValue))
                if (pagesPackage.reportProblems(environment.logger, "pagesPackage", pagesPackageValue)) {
                    return NoOpProcessor
                }

                val genFile = processorMode.frontendFile
                val defaultCssPrefix = environment.options[KSP_DEFAULT_CSS_PREFIX_KEY]
                when (processorMode) {
                    ProcessorMode.APP -> {
                        AppFrontendProcessor(
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = genFile,
                            projectGroup = projectGroup,
                            pagesPackage = pagesPackage,
                            defaultCssPrefix = defaultCssPrefix,
                        )
                    }

                    ProcessorMode.LIBRARY -> {
                        FrontendProcessor(
                            isLibrary = true,
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = genFile,
                            projectGroup = projectGroup,
                            pagesPackage = pagesPackage,
                            defaultCssPrefix = defaultCssPrefix,
                        )
                    }
                }
            }

            is JvmPlatformInfo -> {
                val apiPackageValue = environment.options[KSP_API_PACKAGE_KEY]
                    ?: error("KobwebProcessorProvider: Missing api package ($KSP_API_PACKAGE_KEY)")
                val apiPackage = projectGroup.resolve(PackageShortcut(apiPackageValue))
                if (apiPackage.reportProblems(environment.logger, "apiPackage", apiPackageValue)) {
                    return NoOpProcessor
                }

                when (processorMode) {
                    ProcessorMode.APP -> {
                        AppBackendProcessor(
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = processorMode.backendFile,
                            apiPackage = apiPackage,
                        )
                    }

                    ProcessorMode.LIBRARY -> {
                        BackendProcessor(
                            isLibrary = true,
                            codeGenerator = environment.codeGenerator,
                            logger = environment.logger,
                            genFile = processorMode.backendFile,
                            apiPackage = apiPackage,
                        )
                    }
                }
            }

            else -> {
                environment.logger.warn("KobwebProcessorProvider: Unknown platform: $platform")
                NoOpProcessor
            }
        }
    }
}
