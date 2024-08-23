package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Circle
import com.varabyte.kobweb.compose.dom.svg.Group
import com.varabyte.kobweb.compose.dom.svg.Line
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.Polyline
import com.varabyte.kobweb.compose.dom.svg.Rect
import com.varabyte.kobweb.compose.dom.svg.SVGFillType
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineCap
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineJoin
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeType
import com.varabyte.kobweb.compose.dom.svg.SVGSvgAttrsScope
import com.varabyte.kobweb.compose.dom.svg.Svg
import com.varabyte.kobweb.compose.dom.svg.ViewBox
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.svg.SVGElement

// --------------------------------------------------------------------------------------------------------------------
// This file provides some basic SVG icons. Users will more likely reach to Font Awesome or Google Material icons, but
// SVG icons can be a simple way to get some quick icons working in your project without having to pull in a large
// dependency.
//
// Some of these icons in here are based on those found in Chakra UI: https://chakra-ui.com/docs/components/icon
// --------------------------------------------------------------------------------------------------------------------

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


@Composable
fun ArrowBackIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(20, 11)
                horizontalLineTo(7.83)
                lineTo(5.59, -5.59, isRelative = true)
                lineTo(12, 4)
                lineTo(-8, 8, isRelative = true)
                lineTo(8, 8, isRelative = true)
                lineTo(1.41, -1.41, isRelative = true)
                lineTo(7.83, 13)
                horizontalLineTo(20)
                verticalLineTo(-2, isRelative = true)
                closePath()
            }
        }
    }
}

@Composable
fun ArrowDownIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(20, 12)
                lineTo(-1.41, -1.41, isRelative = true)
                lineTo(13, 16.17)
                verticalLineTo(4)
                horizontalLineTo(-2, isRelative = true)
                verticalLineTo(12.17, isRelative = true)
                lineTo(-5.58, -5.59, isRelative = true)
                lineTo(4, 12)
                lineTo(8, 8, isRelative = true)
                lineTo(8, -8, isRelative = true)
                closePath()
            }
        }
    }
}

@Composable
fun ArrowForwardIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(12, 4)
                lineTo(-1.14, 1.41, isRelative = true)
                lineTo(16.17, 11)
                horizontalLineTo(4)
                verticalLineTo(2, isRelative = true)
                horizontalLineTo(12.17, isRelative = true)
                lineTo(-5.58, 5.59, isRelative = true)
                lineTo(12, 20)
                lineTo(8, -8, isRelative = true)
                closePath()
            }
        }
    }
}

@Composable
fun ArrowUpIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(4, 12)
                lineTo(1.41, 1.41, isRelative = true)
                lineTo(11, 7.83)
                verticalLineTo(20)
                horizontalLineTo(2, isRelative = true)
                verticalLineTo(7.83)
                lineTo(5.58, 5.59, isRelative = true)
                lineTo(20, 12)
                lineTo(-8, -8, isRelative = true)
                lineTo(-8, 8, isRelative = true)
                closePath()
            }
        }
    }
}

@Composable
fun AttachmentIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(21.843, 3.455)
                ellipticalArc(6.961, 6.961, 0, 0, 0, -9.846, 0, isRelative = true)
                lineTo(1.619, 13.832)
                ellipticalArc(5.128, 5.128, 0, 0, 0, 7.252, 7.252, isRelative = true)
                lineTo(17.3, 12.653)
                ellipticalArc(3.293, 3.293, 0, 1, 0, 12.646, 8)
                lineTo(7.457, 13.184)
                ellipticalArc(1, 1, 0, 1, 0, 8.871, 14.6)
                lineTo(14.06, 9.409)
                ellipticalArc(1.294, 1.294, 0, 0, 1, 1.829, 1.83, isRelative = true)
                lineTo(7.457, 19.67)
                ellipticalArc(3.128, 3.128, 0, 0, 1, -4.424, -4.424, isRelative = true)
                lineTo(13.411, 4.869)
                ellipticalArc(4.962, 4.962, 0, 1, 1, 7.018, 7.018, isRelative = true)
                lineTo(12.646, 19.67)
                ellipticalArc(1, 1, 0, 1, 0, 1.414, 1.414, isRelative = true)
                lineTo(21.843, 13.3)
                ellipticalArc(6.96, 6.96, 0, 0, 0, 0, -9.846, isRelative = true)
                closePath()
            }
        }
    }
}

@Composable
fun CheckIcon(modifier: Modifier = Modifier) {
    createIcon(ViewBox.sized(24, 20), renderStyle = IconRenderStyle.Stroke(4), attrs = modifier.toAttrs()) {
        Polyline {
            points(3 to 12, 9 to 18, 21 to 2)
        }
    }
}

@Composable
fun ChevronDownIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(16.59, 8.59)
                lineTo(12, 13.17)
                lineTo(7.41, 8.59)
                lineTo(6, 10)
                lineTo(6, 6, true)
                lineTo(6, -6, true)
                closePath()
            }
        }
    }
}

@Composable
fun ChevronLeftIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(15.41, 7.41)
                lineTo(14, 6)
                lineTo(-6, 6, true)
                lineTo(6, 6, true)
                lineTo(1.41, -1.41, true)
                lineTo(10.83, 12)
                closePath()
            }
        }
    }
}

@Composable
fun ChevronRightIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(10, 6)
                lineTo(8.59, 7.41)
                lineTo(13.17, 12)
                lineTo(-4.58, 4.59, true)
                lineTo(10, 18)
                lineTo(6, -6, true)
                closePath()
            }
        }
    }
}

@Composable
fun ChevronUpIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(12, 8)
                lineTo(-6, 6, true)
                lineTo(1.41, 1.41, true)
                lineTo(12, 10.83)
                lineTo(4.59, 4.58, true)
                lineTo(18, 14)
                closePath()
            }
        }
    }
}

@Composable
fun CircleIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Circle {
            cx(12)
            cy(12)
            r(8)
        }
    }
}

@Composable
fun CloseIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(3), attrs = modifier.toAttrs()) {
        Line {
            x1(1)
            x2(23)
            y1(1)
            y2(23)
        }
        Line {
            x1(23)
            x2(1)
            y1(1)
            y2(23)
        }
    }
}

@Composable
fun DownloadIcon(modifier: Modifier = Modifier) {
    createIcon(viewBox = ViewBox(0, 0, 14, 14), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(11.2857, 6.05714)
                lineTo(10.08571, 4.85714)
                lineTo(7.85714, 7.14786)
                lineTo(7.85714, 1)
                lineTo(6.14286, 1)
                lineTo(6.14286, 7.14786)
                lineTo(3.91429, 4.85714)
                lineTo(2.71429, 6.05714)
                lineTo(7, 10.42857)
                lineTo(11.2857, 6.05714)
                closePath()
                moveTo(1, 11.2857)
                lineTo(1, 13)
                lineTo(13, 13)
                lineTo(13, 11.2857)
                lineTo(1, 11.2857)
                closePath()
            }
        }
    }
}

@Composable
fun ExclaimIcon(modifier: Modifier = Modifier) {
    // From https://github.com/orgs/community/discussions/16925
    createIcon(ViewBox.sized(16), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M 0 1.75 C 0 0.784 0.784 0 1.75 0 h 12.5 C 15.216 0 16 0.784 16 1.75 v 9.5 A 1.75 1.75 0 0 1 14.25 13 H 8.06 l -2.573 2.573 A 1.458 1.458 0 0 1 3 14.543 V 13 H 1.75 A 1.75 1.75 0 0 1 0 11.25 Z m 1.75 -0.25 a 0.25 0.25 0 0 0 -0.25 0.25 v 9.5 c 0 0.138 0.112 0.25 0.25 0.25 h 2 a 0.75 0.75 0 0 1 0.75 0.75 v 2.19 l 2.72 -2.72 a 0.749 0.749 0 0 1 0.53 -0.22 h 6.5 a 0.25 0.25 0 0 0 0.25 -0.25 v -9.5 a 0.25 0.25 0 0 0 -0.25 -0.25 Z m 7 2.25 v 2.5 a 0.75 0.75 0 0 1 -1.5 0 v -2.5 a 0.75 0.75 0 0 1 1.5 0 Z M 9 9 a 1 1 0 1 1 -2 0 a 1 1 0 0 1 2 0 Z")
        }
    }
}

@Composable
fun HamburgerIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(3), attrs = modifier.toAttrs()) {
        for (y in listOf(3, 12, 21)) {
            Line {
                x1(0)
                x2(23)
                y1(y)
                y2(y)
            }
        }
    }
}

@Composable
fun IndeterminateIcon(modifier: Modifier = Modifier) {
    MinusIcon(modifier)
}

@Composable
fun InfoIcon(modifier: Modifier = Modifier) {
    // From https://github.com/orgs/community/discussions/16925
    createIcon(ViewBox.sized(16), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8Zm8-6.5a6.5 6.5 0 1 0 0 13 6.5 6.5 0 0 0 0-13ZM6.5 7.75A.75.75 0 0 1 7.25 7h1a.75.75 0 0 1 .75.75v2.75h.25a.75.75 0 0 1 0 1.5h-2a.75.75 0 0 1 0-1.5h.25v-2h-.25a.75.75 0 0 1-.75-.75ZM8 6a1 1 0 1 1 0-2 1 1 0 0 1 0 2Z")
        }
    }
}

@Composable
fun LightbulbIcon(modifier: Modifier = Modifier) {
    // From https://github.com/orgs/community/discussions/16925
    createIcon(ViewBox.sized(16), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M 8 1.5 c -2.363 0 -4 1.69 -4 3.75 c 0 0.984 0.424 1.625 0.984 2.304 l 0.214 0.253 c 0.223 0.264 0.47 0.556 0.673 0.848 c 0.284 0.411 0.537 0.896 0.621 1.49 a 0.75 0.75 0 0 1 -1.484 0.211 c -0.04 -0.282 -0.163 -0.547 -0.37 -0.847 a 8.456 8.456 0 0 0 -0.542 -0.68 c -0.084 -0.1 -0.173 -0.205 -0.268 -0.32 C 3.201 7.75 2.5 6.766 2.5 5.25 C 2.5 2.31 4.863 0 8 0 s 5.5 2.31 5.5 5.25 c 0 1.516 -0.701 2.5 -1.328 3.259 c -0.095 0.115 -0.184 0.22 -0.268 0.319 c -0.207 0.245 -0.383 0.453 -0.541 0.681 c -0.208 0.3 -0.33 0.565 -0.37 0.847 a 0.751 0.751 0 0 1 -1.485 -0.212 c 0.084 -0.593 0.337 -1.078 0.621 -1.489 c 0.203 -0.292 0.45 -0.584 0.673 -0.848 c 0.075 -0.088 0.147 -0.173 0.213 -0.253 c 0.561 -0.679 0.985 -1.32 0.985 -2.304 c 0 -2.06 -1.637 -3.75 -4 -3.75 Z M 5.75 12 h 4.5 a 0.75 0.75 0 0 1 0 1.5 h -4.5 a 0.75 0.75 0 0 1 0 -1.5 Z M 6 15.25 a 0.75 0.75 0 0 1 0.75 -0.75 h 2.5 a 0.75 0.75 0 0 1 0 1.5 h -2.5 a 0.75 0.75 0 0 1 -0.75 -0.75 Z")
        }
    }
}

@Composable
fun MinusIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(4), attrs = modifier.toAttrs()) {
        Line {
            x1(3)
            x2(21)
            y1(12)
            y2(12)
        }
    }
}

@Composable
fun PlusIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(4), attrs = modifier.toAttrs()) {
        Line {
            x1(3)
            x2(21)
            y1(12)
            y2(12)
        }
        Line {
            x1(12)
            x2(12)
            y1(3)
            y2(21)
        }
    }
}

@Composable
fun SquareIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Rect {
            x(4)
            y(4)
            width(16)
            height(16)
            rx(2)
        }
    }
}

@Composable
fun MoonIcon(modifier: Modifier = Modifier) {
    createIcon(ViewBox.sized(200), renderStyle = IconRenderStyle.Stroke(20), attrs = modifier.toAttrs()) {
        Path {
            d {
                moveTo(175, 106.583)
                ellipticalArc(75, 75, 0, 1, 1, 93.417, 25)
                ellipticalArc(58.333, 58.333, 0, 0, 0, 175, 106.583)
                closePath()
            }
        }
    }
}

@Composable
fun QuestionIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Path {
            strokeLineCap(SVGStrokeLineCap.Round)
            d("M9,9a3,3,0,1,1,4,2.829,1.5,1.5,0,0,0-1,1.415V14.25")
        }
        Path {
            strokeLineCap(SVGStrokeLineCap.Round)
            d("M12,17.25a.375.375,0,1,0,.375.375A.375.375,0,0,0,12,17.25h0")
        }
        Circle {
            strokeMiterLimit(10)
            cx(12)
            cy(12)
            r(11.25)
        }
    }
}

@Composable
fun QuoteIcon(modifier: Modifier = Modifier) {
    // Inspired by https://squidfunk.github.io/mkdocs-material/reference/admonitions/#+type:quote
    // From https://www.svgbackgrounds.com/elements/quotation-marks/
    // <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 300" fill="#FFF"><path d="m175.6 204.73 22.19 46.49C258.61 223.15 278 189.49 278 151.18V48.78H175.6v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55zm-153.6 0 22.19 46.49c60.83-28.07 80.21-61.73 80.21-100.04V48.78H22v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55z"></path></svg>
    // From url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 300" fill="%23FFF"><path d="m175.6 204.73 22.19 46.49C258.61 223.15 278 189.49 278 151.18V48.78H175.6v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55zm-153.6 0 22.19 46.49c60.83-28.07 80.21-61.73 80.21-100.04V48.78H22v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55z"></path></svg>')
    createIcon(viewBox = ViewBox.sized(300), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("m175.6 204.73 22.19 46.49C258.61 223.15 278 189.49 278 151.18V48.78H175.6v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55zm-153.6 0 22.19 46.49c60.83-28.07 80.21-61.73 80.21-100.04V48.78H22v102.4h51.2c0 15.64-12.42 35.66-51.2 53.55z")
        }
    }
}

@Composable
fun StopIcon(modifier: Modifier = Modifier) {
    // From https://github.com/orgs/community/discussions/16925
    createIcon(ViewBox.sized(16), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M 4.47 0.22 A 0.749 0.749 0 0 1 5 0 h 6 c 0.199 0 0.389 0.079 0.53 0.22 l 4.25 4.25 c 0.141 0.14 0.22 0.331 0.22 0.53 v 6 a 0.749 0.749 0 0 1 -0.22 0.53 l -4.25 4.25 A 0.749 0.749 0 0 1 11 16 H 5 a 0.749 0.749 0 0 1 -0.53 -0.22 L 0.22 11.53 A 0.749 0.749 0 0 1 0 11 V 5 c 0 -0.199 0.079 -0.389 0.22 -0.53 Z m 0.84 1.28 L 1.5 5.31 v 5.38 l 3.81 3.81 h 5.38 l 3.81 -3.81 V 5.31 L 10.69 1.5 Z M 8 4 a 0.75 0.75 0 0 1 0.75 0.75 v 3.5 a 0.75 0.75 0 0 1 -1.5 0 v -3.5 A 0.75 0.75 0 0 1 8 4 Z m 0 8 a 1 1 0 1 1 0 -2 a 1 1 0 0 1 0 2 Z")
        }
    }
}

@Composable
fun SunIcon(modifier: Modifier = Modifier) {
    createIcon(renderStyle = IconRenderStyle.Stroke(2), attrs = modifier.toAttrs()) {
        Group(attrs = {
            strokeLineJoin(SVGStrokeLineJoin.Round)
            strokeLineCap(SVGStrokeLineCap.Round)
        }) {
            Circle {
                cx(12)
                cy(12)
                r(5)
            }
            Path {
                d {
                    moveTo(12, 1)
                    verticalLineTo(2, true)
                }
            }
            Path {
                d {
                    moveTo(12, 21)
                    verticalLineTo(2, true)
                }
            }
            Path {
                d {
                    moveTo(4.22, 4.22)
                    lineTo(1.42, 1.42, true)
                }
            }
            Path {
                d {
                    moveTo(18.36, 18.36)
                    lineTo(1.42, 1.42, true)
                }
            }
            Path {
                d {
                    moveTo(1, 12)
                    horizontalLineTo(2, true)
                }
            }
            Path {
                d {
                    moveTo(21, 12)
                    horizontalLineTo(2, true)
                }
            }
            Path {
                d {
                    moveTo(4.22, 19.78)
                    lineTo(1.42, -1.42, true)
                }
            }
            Path {
                d {
                    moveTo(18.36, 5.64)
                    lineTo(1.42, -1.42, true)
                }
            }
        }
    }
}

@Composable
fun WarningIcon(modifier: Modifier = Modifier) {
    // From https://github.com/orgs/community/discussions/16925
    createIcon(ViewBox.sized(16), renderStyle = IconRenderStyle.Fill(), attrs = modifier.toAttrs()) {
        Path {
            d("M 6.457 1.047 c 0.659 -1.234 2.427 -1.234 3.086 0 l 6.082 11.378 A 1.75 1.75 0 0 1 14.082 15 H 1.918 a 1.75 1.75 0 0 1 -1.543 -2.575 Z m 1.763 0.707 a 0.25 0.25 0 0 0 -0.44 0 L 1.698 13.132 a 0.25 0.25 0 0 0 0.22 0.368 h 12.164 a 0.25 0.25 0 0 0 0.22 -0.368 Z m 0.53 3.996 v 2.5 a 0.75 0.75 0 0 1 -1.5 0 v -2.5 a 0.75 0.75 0 0 1 1.5 0 Z M 9 11 a 1 1 0 1 1 -2 0 a 1 1 0 0 1 2 0 Z")
        }
    }
}
