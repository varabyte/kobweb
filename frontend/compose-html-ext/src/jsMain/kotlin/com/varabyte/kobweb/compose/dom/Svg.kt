package com.varabyte.kobweb.compose.dom

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.svg.SVGElement

/**
 * A composable for creating an SVG element.
 *
 * Combine with [Path] to create an SVG icon. For example:
 *
 * ```
 * Svg(attrs = Modifier.width(24.px).height(24.px)
 *     .toAttrs {
 *         attr("viewBox", "0 0 16 16")
 *         attr("role", "img")
 *         attr("aria-label", "Warning:")
 *     }
 * ) {
 *     Path(attrs = Modifier.toAttrs {
 *         attr(
 *             "d",
 *             "M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"
 *         )
 *     })
 * }
 * ```
 */
@Composable
fun Svg(
    attrs: AttrBuilderContext<SVGElement>? = null,
    content: ContentBuilder<SVGElement>? = null
) {
    GenericTag("svg", "http://www.w3.org/2000/svg", attrs, content)
}

@Suppress("UnusedReceiverParameter") // SVGElement scope is useful to ensure `Path` is only used within an SVG
@Composable
fun ElementScope<SVGElement>.Path(
    attrs: AttrBuilderContext<SVGElement>? = null,
    content: ContentBuilder<SVGElement>? = null
) {
    GenericTag("path", "http://www.w3.org/2000/svg", attrs, content)
}
