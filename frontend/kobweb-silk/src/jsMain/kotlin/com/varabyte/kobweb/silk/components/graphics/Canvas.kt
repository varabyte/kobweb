package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributeBuilder
import com.varabyte.kobweb.compose.ui.height
import com.varabyte.kobweb.compose.ui.width
import com.varabyte.kobweb.silk.components.ComponentKey
import com.varabyte.kobweb.silk.components.ComponentModifier
import com.varabyte.kobweb.silk.components.then
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme.colors.Palette
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Date
import kotlin.math.min

val CanvasKey = ComponentKey("silk-canvas")

/**
 * Arguments passed to the user's `render` callback.
 *
 * @param ctx The canvas context which provides drawing functionality
 * @param width The width (in pixels) of this canvas
 * @param height The height (in pixels) of this canvas
 * @param palette The active color palette used by the site
 * @param elapsedMs Time elapsed since last frame.
 */
class RenderScope(
    val ctx: CanvasRenderingContext2D,
    val width: Int,
    val height: Int,
    val palette: Palette,
    val elapsedMs: Double,
)

/**
 * An MS value which, if used, will result in a 60FPS render.
 */
const val ONE_FRAME_MS_60_FPS = 1000.0f / 60.0f

private class CanvasElementBuilder : ElementBuilder<HTMLCanvasElement> {
    val canvas by lazy { document.createElement("canvas") as HTMLCanvasElement }
    override fun create() = canvas.cloneNode() as HTMLCanvasElement
}

private class RenderCallback(
    private val ctx: CanvasRenderingContext2D,
    private val width: Int,
    private val height: Int,
    minDeltaMs: Number,
    maxDeltaMs: Number,
    private val render: RenderScope.() -> Unit,
    private val onStepped: RenderCallback.() -> Unit
) {
    private var lastTimestamp: Double = 0.0
    private val minDeltaMs = minDeltaMs.toDouble()
    private val maxDeltaMs = maxDeltaMs.toDouble()

    fun step(palette: Palette) {
        val firstRender = lastTimestamp == 0.0
        val now = Date.now()
        val deltaMs = now - lastTimestamp
        if (deltaMs >= minDeltaMs) {
            val scope = RenderScope(ctx, width, height, palette, if (firstRender) 0.0 else min(deltaMs, maxDeltaMs))
            scope.render()
        }
        onStepped()
    }
}

/**
 * A composable which creates a canvas element. The body for this element is a (non-composable!) method which handles
 * a frame of rendering.
 *
 * @param width The width (in pixels) of this canvas. If the user adds a different value for the width in the [modifier]
 *   parameter, the canvas will be stretched to fit.
 * @param height The height (in pixels) of this canvas. Same additional details as [width].
 * @param minDeltaMs If set, ensures that draw won't be called more than once per this period. If not set, render will
 *   be called as frequently as possible. The constant [ONE_FRAME_MS_60_FPS] could be useful to set here.
 * @param maxDeltaMs Ensured that the delta passed into [RenderScope] will be capped. This is useful to make sure that
 *   render behavior doesn't explode after sitting on a breakpoint for a while or get stuck on some edge case long
 *   calculation. By default, it is capped to half a second.
 */
@Composable
fun Canvas(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: ComponentModifier? = null,
    minDeltaMs: Number = 0f,
    maxDeltaMs: Number = 500f,
    render: RenderScope.() -> Unit,
) {
    val builder = remember { CanvasElementBuilder() }
    TagElement(
        builder,
        SilkTheme.componentModifiers[CanvasKey].then(variant).toModifier(null).width(width.px).height(height.px)
            .then(modifier).asAttributeBuilder {
                attr("width", width.toString())
                attr("height", height.toString())
            }
    ) {
        var requestId by remember { mutableStateOf(0) }
        val palette = SilkTheme.palette
        DomSideEffect(palette) { element ->
            val ctx = element.getContext("2d") as? CanvasRenderingContext2D
            if (ctx != null) {
                val callback = RenderCallback(ctx, width, height, minDeltaMs, maxDeltaMs, render, onStepped = {
                    requestId = window.requestAnimationFrame { step(palette) }
                })
                requestId = window.requestAnimationFrame { callback.step(palette) }
            }
        }

        DisposableRefEffect(palette) {
            onDispose {
                if (requestId != 0) {
                    window.cancelAnimationFrame(requestId)
                }
            }
        }
    }
}