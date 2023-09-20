package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.Circle
import com.varabyte.kobweb.compose.dom.Group
import com.varabyte.kobweb.compose.dom.Line
import com.varabyte.kobweb.compose.dom.Path
import com.varabyte.kobweb.compose.dom.Polyline
import com.varabyte.kobweb.compose.dom.Rect
import com.varabyte.kobweb.compose.dom.Svg
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.svg.SVGElement

// --------------------------------------------------------------------------------------------------------------------
// This file provides some basic SVG icons. Users will more likely reach to Font Awesome or Google Material icons, but
// SVG icons can be a simple way to get some quick icons working in your project without having to pull in a large
// dependency.
// --------------------------------------------------------------------------------------------------------------------

// NOTE: This API is sloppy with params. Revisit if we ever want to make it public. Possibly come up with better SVG API
// support first, instead of setting attrs everywhere.
@Composable
private fun createIcon(
    viewBox: String,
    width: CSSLengthValue = 1.2.em,
    strokeWidth: Int = 1,
    fill: String? = "none",
    content: @Composable ElementScope<SVGElement>.() -> Unit
) {
    Svg(attrs = {
        attr("width", width.toString())
        attr("viewBox", viewBox)
        style {
            fill?.let { property("fill", it) }
            property("stroke", "currentColor")
            property("stroke-width", strokeWidth)
        }
    }, content)
}

@Composable
fun CheckIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 4) {
        Polyline {
            points(3 to 12, 9 to 19, 21 to 2)
        }
    }
}

@Composable
fun ChevronDownIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 2) {
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
fun ChevronLeftIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 2) {
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
fun ChevronRightIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 2) {
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
fun ChevronUpIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 2) {
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
fun CircleIcon() {
    createIcon(viewBox = "0 0 24 24", fill = "currentColor") {
        Circle {
            cx(12)
            cy(12)
            r(8)
        }
    }
}

@Composable
fun IndeterminateIcon() {
    MinusIcon()
}

@Composable
fun MinusIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 4) {
        Line {
            x1(3)
            x2(21)
            y1(12)
            y2(12)
        }
    }
}

@Composable
fun PlusIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 4) {
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
fun SquareIcon() {
    createIcon(viewBox = "0 0 24 24", fill = "currentColor") {
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
fun MoonIcon() {
    createIcon(viewBox = "0 0 200 200", strokeWidth = 20) {
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
fun SunIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 2) {
        Group(attrs = {
            attr("stroke-linejoin", "round")
            attr("stroke-linecap", "round")
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