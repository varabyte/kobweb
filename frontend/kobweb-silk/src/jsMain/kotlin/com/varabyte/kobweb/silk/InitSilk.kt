package com.varabyte.kobweb.silk

/**
 * An annotation which identifies a function as one which will be called when the page opens before DOM nodes are
 * composed. The function should take an [InitSilkContext] as its only parameter.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class InitSilk

/** By default, does nothing here, but will be overridden at build time by the Gradle Application plugin. */
var initSilkHook: (InitSilkContext) -> Unit = {}