package com.varabyte.kobweb.api.dispose

/**
 * An annotation which identifies a function as one which will be called when the server reloads the api in dev mode.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DisposeApi