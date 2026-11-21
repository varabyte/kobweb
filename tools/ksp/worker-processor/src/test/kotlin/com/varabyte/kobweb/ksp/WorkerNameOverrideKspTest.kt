@file:Suppress("FunctionName")

package com.varabyte.kobweb.ksp

import com.varabyte.kobweb.ksp.testutil.KspFixture
import com.varabyte.kobweb.ksp.testutil.WorkerKspOptions
import com.varabyte.kobweb.ksp.testutil.runWorker
import com.varabyte.truthish.assertThat
import org.junit.Rule
import org.junit.Test

/** Tests for each form of the `fqcn` override documented in the Gradle `WorkerBlock`. */
class WorkerNameOverrideKspTest {
    @get:Rule
    val ksp = KspFixture()

    @Test
    fun `fqcn with only a class name puts the worker in the root package`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = "PiWorker"),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("PiWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("PiWorker.kt")
        assertThat(workerFile).doesNotContain("package ")
        assertThat(workerFile).contains("class PiWorker(")
    }

    @Test
    fun `fqcn with a package and class name is used as is`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = "com.mysite.PiWorker"),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/mysite/PiWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("com/mysite/PiWorker.kt")
        assertThat(workerFile).contains("package com.mysite")
        assertThat(workerFile).contains("class PiWorker(")
    }

    @Test
    fun `fqcn starting with a dot is relative to the factory package`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = ".PiWorker"),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/example/PiWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("com/example/PiWorker.kt")
        assertThat(workerFile).contains("package com.example")
        assertThat(workerFile).contains("class PiWorker(")
    }

    @Test
    fun `fqcn with a relative subpackage is appended to the factory package`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = ".mysite.PiWorker"),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/example/mysite/PiWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("com/example/mysite/PiWorker.kt")
        assertThat(workerFile).contains("package com.example.mysite")
        assertThat(workerFile).contains("class PiWorker(")
    }

    @Test
    fun `fqcn ending with a dot keeps the class name inferred from the factory`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = "com.mysite."),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/mysite/PiCalculatorWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("com/mysite/PiCalculatorWorker.kt")
        assertThat(workerFile).contains("package com.mysite")
        assertThat(workerFile).contains("class PiCalculatorWorker(")
    }

    @Test
    fun `fqcn with only a relative subpackage keeps the class name inferred from the factory`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = ".mysite."),
            sources = mapOf(
                "com/example/PiCalculatorWorkerFactory.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculatorWorkerFactory : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/example/mysite/PiCalculatorWorker.kt", "main.kt")
        val workerFile = result.generatedFiles.getValue("com/example/mysite/PiCalculatorWorker.kt")
        assertThat(workerFile).contains("package com.example.mysite")
        assertThat(workerFile).contains("class PiCalculatorWorker(")
    }

    @Test
    fun `fqcn with a class name lets the factory name skip the WorkerFactory suffix`() {
        val result = ksp.runWorker(
            options = WorkerKspOptions(outputPath = "site/pi.js", fqcn = ".PiWorker"),
            sources = mapOf(
                "com/example/PiCalculator.kt" to
                    //language=kotlin
                    """
                    package com.example

                    import com.varabyte.kobweb.worker.WorkerFactory

                    class PiInput
                    class PiOutput

                    internal class PiCalculator : WorkerFactory<PiInput, PiOutput>
                """.trimIndent(),
            ),
        )

        assertThat(result.errors).isEmpty()
        assertThat(result.generatedFiles.keys).containsExactly("com/example/PiWorker.kt", "main.kt")
        assertThat(result.generatedFiles.getValue("main.kt")).contains("val factory = com.example.PiCalculator()")
    }
}
