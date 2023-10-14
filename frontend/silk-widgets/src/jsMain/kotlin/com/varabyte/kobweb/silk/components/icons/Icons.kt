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
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.svg.SVGElement

// --------------------------------------------------------------------------------------------------------------------
// This file provides some basic SVG icons. Users will more likely reach to Font Awesome or Google Material icons, but
// SVG icons can be a simple way to get some quick icons working in your project without having to pull in a large
// dependency.
// --------------------------------------------------------------------------------------------------------------------

private class ViewBox(val x: Int, val y: Int, val width: Int, val height: Int) {
    companion object {
        fun sized(width: Int, height: Int = width) = ViewBox(0, 0, width, height)
    }
}

sealed interface IconRenderStyle {
    @Suppress("CanSealedSubClassBeObject") // May add Fill parameters someday
    class Fill : IconRenderStyle
    class Stroke(val strokeWidth: Number? = null) : IconRenderStyle
}

// NOTE: This API is sloppy with params. Revisit if we ever want to make it public. Possibly come up with better SVG API
// support first, instead of setting attrs everywhere.
@Composable
private fun createIcon(
    viewBox: ViewBox = ViewBox.sized(24),
    width: CSSLengthValue = 1.2.em,
    renderStyle: IconRenderStyle? = IconRenderStyle.Stroke(),
    attrs: (SVGSvgAttrsScope.() -> Unit)? = null,
    content: ContentBuilder<SVGElement>
) {
    Svg(attrs = {
        width(width)
        viewBox(viewBox.x, viewBox.y, viewBox.width, viewBox.height)
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
