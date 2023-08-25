package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.Line
import com.varabyte.kobweb.compose.dom.Polyline
import com.varabyte.kobweb.compose.dom.Svg

@Composable
fun CheckIcon() {
    Svg(attrs = {
        attr("width", "1.2em")
        attr("viewBox", "0 0 12 10")
        style {
            property("fill", "none")
            property("stroke", "currentColor")
            property("stroke-width", "2")
        }
    }) {
        Polyline(attrs = {
            attr("points", "1.5 6 4.5 9 10.5 1")
        })
    }
}

@Composable
fun IndeterminateIcon() {
    Svg(attrs = {
        attr("width", "1.2em")
        attr("viewBox", "0 0 24 24")
        style {
            property("stroke", "currentColor")
            property("stroke-width", "4")
        }
    }) {
        Line(attrs = {
            attr("x1", "3")
            attr("x2", "21")
            attr("y1", "12")
            attr("y2", "12")
        })
    }

}
