package com.varabyte.kobweb.silk.components.icons

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.svg.Circle
import com.varabyte.kobweb.compose.dom.svg.Group
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineCap
import com.varabyte.kobweb.compose.dom.svg.SVGStrokeLineJoin
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs

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