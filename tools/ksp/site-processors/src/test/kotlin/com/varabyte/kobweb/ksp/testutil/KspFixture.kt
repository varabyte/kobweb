package com.varabyte.kobweb.ksp.testutil

import com.google.devtools.ksp.impl.KotlinSymbolProcessing
import com.google.devtools.ksp.processing.KSPJsConfig
import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.ksp.KOBWEB_APP_METADATA_FRONTEND
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.ksp.KSP_DEFAULT_CSS_PREFIX_KEY
import com.varabyte.kobweb.ksp.KSP_PAGES_PACKAGE_KEY
import com.varabyte.kobweb.ksp.KSP_PROCESSOR_MODE_KEY
import com.varabyte.kobweb.ksp.KSP_PROJECT_GROUP_KEY
import com.varabyte.kobweb.ksp.KobwebProcessorProvider
import com.varabyte.kobweb.project.common.nonStrictDefault
import com.varabyte.kobweb.project.frontend.AppFrontendData
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.json.Json
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * The options which the Kobweb Gradle plugin passes to the processor for a site module's JS sources.
 *
 * Packages are passed as configured in the Gradle `kobweb` block, so they may use shortcuts like ".pages".
 */
class FrontendKspOptions(
    val mode: ProcessorMode,
    val projectGroup: String,
    val pagesPackage: String,
    val defaultCssPrefix: String? = null,
) {
    companion object {
        /** Convenience method for creating [ProcessorMode.APP] options, with useful defaults. */
        fun app(
            projectGroup: String,
            pagesPackage: String = ".pages",
        ) = FrontendKspOptions(ProcessorMode.APP, projectGroup, pagesPackage)

        /** Convenience method for creating [ProcessorMode.LIBRARY] options, with useful defaults. */
        fun library(
            projectGroup: String,
            pagesPackage: String = ".pages",
        ) = FrontendKspOptions(ProcessorMode.LIBRARY, projectGroup, pagesPackage)
    }
}

private fun FrontendKspOptions.toMap() = buildMap {
    put(KSP_PROCESSOR_MODE_KEY, mode.name)
    put(KSP_PROJECT_GROUP_KEY, projectGroup)
    put(KSP_PAGES_PACKAGE_KEY, pagesPackage)
    defaultCssPrefix?.let { put(KSP_DEFAULT_CSS_PREFIX_KEY, it) }
}

/**
 * A rule for running [KobwebProcessorProvider] for real, through KSP2, over some test sources.
 *
 * Each run works in its own folder, which is deleted when the test finishes.
 */
class KspFixture : TestRule {
    private val tempFolder = TemporaryFolder()

    override fun apply(base: Statement, description: Description): Statement = tempFolder.apply(base, description)

    /**
     * Run the processor the way it runs on a site module's JS sources, where it collects frontend metadata.
     *
     * @param sources Kotlin source files, keyed by their path relative to the source root. [KobwebStubs] are always
     *   added, the way a real site always has the framework on its classpath.
     */
    fun runFrontend(options: FrontendKspOptions, sources: Map<String, String>): KspResult {
        val rootDir = tempFolder.newFolder()
        val sourceDir = rootDir.resolve("src")
        (KobwebStubs.frontend + sources).forEach { (path, content) ->
            sourceDir.resolve(path).apply { parentFile.mkdirs() }.writeText(content)
        }

        val outputDirs = listOf("kotlin", "resources", "classes").map { rootDir.resolve("out/$it") }
        val (kotlinOutputDir, resourceOutputDir, classOutputDir) = outputDirs
        val config = KSPJsConfig.Builder().apply {
            backend = "JS"
            moduleName = "test"
            sourceRoots = listOf(sourceDir)
            commonSourceRoots = emptyList()
            // No stdlib or framework libraries; tests declare stubs for the framework types they need
            libraries = emptyList()
            processorOptions = options.toMap()
            projectBaseDir = rootDir
            outputBaseDir = rootDir.resolve("out")
            cachesDir = rootDir.resolve("caches")
            this.kotlinOutputDir = kotlinOutputDir
            this.resourceOutputDir = resourceOutputDir
            this.classOutputDir = classOutputDir
            languageVersion = KotlinVersion.CURRENT.let { "${it.major}.${it.minor}" }
            apiVersion = languageVersion
        }.build()

        val logger = RecordingLogger()
        KotlinSymbolProcessing(config, listOf(KobwebProcessorProvider()), logger).execute()

        val generatedFiles = outputDirs.flatMap { outputDir ->
            outputDir.walk()
                .filter { it.isFile }
                .map { it.relativeTo(outputDir).invariantSeparatorsPath to it.readText() }
        }.toMap()
        return KspResult(generatedFiles, logger.warnings, logger.errors)
    }
}

/**
 * What a [KspFixture] run produced.
 *
 * @property generatedFiles The contents of every file the processor generated, keyed by its path relative to the
 *   output folder it was written to, e.g. "kobweb/metadata/frontend.json".
 */
class KspResult(
    val generatedFiles: Map<String, String>,
    val warnings: List<String>,
    val errors: List<String>,
)

/** The frontend metadata generated by an app run, i.e. [FrontendKspOptions.mode] was [ProcessorMode.APP]. */
val KspResult.appFrontendData: AppFrontendData
    get() = Json.nonStrictDefault.decodeFromString(generatedFiles.getValue(KOBWEB_APP_METADATA_FRONTEND))

/** The frontend metadata generated by a library run, i.e. [FrontendKspOptions.mode] was [ProcessorMode.LIBRARY]. */
val KspResult.libraryFrontendData: FrontendData
    get() = Json.nonStrictDefault.decodeFromString(generatedFiles.getValue(KOBWEB_METADATA_FRONTEND))
