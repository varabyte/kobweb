package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.StyleScope

fun StyleScope.backgroundBlendMode(vararg blendModes: MixBlendMode) {
    property("background-blend-mode", blendModes.joinToString())
}