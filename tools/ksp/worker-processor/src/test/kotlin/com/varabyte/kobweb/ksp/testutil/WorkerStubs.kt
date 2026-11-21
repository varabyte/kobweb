package com.varabyte.kobweb.ksp.testutil

/**
 * Source files declaring stand-ins for the framework types that the worker processor looks for.
 *
 * The processor finds `WorkerFactory` by fully qualified name and only reads its type arguments, so an empty interface
 * in the right package works just like the real thing.
 */
object WorkerStubs {
    val all = mapOf(
        "com/varabyte/kobweb/worker/WorkerFactory.kt" to
            //language=kotlin
            """
            package com.varabyte.kobweb.worker

            interface WorkerFactory<I, O>
        """.trimIndent(),
    )
}
