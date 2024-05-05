package com.varabyte.kobweb.project.frontend

import kotlinx.serialization.Serializable

/**
 * Useful Kobweb-related information discovered while parsing a frontend module.
 *
 * @param pages A collection of methods annotated with `@Page` and relevant metadata.
 * @param kobwebInits A collection of methods annotated with `@InitKobweb` and relevant metadata.
 * @param silkInits A collection of methods annotated with `@InitSilk` and relevant metadata.
 * @param silkStyles A collection of fields that represent component style definitions.
 * @param silkVariants A collection of fields that represent component variant definitions.
 */
@Serializable
class FrontendData(
    val pages: List<PageEntry> = mutableListOf(),
    val kobwebInits: List<InitKobwebEntry> = mutableListOf(),
    val silkInits: List<InitSilkEntry> = mutableListOf(),
    val silkStyles: List<ComponentStyleEntry> = mutableListOf(),
    val silkVariants: List<ComponentVariantEntry> = mutableListOf(),
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
        this.flatMap { it.silkStyles },
        this.flatMap { it.silkVariants },
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
class AppData(val appEntry: AppEntry?, val frontendData: FrontendData)

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
 *
 * For legacy Keyframes usages, the name comes from the Keyframes object itself
 * (e.g. `val MyStyle = Keyframes("bounce") { ... }`) but by Kobweb 1.0 this code should be removed and the
 * `name` field below should become non-null.
 */
@Serializable
class KeyframesEntry(val fqcn: String, val name: String? = null)

/**
 * Information about a method in the user's code targeted by an `@Page` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param route The associated route that should be generated for this page method, e.g. "/example/path". The final
 *   value is usually decided by the current file name but could be influenced by arguments in the `@Page` annotation
 *   as well.
 */
@Serializable
class PageEntry(val fqn: String, val route: String)

/**
 * Metadata about code like `val MyStyle = ComponentStyle { ... }`
 *
 * `ComponentStyle` is a legacy class. The name of the variant comes from the property name itself.
 */
@Serializable
class ComponentStyleEntry(val fqcn: String)

/**
 * Metadata about code like `val MyVariant = MyStyle.addVariant { ... }`
 *
 * `ComponentVariant` is a legacy class. The name of the variant comes from the property name itself.
 */
@Serializable
class ComponentVariantEntry(val fqcn: String)

/**
 * Metadata for code like `val MyStyle = CssStyle { ... }` or `val SM = ButtonSize()` (or any `CssStyle` subclass)
 *
 * The name of the variant will come from a `@CssName` annotation or, if not specified, the property name itself.
 *
 * ## A note on imports
 * We support extension properties for CSS styles, useful to extend companion objects, such as in the
 * following code:
 *
 * ```
 * package some.example.pkg
 *
 * private val _XXL: ButtonSize
 * val ButtonSize.Companion.XXL: ButtonSize get() = _XXL
 * ```
 *
 * Because of how extension properties work, an import has to be provided for main.kt to add, because otherwise Kotlin
 * can't reference extension methods without an import statement. In the above example, `fqcn` will be set to
 * `ButtonSize.XXL` and `import` will be set to `some.example.pkg.XXL`
 */
@Serializable
class CssStyleEntry(val fqcn: String, val name: String, val import: String? = null)

/**
 * Metadata for code like `val BoldLabelVariant = LabelStyle.addVariant { ... }` or `val SM = ButtonSize()`
 *
 * The name of the variant will come from a `@CssName` annotation or, if not specified, the property name itself.
 */
@Serializable
class CssStyleVariantEntry(val fqcn: String, val name: String)
