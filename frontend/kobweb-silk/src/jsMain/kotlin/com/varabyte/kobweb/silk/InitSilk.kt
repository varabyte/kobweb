package com.varabyte.kobweb.silk

import com.varabyte.kobweb.silk.theme.MutableSilkTheme

/**
 * An annotation which identifies a function as one which will be called when the page opens before DOM nodes are
 * composed. The function should take an [InitSilkContext] as its only parameter.
 */
annotation class InitSilk

class InitSilkContext(val theme: MutableSilkTheme)

/** By default, does nothing here, but will be overridden at build time by the Gradle Application plugin. */
var initSilkHook: (InitSilkContext) -> Unit = {}
