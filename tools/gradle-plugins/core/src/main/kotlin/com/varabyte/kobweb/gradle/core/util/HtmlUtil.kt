package com.varabyte.kobweb.gradle.core.util

import kotlinx.html.BODY
import kotlinx.html.HEAD
import kotlinx.html.STYLE
import kotlinx.html.TagConsumer
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe

object HtmlUtil {

    // Workaround for generating child nodes without the containing <body> tag
    // See: https://github.com/Kotlin/kotlinx.html/issues/228
    private inline fun <T, C : TagConsumer<T>> C.bodyFragment(crossinline block: BODY.() -> Unit): T {
        BODY(emptyMap(), this).block()
        return this.finalize()
    }

    // Workaround for generating child nodes without the containing <head> tag
    private inline fun <T, C : TagConsumer<T>> C.headFragment(crossinline block: HEAD.() -> Unit): T {
        HEAD(emptyMap(), this).block()
        return this.finalize()
    }



    // Use `xhtmlCompatible = true` to include a closing slash as currently kotlinx.html needs them when adding raw text.
    // See: https://github.com/Kotlin/kotlinx.html/issues/247
    /**
     * Serialize the child nodes created by [block], excluding the opening and closing `<body>` tag.
     *
     * This is useful when accumulating elements from several sources, which can then be wrapped altogether in a single
     * `<body>` tag.
     */
    fun serializeBodyContents(block: BODY.() -> Unit): String =
        createHTML(prettyPrint = false, xhtmlCompatible = true).bodyFragment(block)


/**
     * Serialize the child nodes created by [block], excluding the opening and closing `<head>` tag.
     *
     * This is useful when accumulating elements from several sources, which can then be wrapped altogether in a single
     * `<head>` tag.
     */
    fun serializeHeadContents(block: HEAD.() -> Unit): String =
        createHTML(prettyPrint = false, xhtmlCompatible = true).headFragment(block)
}

/**
 * Adds an [`@import`](https://developer.mozilla.org/en-US/docs/Web/CSS/@import) rule to import a CSS stylesheet,
 * optionally loading it into a specific [CSS Cascade layer](https://developer.mozilla.org/en-US/docs/Learn/CSS/Building_blocks/Cascade_layers).
 *
 * This can be useful when using a third party CSS library whose styles are a bit too aggressive and are interfering
 * with your own styles.
 *
 * For example, replace a `<link>` tag with an `@import` rule:
 *
 * ```
 * kobweb.app.index.head.add {
 *   // Before
 *   link(href = "/highlight.js/styles/dracula.css", rel = "stylesheet")
 *   // After
 *   style {
 *     importCss("/highlight.js/styles/dracula.css", layerName = "highlightjs")
 *   }
 * }
 * ```
 *
 * Then, register your new layer in an `@InitSilk` block:
 *
 * ```
 * @InitSilk
 * fun initSilk(ctx: InitSilkContext) {
 *   // Layer(s) referenced in build.gradle.kts
 *   ctx.stylesheet.cssLayers.add("highlightjs", after = SilkLayer.BASE)
 * }
 * ```
 *
 * @param url The URL of the CSS file to import. This can be an external URL or a path to a local `public` resource.
 * @param layerName The cascade layer in which to load the stylesheet. **For this to have any effect, the layer
 *  MUST be registered in an `@InitSilk` block.**
 */
fun STYLE.importCss(url: String, layerName: String? = null) {
    unsafe { raw("@import url(\"$url\")${if (layerName != null) " layer($layerName)" else ""};\n") }
}
