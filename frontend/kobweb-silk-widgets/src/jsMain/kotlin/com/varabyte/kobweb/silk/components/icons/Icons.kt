package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.Circle
import com.varabyte.kobweb.compose.dom.Line
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
    strokeWidth: Int = 2,
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
    createIcon(viewBox = "0 0 12 10") {
        Polyline(attrs = {
            attr("points", "1.5 6 4.5 9 10.5 1")
        })
    }
}

@Composable
fun CircleIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 1, fill = "currentColor") {
        Circle {
            attr("cx", "12")
            attr("cy", "12")
            attr("r", "8")
        }
    }
}

@Composable
fun IndeterminateIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 4) {
        Line(attrs = {
            attr("x1", "3")
            attr("x2", "21")
            attr("y1", "12")
            attr("y2", "12")
        })
    }
}

@Composable
fun MinusIcon() {
    IndeterminateIcon()
}

@Composable
fun PlusIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 4) {
        Line(attrs = {
            attr("x1", "3")
            attr("x2", "21")
            attr("y1", "12")
            attr("y2", "12")
        })
        Line(attrs = {
            attr("x1", "12")
            attr("x2", "12")
            attr("y1", "3")
            attr("y2", "21")
        })
    }
}

@Composable
fun SquareIcon() {
    createIcon(viewBox = "0 0 24 24", strokeWidth = 1, fill = "currentColor") {
        Rect {
            attr("x", "4")
            attr("y", "4")
            attr("width", "16")
            attr("height", "16")
            attr("rx", "2")
        }
    }
}
