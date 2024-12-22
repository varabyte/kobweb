package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.webgl.WebGL2RenderingContext
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
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

sealed interface CanvasKind : ComponentKind

val CanvasStyle = CssStyle<CanvasKind> {}

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
 * A millisecond value which, if used, will result in a 60FPS render.
 *
 * This is a value for the Canvas `minDeltaMs` parameter.
 */
const val ONE_FRAME_MS_60_FPS = 1000.0f / 60.0f

/**
 * A millisecond value which, if used, will result in a 30FPS render.
 *
 * This is a value for the Canvas `minDeltaMs` parameter.
 */
const val ONE_FRAME_MS_30_FPS = ONE_FRAME_MS_60_FPS * 2.0f

/**
 * A millisecond value which, if used, will result in a canvas that never repaints on its own.
 *
 * This is a value for the Canvas `minDeltaMs` parameter.
 *
 * This can be useful for a canvas that only ever is meant to paint exactly once or one whose repaints are triggered
 * manually by the caller via a [CanvasRepainter].
 *
 * If you pass in a repainter and don't explicitly set the `minDeltaMs` parameter, it will automatically be set to this
 * value.
 *
 * ```
 * val repainter = remember { CanvasRepainter() }
 * Canvas2d(
 *     500, 500,
 *     Modifier.onClick { repainter.repaint() },
 *     repainter = repainter // Automatically sets minDeltaMs to REPAINT_CANVAS_MANUALLY
 * ) {
 *     ctx.fillStyle = Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
 *     ctx.fillRect(0.0, 0.0, 500.0, 500.0)
 * }
 * ```
 */
const val REPAINT_CANVAS_MANUALLY = Float.MAX_VALUE

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
    private val onStepped: RenderCallback<C>.() -> Unit
) {
    private var lastRenderedTimestamp: Double = 0.0
    private val minDeltaMs = minDeltaMs.toDouble()
    private val maxDeltaMs = maxDeltaMs.toDouble()

    fun step(colorMode: ColorMode, force: Boolean = false, render: RenderScope<C>.() -> Unit) {
        val firstRender = lastRenderedTimestamp == 0.0
        val now = Date.now()
        val deltaMs = now - lastRenderedTimestamp
        if (firstRender || force || deltaMs >= minDeltaMs) {
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
    variant: CssStyleVariant<CanvasKind>?,
    repainter: CanvasRepainter?,
    minDeltaMs: Number,
    maxDeltaMs: Number,
    ref: ElementRefScope<HTMLCanvasElement>?,
    crossinline createContext: (HTMLCanvasElement) -> C?,
    noinline render: RenderScope<C>.() -> Unit,
) {
    // Hack-ish alert: We need to wrap `render` because if it changes on a subsequent callback, we do NOT want the
    // `TagElement` below to get recomposed (that will cause the canvas to flicker as it gets deallocated /
    // reallocated). Instead, we want to make sure that the closure grabs a wrapper class (here, `renderWrapped`
    // delegates to a `State<T>` wrapper class) so that the callback can check if the render has changed and if so,
    // call the new render method instead.
    val renderWrapped by rememberUpdatedState(newValue = render)
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

        val colorMode = ColorMode.current
        DisposableEffect(colorMode) {
            var requestId = 0
            val ctx = createContext(scopeElement)
            if (ctx != null) {
                // Subsequent renders, triggered when enough milliseconds have elapsed
                val callback: RenderCallback<C> = RenderCallback(ctx, width, height, minDeltaMs, maxDeltaMs, onStepped = {
                    requestId = window.requestAnimationFrame { step(colorMode, render = renderWrapped) }
                })
                // Initial render
                requestId = window.requestAnimationFrame { callback.step(colorMode, render = renderWrapped) }
                repainter?.repaintRequested = {
                    requestId = window.requestAnimationFrame { callback.step(colorMode, render = renderWrapped, force = true) }
                }
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
 * A helper class which lets callers trigger a repaint manually.
 *
 * Pass it into a [Canvas2d] (or [CanvasGl]) as follows:
 *
 * ```
 * val repainter = remember { CanvasRepainter() }
 * Canvas2d(
 *     500, 500,
 *     Modifier.onClick { repainter.repaint() },
 *     repainter = repainter
 * ) {
 *     ctx.fillStyle = Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
 *     ctx.fillRect(0.0, 0.0, 500.0, 500.0)
 * }
 * ```
 */
class CanvasRepainter {
    internal var repaintRequested: () -> Unit = {}
    fun repaint() {
        repaintRequested()
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
 * @param repainter If present, provides a handle that lets callers trigger a repaint manually. You should declare the
 *   [CanvasRepainter] instance inside a [remember] block.
 * @param render A callback which handles rendering a single frame.
 */
@Composable
fun Canvas2d(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<CanvasKind>? = null,
    repainter: CanvasRepainter? = null,
    minDeltaMs: Number = if (repainter != null) REPAINT_CANVAS_MANUALLY else 0.0,
    maxDeltaMs: Number = max(500.0, minDeltaMs.toDouble()),
    ref: ElementRefScope<HTMLCanvasElement>? = null,
    render: RenderScope<CanvasRenderingContext2D>.() -> Unit,
) {
    Canvas(
        width,
        height,
        modifier,
        variant,
        repainter,
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
 * @param repainter If present, provides a handle that lets callers trigger a repaint manually. You should declare the
 *   [CanvasRepainter] instance inside a [remember] block.
 * @param render A callback which handles rendering a single frame.
 */
@Composable
fun CanvasGl(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<CanvasKind>? = null,
    repainter: CanvasRepainter? = null,
    minDeltaMs: Number = if (repainter != null) REPAINT_CANVAS_MANUALLY else 0.0,
    maxDeltaMs: Number = max(500.0, minDeltaMs.toDouble()),
    ref: ElementRefScope<HTMLCanvasElement>? = null,
    render: RenderScope<WebGLRenderingContext>.() -> Unit,
) {
    Canvas(
        width,
        height,
        modifier,
        variant,
        repainter,
        minDeltaMs,
        maxDeltaMs,
        ref,
        { canvas -> canvas.getContext("webgl") as? WebGLRenderingContext },
        render
    )
}

/**
 * Renders a [Canvas] using the "webgl2" rendering context.
 *
 * See [CanvasGl] for more details about parameters, as they are the same.
 */
@Composable
fun CanvasGl2(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<CanvasKind>? = null,
    repainter: CanvasRepainter? = null,
    minDeltaMs: Number = if (repainter != null) REPAINT_CANVAS_MANUALLY else 0.0,
    maxDeltaMs: Number = max(500.0, minDeltaMs.toDouble()),
    ref: ElementRefScope<HTMLCanvasElement>? = null,
    render: RenderScope<WebGL2RenderingContext>.() -> Unit,
) {
    Canvas(
        width,
        height,
        modifier,
        variant,
        repainter,
        minDeltaMs,
        maxDeltaMs,
        ref,
        { canvas -> canvas.getContext("webgl2") as? WebGL2RenderingContext },
        render
    )
}
