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
    val pages: List<PageEntry>,
    val kobwebInits: List<InitKobwebEntry>,
    val silkInits: List<InitSilkEntry>,
    val silkStyles: List<ComponentStyleEntry>,
    val silkVariants: List<ComponentVariantEntry>,
    val keyframesList: List<KeyframesEntry>,
) {
    // TODO: for some reason gradle give a noclassdeffounderror
    fun assertValid() {
//        pages.assertValidPages()
    }
}

fun Iterable<FrontendData>.merge(): FrontendData {
    return FrontendData(
        this.flatMap { it.pages },
        this.flatMap { it.kobwebInits },
        this.flatMap { it.silkInits },
        this.flatMap { it.silkStyles },
        this.flatMap { it.silkVariants },
        this.flatMap { it.keyframesList },
    ).also { it.assertValid() }
}

//fun FrontendData.assertValid() {
//    pages.assertValidPages()
//}

private fun Iterable<PageEntry>.assertValidPages() {
    val entriesByRoute = this.groupBy { it.route }
    entriesByRoute.forEach { (route, pages) ->
        // TODO
//        if (pages.size > 1) {
//            throw GradleException("The route \"$route\" was defined more than once. Used by:\n${pages.joinToString("\n") { page -> " - ${page.fqn}" }}")
//        }
//
//        if (route.removePrefix("/").split('/', limit = 2).first() == "api") {
//            throw GradleException("The route \"$route\" starts with \"api\", which is currently a reserved route path, so please choose another name. Used by ${pages.first().fqn}.")
//        }
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
 * Metadata about code like `val Bounce = Keyframes("bounce")` or `val Bounce by Keyframes`
 */
@Serializable
class KeyframesEntry(val fqcn: String)

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
 * Metadata about code like `val MyStyle = ComponentStyle("my-style")`
 */
@Serializable
class ComponentStyleEntry(val fqcn: String)

/**
 * Metadata about code like `val MyVariant = MyStyle.addVariant("my-variant")`
 */
@Serializable
class ComponentVariantEntry(val fqcn: String)
