package com.varabyte.kobweb.silk

import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkConfig
import com.varabyte.kobweb.silk.theme.SilkTheme

/**
 * An annotation which identifies a function as one which will be called when the page opens before DOM nodes are
 * composed. The function should take an [InitSilkContext] as its only parameter.
 */
annotation class InitSilk

/**
 * Various classes passed to the user in a method annotated by `@InitSilk` which they can use to for initializing Silk
 * values.
 *
 * @param config A handful of settings which will be used for configuring Silk behavior at startup time.
 * @param theme A version of [SilkTheme] that is still mutable (before it has been frozen, essentially, at startup).
 *   Use this if you need to modify site global colors, shapes, typography, and/or styles.
 */
class InitSilkContext(val config: SilkConfig, val theme: MutableSilkTheme)

/** By default, does nothing here, but will be overridden at build time by the Gradle Application plugin. */
var initSilkHook: (InitSilkContext) -> Unit = {}