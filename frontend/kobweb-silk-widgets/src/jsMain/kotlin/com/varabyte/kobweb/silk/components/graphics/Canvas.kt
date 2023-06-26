package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.RenderingContext
import kotlin.js.Date
import kotlin.math.max
import kotlin.math.min

val CanvasStyle by ComponentStyle(prefix = "silk") {}

/**
 * Arguments passed to the user's `render` callback.
 *
 * @param ctx The canvas context which provides drawing functionality.
 * @param width The width (in pixels) of this canvas.
 * @param height The height (in pixels) of this canvas.
 * @param colorMode The active color mode used by the site.
 * @param elapsedMs Time elapsed since last frame.
 *
 * @param C The type of the canvas context
 */
class RenderScope<C : RenderingContext>(
    val ctx: C,
    val width: Int,
    val height: Int,
    val colorMode: ColorMode,
    val elapsedMs: Double,
)

/**
 * An MS value which, if used, will result in a 60FPS render.
 */
const val ONE_FRAME_MS_60_FPS = 1000.0f / 60.0f

/**
 * An MS value which, if used, will result in a 30FPS render.
 */
const val ONE_FRAME_MS_30_FPS = ONE_FRAME_MS_60_FPS * 2.0f

private class CanvasElementBuilder : ElementBuilder<HTMLCanvasElement> {
    val canvas by lazy { document.createElement("canvas") as HTMLCanvasElement }
    override fun create() = canvas.cloneNode() as HTMLCanvasElement
}

private class RenderCallback<C : RenderingContext>(
    private val ctx: C,
    private val width: Int,
    private val height: Int,
    minDeltaMs: Number,
    maxDeltaMs: Number,
    private val render: RenderScope<C>.() -> Unit,
    private val onStepped: RenderCallback<C>.() -> Unit
) {
    private var lastRenderedTimestamp: Double = 0.0
    private val minDeltaMs = minDeltaMs.toDouble()
    private val maxDeltaMs = maxDeltaMs.toDouble()

    fun step(colorMode: ColorMode) {
        val firstRender = lastRenderedTimestamp == 0.0
        val now = Date.now()
        val deltaMs = now - lastRenderedTimestamp
        if (deltaMs >= minDeltaMs) {
            val scope = RenderScope(ctx, width, height, colorMode, if (firstRender) 0.0 else min(deltaMs, maxDeltaMs))
            scope.render()
            lastRenderedTimestamp = now
        }
        onStepped()
    }
}

/**
 * A composable which creates a canvas element along with a callback (non-composable!) which handles a frame of
 * rendering.
 *
 * Instead of this method, callers should use [Canvas2d] or [CanvasGl].
 *
 * @param createContext A factory method for creating a context for the canvas element. If this returns null, the
 *   canvas will not render.
 */
@Composable
// Note: This method is marked inline and made private currently to avoid issues with the Composable compiler
// I'd like to say I completely understand crossinline / noinline tags but I just added those to make the compiler
// happy.
private inline fun <C : RenderingContext> Canvas(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    minDeltaMs: Number = 0f,
    maxDeltaMs: Number = 500f,
    ref: ElementRefScope<HTMLElement>? = null,
    crossinline createContext: (HTMLCanvasElement) -> C?,
    noinline render: RenderScope<C>.() -> Unit,
) {
    val builder = remember { CanvasElementBuilder() }
    TagElement(
        builder,
        CanvasStyle.toModifier(variant)
            .width(width.px).height(height.px)
            .then(modifier).toAttrs {
                attr("width", width.toString())
                attr("height", height.toString())
            }
    ) {
        registerRefScope(ref)

        val colorMode = getColorMode()
        DisposableEffect(colorMode) {
            var requestId = 0
            val ctx = createContext(scopeElement)
            if (ctx != null) {
                val callback = RenderCallback(ctx, width, height, minDeltaMs, maxDeltaMs, render, onStepped = {
                    requestId = window.requestAnimationFrame { step(colorMode) }
                })
                requestId = window.requestAnimationFrame { callback.step(colorMode) }
            }

            onDispose {
                if (requestId != 0) {
                    window.cancelAnimationFrame(requestId)
                }
            }
        }
    }
}

/**
 * Renders a [Canvas] using the "2d" rendering context.
 *
 * @param width The width (in pixels) of this canvas. If the user adds a different value for the width in the [modifier]
 *   parameter, the canvas will be resized to fit.
 * @param height The height (in pixels) of this canvas. Same additional details as [width].
 * @param minDeltaMs If set, ensures that draw won't be called more than once per this period. If not set, render will
 *   be called as frequently as possible. The constant [ONE_FRAME_MS_60_FPS] could be useful to set here.
 * @param maxDeltaMs Ensured that the delta passed into [RenderScope] will be capped. This is useful to make sure that
 *   render behavior doesn't explode after sitting on a breakpoint for a while or get stuck on some edge case long
 *   calculation. By default, it is capped to half a second.
 * @param render A callback which handles rendering a single frame.
 */
@Composable
fun Canvas2d(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    minDeltaMs: Number = 0.0,
    maxDeltaMs: Number = max(500.0, minDeltaMs.toDouble()),
    ref: ElementRefScope<HTMLElement>? = null,
    render: RenderScope<CanvasRenderingContext2D>.() -> Unit,
) {
    Canvas(
        width,
        height,
        modifier,
        variant,
        minDeltaMs,
        maxDeltaMs,
        ref,
        { canvas -> canvas.getContext("2d") as? CanvasRenderingContext2D },
        render
    )
}

/**
 * Renders a [Canvas] using the "webgl" rendering context.
 *
 * @param width The width (in pixels) of this canvas. If the user adds a different value for the width in the [modifier]
 *   parameter, the canvas will be resized to fit.
 * @param height The height (in pixels) of this canvas. Same additional details as [width].
 * @param minDeltaMs If set, ensures that draw won't be called more than once per this period. If not set, render will
 *   be called as frequently as possible. The constant [ONE_FRAME_MS_60_FPS] could be useful to set here.
 * @param maxDeltaMs Ensured that the delta passed into [RenderScope] will be capped. This is useful to make sure that
 *   render behavior doesn't explode after sitting on a breakpoint for a while or get stuck on some edge case long
 *   calculation. By default, it is capped to half a second.
 * @param render A callback which handles rendering a single frame.
 */
@Composable
fun CanvasGl(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    minDeltaMs: Number = 0.0,
    maxDeltaMs: Number = max(500.0, minDeltaMs.toDouble()),
    ref: ElementRefScope<HTMLElement>? = null,
    render: RenderScope<WebGLRenderingContext>.() -> Unit,
) {
    Canvas(
        width,
        height,
        modifier,
        variant,
        minDeltaMs,
        maxDeltaMs,
        ref,
        { canvas -> canvas.getContext("webgl") as? WebGLRenderingContext },
        render
    )
}
