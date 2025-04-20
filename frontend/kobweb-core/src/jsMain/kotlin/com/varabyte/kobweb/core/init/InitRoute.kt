package com.varabyte.kobweb.core.init

/**
 * An annotation which identifies a function as one which will be called when a page is about to be rendered.
 * The function should take an [InitKobwebContext] as its only parameter.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class InitRoute
