package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.SVGFillType
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeType
import com.varabyte.kobweb.compose.dom.svg.SVGSvgAttrsScope
import com.varabyte.kobweb.compose.dom.svg.Svg
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.svg.SVGElement

sealed interface IconRenderStyle {
    @Suppress("CanSealedSubClassBeObject") // May add Fill parameters someday
    class Fill : IconRenderStyle
    class Stroke(val strokeWidth: Number? = null) : IconRenderStyle
}

/**
 * A convenience helper function for creating your own SVG icon.
 *
 * This method takes a few common parameters (with defaults). Any of them can be set to null in case you want to
 * handle them yourself, setting values on [attrs] directly.
 *
 * @param viewBox The viewBox to use for the SVG. Defaults to 24x24.
 * @param width The width of the SVG. Defaults to 1em (so that it will resize according to its container's font size).
 *   Can be set explicitly to null if you want to handle passing in sizes yourself.
 * @param renderStyle The drawing style to use when rendering the SVG (i.e. stroke or fill).
 * @param attrs A scope for setting attributes on the SVG.
 * @param content A scope which handles declaring the SVG's content.
 */
@Composable
fun createIcon(
    viewBox: ViewBox? = ViewBox.sized(24),
    width: CSSLengthValue? = 1.em,
    renderStyle: IconRenderStyle? = IconRenderStyle.Stroke(),
    attrs: (SVGSvgAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGElement>
) {
    Svg(attrs = {
        width?.let { width(it) }
        viewBox?.let { viewBox(it.x, it.y, it.width, it.height) }
        renderStyle?.let { renderStyle ->
            when (renderStyle) {
                is IconRenderStyle.Fill -> {
                    fill(SVGFillType.CurrentColor)
                    stroke(SVGStrokeType.None)
                }

                is IconRenderStyle.Stroke -> {
                    stroke(SVGStrokeType.CurrentColor)
                    fill(SVGFillType.None)
                    renderStyle.strokeWidth?.let { strokeWidth(it) }
                }
            }
        }
        attrs?.invoke(this)
    }, content)
}
