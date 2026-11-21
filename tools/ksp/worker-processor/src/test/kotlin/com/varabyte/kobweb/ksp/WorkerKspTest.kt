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

    @Test
    fun `missing worker factory is an error`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/PiInput.kt" to
                    //language=kotlin
                    """
                    package example

                    class PiInput
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb worker module must have exactly one class that implements `WorkerFactory`. However, none were found."
        )
    }

    @Test
    fun `multiple worker factories is an error`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/WorkerFactories.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class CalculatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                    internal class EstimatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb worker module must have exactly one class that implements `WorkerFactory`. However, the following were found: [example.CalculatePiWorkerFactory, example.EstimatePiWorkerFactory]."
        )
    }

    @Test
    fun `worker factory without an empty constructor is an error`() {
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

                    internal class CalculatePiWorkerFactory(val digits: Int) : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb `WorkerFactory` implementation must have a public empty constructor. Please add one to `example.CalculatePiWorkerFactory`."
        )
    }

    @Test
    fun `private worker factory is an error`() {
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

                    private class CalculatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb `WorkerFactory` implementation cannot be private, as this prevents us from generating code that wraps it. Please make `example.CalculatePiWorkerFactory` internal."
        )
    }

    @Test
    fun `non-public input type is an error`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/CalculatePiWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    internal class PiInput
                    class PiOutput

                    internal class CalculatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb `WorkerFactory` implementation's input type (`example.PiInput`) must be public so the Kobweb application can use it. Please make `example.PiInput` public."
        )
    }

    @Test
    fun `non-public output type is an error`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/CalculatePiWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    internal class PiOutput

                    internal class CalculatePiWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb `WorkerFactory` implementation's output type (`example.PiOutput`) must be public so the Kobweb application can use it. Please make `example.PiOutput` public."
        )
    }

    @Test
    fun `worker factory name without the WorkerFactory suffix is an error`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js"),
            sources = mapOf(
                "example/PiCalculator.kt" to
                    //language=kotlin
                    """
                    package example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculator : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).containsExactly(
            "A Kobweb `WorkerFactory` implementation's name should end with the suffix \"WorkerFactory\", so we can automatically generate an associated \"Worker\" class from it. Please change the name of `example.PiCalculator` to meet this requirement. Alternately, call `kobweb { worker { fqcn.set(...) }` to explicitly set the generated class name."
        )
    }
}
