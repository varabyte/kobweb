package com.varabyte.kobweb.project.frontend

import kotlinx.serialization.Serializable

// A note on imports
//
// We support extension properties for CSS styles, variants, and keyframes. For example, here is code that extends the
// `ButtonSize` style:
// *
// * ```
// * package some.example.pkg
// *
// * private val _XXL: ButtonSize
// * val ButtonSize.Companion.XXL: ButtonSize get() = _XXL
// * ```
// *
// * Because of how extension properties work, an import has to be provided for main.kt to add, because otherwise Kotlin
// * can't reference extension methods without it.

/**
 * Useful Kobweb-related information discovered while parsing a frontend module.
 *
 * @param pages A collection of methods annotated with `@Page` and relevant metadata.
 * @param kobwebInits A collection of methods annotated with `@InitKobweb` and relevant metadata.
 * @param silkInits A collection of methods annotated with `@InitSilk` and relevant metadata.
 */
@Serializable
class FrontendData(
    val pages: List<PageEntry> = mutableListOf(),
    val kobwebInits: List<InitKobwebEntry> = mutableListOf(),
    val silkInits: List<InitSilkEntry> = mutableListOf(),
    val keyframesList: List<KeyframesEntry> = mutableListOf(),
    val cssStyles: List<CssStyleEntry> = mutableListOf(),
    val cssStyleVariants: List<CssStyleVariantEntry> = mutableListOf(),
)

/**
 * Merge multiple [FrontendData] objects together.
 *
 * An error will be thrown if the merged result generates any invalid conditions, e.g. duplicate routes.
 */
fun Iterable<FrontendData>.merge(throwError: (String) -> Unit): FrontendData {
    return FrontendData(
        this.flatMap { it.pages },
        this.flatMap { it.kobwebInits },
        this.flatMap { it.silkInits },
        this.flatMap { it.keyframesList },
        this.flatMap { it.cssStyles },
        this.flatMap { it.cssStyleVariants },
    ).also { it.assertValid(throwError) }
}

fun FrontendData.assertValid(throwError: (String) -> Unit) {
    pages.assertValidPages(throwError)
}

private fun Iterable<PageEntry>.assertValidPages(throwError: (String) -> Unit) {
    val entriesByRoute = this.groupBy { it.route }
    entriesByRoute.forEach { (route, pages) ->
        if (pages.size > 1) {
            throwError("The route \"$route\" was defined more than once. Used by:\n${pages.joinToString("\n") { page -> " - ${page.fqn}" }}")
        }

        if (route.removePrefix("/").split('/', limit = 2).first() == "api") {
            throwError("The route \"$route\" starts with \"api\", which is currently a reserved route path, so please choose another name. Used by ${pages.first().fqn}.")
        }
    }
}

@Serializable
class AppEntry(val fqn: String)

@Serializable
class AppFrontendData(val appEntry: AppEntry?, val frontendData: FrontendData)

@Serializable
class InitSilkEntry(
    val fqn: String,
)

/**
 * Information about a method in the user's code targeted by an `@InitKobweb` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param acceptsContext If true, the method accepts a single `InitKobwebContext` argument; otherwise, no arguments.
 */
@Serializable
class InitKobwebEntry(val fqn: String, val acceptsContext: Boolean)

/**
 * Metadata about code like `val Bounce = Keyframes { ... }`
 */
@Serializable
class KeyframesEntry(val fqcn: String, val name: String, val import: String? = null)

/**
 * Information about a method in the user's code targeted by an `@Page` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param route The associated route that should be generated for this page method, e.g. "/example/path". The final
 *   value is usually decided by the current file name but could be influenced by arguments in the `@Page` annotation
 *   as well.
 * @param acceptsContext If true, the method accepts a single `PageContext` argument; otherwise, no arguments. Defaults
 *   to false for compatibility with libraries using a version of Kobweb before this feature was introduced.
 */
@Serializable
class PageEntry(val fqn: String, val route: String, val acceptsContext: Boolean = false)

/**
 * Metadata for code like `val MyStyle = CssStyle { ... }` or `val SM = ButtonSize()` (or any `CssStyle` subclass)
 *
 * The name of the variant will come from a `@CssName` annotation or, if not specified, the property name itself.
 */
@Serializable
class CssStyleEntry(val fqcn: String, val name: String, val import: String? = null, val layer: String? = null)

/**
 * Metadata for code like `val BoldLabelVariant = LabelStyle.addVariant { ... }` or `val SM = ButtonSize()`
 *
 * The name of the variant will come from a `@CssName` annotation or, if not specified, the property name itself.
 */
@Serializable
class CssStyleVariantEntry(val fqcn: String, val name: String, val import: String? = null, val layer: String? = null)
