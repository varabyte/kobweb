@file:Suppress("LeakingThis") // Using recommend Gradle practices for initializing extensions

package com.varabyte.kobweb.gradle.worker.extensions

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.worker.util.suggestWorkerName
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

/**
 * A sub-block for defining all properties relevant to a Kobweb worker.
 */
abstract class WorkerBlock @Inject constructor(project: Project) {
    /**
     * The development name you'd like to use to represent this project.
     *
     * It won't be visible to the end user, but it will be used to generate a script name for this worker. If left
     * unset, it will be given the generic name "worker".
     */
    abstract val name: Property<String>

    /**
     * Set the fully qualified class name of the worker class to generate.
     *
     * Normally, the class name is inferred from the name of the `WorkerStrategy` implementation class, but users can
     * choose to override this if they prefer.
     *
     * Note that if this value begins with a ".", it will be prepended with the package that the `WorkerStrategy`
     * implementation lives in. Similarly, if this value ends with a ".", its package will be used and then appended
     * with a class name inferred from the `WorkerStrategy` implementation's name.
     *
     * For a concrete example, if this worker's `WorkerStrategy` implementation is
     * `com.example.PiCalculatorWorkerStrategy`, then...
     *
     * * fqcn = _not set_ → "com.example.PiCalculatorWorker"
     * * fqcn = "com.mysite.PiWorker" → "com.mysite.PiWorker"
     * * fqcn = ".PiWorker" → "com.example.PiWorker"
     * * fqcn = "com.mysite." → "com.mysite.PiCalculatorWorker"
     * * fqcn = ".mysite.PiWorker" → "com.example.mysite.PiWorker"
     * * fqcn = ".mysite." → "com.example.mysite.PiCalculatorWorker"
     */
    abstract val fqcn: Property<String>

    init {
        name.convention(project.suggestWorkerName())
    }
}

val KobwebBlock.worker: WorkerBlock
    get() = extensions.getByType<WorkerBlock>()

internal fun KobwebBlock.createWorkerBlock(project: Project) {
    extensions.create<WorkerBlock>("worker", project)
}
