package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.defer.Deferred
import com.varabyte.kobweb.silk.defer.DeferringHost
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

object OverlayVars {
    val BackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
}

sealed interface OverlayKind : ComponentKind

val OverlayStyle = CssStyle.base<OverlayKind> {
    Modifier.backgroundColor(OverlayVars.BackgroundColor.value())
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
 *   Overlay(Modifier.onClick { showModal = false }) {
 *     Dialog {
 *        // ... your modal content here ...
 *     }
 *   }
 * }
 * ```
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [DeferringHost] yourself
 * first, as a parent method that this lives under. See the method for more details.
 */
@Composable
fun Overlay(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<OverlayKind>? = null,
    contentAlignment: Alignment = Alignment.TopCenter,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Deferred {
        Box(
            OverlayStyle.toModifier(variant)
                .position(Position.Fixed)
                .top(0.px).bottom(0.px).left(0.px).right(0.px)
                .then(modifier),
            contentAlignment = contentAlignment,
            ref = ref,
            content = content,
        )
    }
}
