package com.varabyte.kobweb.compose.foundation.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*

@LayoutScopeMarker
@Immutable // TODO(#554): Remove annotation after upstream fix
interface FlexScope {
    // Convenient remapping to "flexGrow" for users coming from the world of Android
    fun Modifier.weight(value: Number) = this.flexGrow(value)
}
