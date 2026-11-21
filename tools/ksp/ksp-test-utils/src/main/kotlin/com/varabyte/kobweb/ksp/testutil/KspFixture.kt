package com.varabyte.kobweb.ksp.testutil

import com.google.devtools.ksp.impl.KotlinSymbolProcessing
import com.google.devtools.ksp.processing.KSPConfig
import com.google.devtools.ksp.processing.KSPJsConfig
import com.google.devtools.ksp.processing.KSPJvmConfig
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File

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
 * A rule for running a KSP processor for real, through KSP2, over some test sources.
 *
 * Each run works in its own folder, which is deleted when the test finishes.
 */
class KspFixture : TestRule {
    private val tempFolder = TemporaryFolder()

    override fun apply(base: Statement, description: Description): Statement = tempFolder.apply(base, description)

    /**
     * Run [provider]'s processor over [sources] as Kotlin/JS code.
     *
     * @param sources Kotlin source files, keyed by their path relative to the source root.
     */
    fun runJs(
        provider: SymbolProcessorProvider,
        processorOptions: Map<String, String>,
        sources: Map<String, String>,
    ): KspResult {
        return runKsp(provider, sources) { rootDir ->
            KSPJsConfig.Builder().apply {
                configure(rootDir, processorOptions)
                backend = "JS"
            }.build()
        }
    }

    /**
     * Run [provider]'s processor over [sources] as Kotlin/JVM code.
     *
     * @param sources Kotlin source files, keyed by their path relative to the source root.
     */
    fun runJvm(
        provider: SymbolProcessorProvider,
        processorOptions: Map<String, String>,
        sources: Map<String, String>,
    ): KspResult {
        return runKsp(provider, sources) { rootDir ->
            KSPJvmConfig.Builder().apply {
                configure(rootDir, processorOptions)
                javaSourceRoots = emptyList()
                javaOutputDir = rootDir.resolve("out/java")
                jvmTarget = "11"
            }.build()
        }
    }

    private fun runKsp(
        provider: SymbolProcessorProvider,
        sources: Map<String, String>,
        createConfig: (rootDir: File) -> KSPConfig,
    ): KspResult {
        val rootDir = tempFolder.newFolder()
        sources.forEach { (path, content) ->
            rootDir.resolve("src/$path").apply { parentFile.mkdirs() }.writeText(content)
        }

        val logger = RecordingLogger()
        KotlinSymbolProcessing(createConfig(rootDir), listOf(provider), logger).execute()

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
