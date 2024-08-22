package com.varabyte.kobweb.compose

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import org.jetbrains.compose.web.css.*

@Composable
fun KobwebComposeStyles() {
    Style(KobwebComposeStyleSheet)
}
