package com.varabyte.kobweb.gradle.core.project.frontend

import kotlinx.serialization.Serializable
import org.gradle.api.GradleException

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
        if (pages.size > 1) {
            throw GradleException("The route \"$route\" was defined more than once. Used by:\n${pages.joinToString("\n") { page -> " - ${page.fqn}" }}")
        }

        if (route.removePrefix("/").split('/', limit = 2).first() == "api") {
            throw GradleException("The route \"$route\" starts with \"api\", which is currently a reserved route path, so please choose another name. Used by ${pages.first().fqn}.")
        }
    }
}
