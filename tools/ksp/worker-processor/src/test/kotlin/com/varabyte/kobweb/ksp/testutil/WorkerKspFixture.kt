package com.varabyte.kobweb.ksp.testutil

import com.varabyte.kobweb.ksp.KSP_WORKER_FQCN_KEY
import com.varabyte.kobweb.ksp.KSP_WORKER_OUTPUT_PATH_KEY
import com.varabyte.kobweb.ksp.WorkerProcessorProvider

/**
 * The options which the Kobweb Gradle plugin passes to the processor for a worker module.
 *
 * @property outputPath Where the worker script is served, relative to the site's worker root, e.g. "site/pi.js".
 * @property fqcn The fully qualified class name of the generated worker, as configured in the Gradle `kobweb` block,
 *   so it may use shortcuts like ".PiWorker". If null, the name is derived from the `WorkerFactory` implementation.
 */
class WorkerKspOptions(
    val outputPath: String,
    val fqcn: String? = null,
)

private fun WorkerKspOptions.toMap() = buildMap {
    put(KSP_WORKER_OUTPUT_PATH_KEY, outputPath)
    fqcn?.let { put(KSP_WORKER_FQCN_KEY, it) }
}

/**
 * Run [WorkerProcessorProvider] the way it runs on a worker module's JS sources.
 *
 * @param sources Kotlin source files, keyed by their path relative to the source root. [WorkerStubs.all] are always
 *   added, the way a real worker module always has the framework on its classpath.
 */
fun KspFixture.runWorker(options: WorkerKspOptions, sources: Map<String, String>): KspResult {
    return runJs(WorkerProcessorProvider(), options.toMap(), WorkerStubs.all + sources)
}
