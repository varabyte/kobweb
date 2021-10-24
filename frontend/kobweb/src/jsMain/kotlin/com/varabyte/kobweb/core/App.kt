package com.varabyte.kobweb.core

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.boxSizing
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px

/**
 * An annotation which identifies a [Composable] function as one which will be used as the root skeleton for every
 * page.
 *
 * The method should take a `content: @Composable () -> Unit` parameter.
 *
 * If no method is annotated `@App` then [DefaultApp] will be used. Of course, your own custom app method can compose
 * that function if it wishes:
 *
 * ```
 * @App
 * @Composable
 * fun MyApp(content: @Composable () -> Unit) {
 *   DefaultApp {
 *     // My own extra initialization
 *     content()
 *   }
 * }
 * ```
 *
 * Finally, there must either be no methods or just a single method marked with this annotation. If Kobweb encounters
 * more than one App annotation, it will log an error and discard duplicates arbitrarily.
 */
annotation class App

@Composable
fun DefaultApp(content: @Composable () -> Unit) {
    Style(DefaultStyleSheet)
    content()
}

object DefaultStyleSheet : StyleSheet() {
    init {
        "html, body" style {
            // Allow our app to stretch the full screen
            padding(0.px)
            margin(0.px)
        }

        "*" style {
            // See also: https://css-tricks.com/box-sizing
            boxSizing("border-box")
        }
    }
}