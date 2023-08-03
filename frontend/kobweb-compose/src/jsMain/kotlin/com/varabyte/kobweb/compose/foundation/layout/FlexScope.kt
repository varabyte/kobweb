package com.varabyte.kobweb.compose.foundation.layout

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*

interface FlexScope {
    // Convenient remapping to "flexGrow" for users coming from the world of Android
    fun Modifier.weight(value: Number) = this.flexGrow(value)
}
