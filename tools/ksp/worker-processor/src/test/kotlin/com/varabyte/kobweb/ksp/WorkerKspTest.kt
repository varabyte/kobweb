@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp

import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.WorkerKspOptions
import com.varabyte.kobweb.ksp.testutil.runWorker
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

class WorkerKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `worker class and main file are generated from the worker factory`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/CalculatePiWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class CalculatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("example/CalculatePiWorker.kt", "main.kt")

        val workerFile = result.generatedFiles.getValue("example/CalculatePiWorker.kt")
        assertThat(workerFile).contains("package example")
        assertThat(workerFile).contains(
            "class CalculatePiWorker(override var onOutput: WorkerContext.(output: example.PiOutput) -> Unit = {})"
        )
        assertThat(workerFile).contains("com.varabyte.kobweb.worker.Worker<example.PiInput, example.PiOutput>")
        assertThat(workerFile).contains("Worker(BasePath.prependTo(\"/_kobweb/workers/site/pi.js\"))")

        val mainFile = result.generatedFiles.getValue("main.kt")
        assertThat(mainFile).contains("val factory = example.CalculatePiWorkerFactory()")
    }

    @Test
    fun `abstract worker factory base classes are not implementations`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/CalculatePiWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    abstract class BasePiWorkerFactory : WorkerFactory<PiInput, PiOutput>

                    internal class CalculatePiWorkerFactory : BasePiWorkerFactory()
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.warnings).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("example/CalculatePiWorker.kt", "main.kt")
        assertThat(result.generatedFiles.getValue("example/CalculatePiWorker.kt"))
            .contains("com.varabyte.kobweb.worker.Worker<example.PiInput, example.PiOutput>")
    }
}
