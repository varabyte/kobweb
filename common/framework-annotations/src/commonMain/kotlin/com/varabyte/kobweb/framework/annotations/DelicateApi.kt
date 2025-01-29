package com.varabyte.kobweb.framework.annotations

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is delicate and should only be used after reviewing its documentation."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class DelicateApi(@Suppress("unused") val message: String)
