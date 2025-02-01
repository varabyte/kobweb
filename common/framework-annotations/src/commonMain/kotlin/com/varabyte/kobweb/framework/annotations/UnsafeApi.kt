package com.varabyte.kobweb.framework.annotations

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is potentially unsafe and should only be used after reviewing its documentation."
)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class UnsafeApi(@Suppress("unused") val message: String)
