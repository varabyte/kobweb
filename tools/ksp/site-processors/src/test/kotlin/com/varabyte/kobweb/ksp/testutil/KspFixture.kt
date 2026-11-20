package com.varabyte.kobweb.ksp.testutil

import com.google.devtools.ksp.impl.KotlinSymbolProcessing
import com.google.devtools.ksp.processing.KSPConfig
import com.google.devtools.ksp.processing.KSPJsConfig
import com.google.devtools.ksp.processing.KSPJvmConfig
import com.varabyte.kobweb.ProcessorMode
import com.varabyte.kobweb.ksp.KOBWEB_APP_METADATA_BACKEND
import com.varabyte.kobweb.ksp.KOBWEB_APP_METADATA_FRONTEND
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import com.varabyte.kobweb.ksp.KSP_API_PACKAGE_KEY
import com.varabyte.kobweb.ksp.KSP_DEFAULT_CSS_PREFIX_KEY
import com.varabyte.kobweb.ksp.KSP_PAGES_PACKAGE_KEY
import com.varabyte.kobweb.ksp.KSP_PROCESSOR_MODE_KEY
import com.varabyte.kobweb.ksp.KSP_PROJECT_GROUP_KEY
import com.varabyte.kobweb.ksp.KobwebProcessorProvider
import com.varabyte.kobweb.project.backend.AppBackendData
import com.varabyte.kobweb.project.common.nonStrictDefault
import com.varabyte.kobweb.project.frontend.AppFrontendData
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.json.Json
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File

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
            defaultCssPrefix: String? = null,
        ) = FrontendKspOptions(ProcessorMode.APP, projectGroup, pagesPackage, defaultCssPrefix)

        /** Convenience method for creating [ProcessorMode.LIBRARY] options, with useful defaults. */
        fun library(
            projectGroup: String,
            pagesPackage: String = ".pages",
            defaultCssPrefix: String? = null,
        ) = FrontendKspOptions(ProcessorMode.LIBRARY, projectGroup, pagesPackage, defaultCssPrefix)
    }
}

private fun FrontendKspOptions.toMap() = buildMap {
    put(KSP_PROCESSOR_MODE_KEY, mode.name)
    put(KSP_PROJECT_GROUP_KEY, projectGroup)
    put(KSP_PAGES_PACKAGE_KEY, pagesPackage)
    defaultCssPrefix?.takeIf { it.isNotBlank() }?.let { put(KSP_DEFAULT_CSS_PREFIX_KEY, it) }
}

/**
 * The options which the Kobweb Gradle plugin passes to the processor for a site module's JVM sources.
 *
 * Packages are passed as configured in the Gradle `kobweb` block, so they may use shortcuts like ".api".
 */
class BackendKspOptions(
    val mode: ProcessorMode,
    val projectGroup: String,
    val apiPackage: String,
) {
    companion object {
        /** Convenience method for creating [ProcessorMode.APP] options, with useful defaults. */
        fun app(
            projectGroup: String,
            apiPackage: String = ".api",
        ) = BackendKspOptions(ProcessorMode.APP, projectGroup, apiPackage)

        /** Convenience method for creating [ProcessorMode.LIBRARY] options, with useful defaults. */
        fun library(
            projectGroup: String,
            apiPackage: String = ".api",
        ) = BackendKspOptions(ProcessorMode.LIBRARY, projectGroup, apiPackage)
    }
}

private fun BackendKspOptions.toMap() = mapOf(
    KSP_PROCESSOR_MODE_KEY to mode.name,
    KSP_PROJECT_GROUP_KEY to projectGroup,
    KSP_API_PACKAGE_KEY to apiPackage,
)

/** Fill in the config settings shared by all platforms, for a run working in [rootDir]. */
private fun KSPConfig.Builder.configure(rootDir: File, processorOptions: Map<String, String>) {
    moduleName = "test"
    sourceRoots = listOf(rootDir.resolve("src"))
    commonSourceRoots = emptyList()
    // No stdlib or framework libraries; tests declare stubs for the framework types they need
    libraries = emptyList()
    this.processorOptions = processorOptions
    projectBaseDir = rootDir
    outputBaseDir = rootDir.resolve("out")
    cachesDir = rootDir.resolve("caches")
    kotlinOutputDir = rootDir.resolve("out/kotlin")
    resourceOutputDir = rootDir.resolve("out/resources")
    classOutputDir = rootDir.resolve("out/classes")
    languageVersion = KotlinVersion.CURRENT.let { "${it.major}.${it.minor}" }
    apiVersion = languageVersion
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
     * @param sources Kotlin source files, keyed by their path relative to the source root. [KobwebStubs.frontend] are
     *   always added, the way a real site always has the framework on its classpath.
     */
    fun runFrontend(options: FrontendKspOptions, sources: Map<String, String>): KspResult {
        return runKsp(KobwebStubs.frontend + sources) { rootDir ->
            KSPJsConfig.Builder().apply {
                configure(rootDir, options.toMap())
                backend = "JS"
            }.build()
        }
    }

    /**
     * Run the processor the way it runs on a site module's JVM sources, where it collects backend metadata.
     *
     * @param sources Kotlin source files, keyed by their path relative to the source root. [KobwebStubs.backend] are
     *   always added, the way a real site always has the framework on its classpath.
     */
    fun runBackend(options: BackendKspOptions, sources: Map<String, String>): KspResult {
        return runKsp(KobwebStubs.backend + sources) { rootDir ->
            KSPJvmConfig.Builder().apply {
                configure(rootDir, options.toMap())
                javaSourceRoots = emptyList()
                javaOutputDir = rootDir.resolve("out/java")
                jvmTarget = "11"
            }.build()
        }
    }

    private fun runKsp(sources: Map<String, String>, createConfig: (rootDir: File) -> KSPConfig): KspResult {
        val rootDir = tempFolder.newFolder()
        sources.forEach { (path, content) ->
            rootDir.resolve("src/$path").apply { parentFile.mkdirs() }.writeText(content)
        }

        val logger = RecordingLogger()
        KotlinSymbolProcessing(createConfig(rootDir), listOf(KobwebProcessorProvider()), logger).execute()

        val outputDirs = rootDir.resolve("out").listFiles().orEmpty().filter { it.isDirectory }
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

/** The backend metadata generated by an app run, i.e. [BackendKspOptions.mode] was [ProcessorMode.APP]. */
val KspResult.appBackendData: AppBackendData
    get() = Json.nonStrictDefault.decodeFromString(generatedFiles.getValue(KOBWEB_APP_METADATA_BACKEND))
