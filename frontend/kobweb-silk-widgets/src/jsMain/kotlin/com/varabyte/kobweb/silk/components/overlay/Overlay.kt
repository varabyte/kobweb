package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.bottom
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

val OverlayStyle = ComponentStyle.base("silk-overlay") {
    Modifier.backgroundColor(colorMode.toSilkPalette().overlay)
}

/**
 * Renders a fullscreen overlay that is removed from the normal compose flow.
 *
 * In other words, any children content for this overlay will be parented under the overlay as a new root, and not
 * wherever in the compose hierarchy things happen to be.
 *
 * This class is particularly suited to opening a modal dialog on top of it. For example, if you had a `Dialog`
 * composable, you could do something like:
 *
 * ```
 * var showModal by remember { mutableStateOf(true) }
 * if (showModal) {
 *   Overlay(Modifer.onClick { showModal = false }) {
 *     Dialog {
 *        // ... your modal content here ...
 *     }
 *   }
 * }
 * ```
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 */
@Composable
fun Overlay(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopCenter,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit
) {
    deferRender {
        Box(
            OverlayStyle
                .toModifier()
                .position(Position.Fixed)
                .top(0.px).bottom(0.px).left(0.px).right(0.px)
                .then(modifier),
            contentAlignment = contentAlignment,
            ref = ref,
            content = content,
        )
    }
}