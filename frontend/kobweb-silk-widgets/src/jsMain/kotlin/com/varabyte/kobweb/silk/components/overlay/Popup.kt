package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.transitionDuration
import com.varabyte.kobweb.compose.ui.modifiers.transitionProperty
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import org.jetbrains.compose.web.css.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

/**
 * An enumeration for placing a popup outside of while still being aligned to another.
 *
 * Popups should avoid covering the element itself
 */
enum class PopupPlacement {
    Top,
    Left,
    Right,
    Bottom,
    // TODO(#199): Add more locations
    //     TL  TR
    //  LT +----+ RT
    //     |    |
    //  LB +----+ RB
    //     BL  BR
    //  Also, maybe auto? As a way to avoid tooltips or popups going off screen
}

val PopupStyle = ComponentStyle.base("silk-popup") {
    Modifier
        .opacity(1)
        .transitionProperty("opacity")
        .transitionDuration(100.ms)
}

/**
 * Render a general, undecorated composable in a location above and outside of some target element.
 *
 * See also [Tooltip], which wraps your composable in a sort of chat bubble, making it particularly well-suited for
 * text tooltips.
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 */
@Composable
fun Popup(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    variant: ComponentVariant? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    var targetElement by remember { mutableStateOf<HTMLElement?>(null) }
    Box(
        Modifier.display(DisplayStyle.None),
        ref = ref { element ->
            target(startingFrom = element)?.apply {
                onmouseenter = { targetElement = this; Unit }
                onmouseleave = { targetElement = null; Unit }
            }
        }
    )

    targetElement?.let { element ->
        val targetBounds = element.getBoundingClientRect()
        var popupBounds by remember { mutableStateOf<DOMRect?>(null) }
        @Suppress("NAME_SHADOWING")
        val absPosModifier = popupBounds?.let { popupBounds ->
            when (placement) {
                PopupPlacement.Top -> {
                    Modifier
                        .left((targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((targetBounds.top - 10 - popupBounds.height).px)
                }
                PopupPlacement.Bottom -> {
                    Modifier
                        .left((targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((targetBounds.bottom + 10).px)
                }
                PopupPlacement.Left -> {
                    Modifier
                        .top((targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                        .left((targetBounds.left - 10 - popupBounds.width).px)
                }
                PopupPlacement.Right -> {
                    Modifier
                        .top((targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                        .left((targetBounds.right + 10).px)
                }
            }
        }
        // Hack - move the popup out of the way while we calculate its width, or else it can block the cursor
        // causing focus to be gained and lost
            ?: Modifier.top((-100).percent).left((-100).percent).opacity(0) // Hide for a frame or so until we've calculated the final popup location

        deferRender {
            // Need to set targetElement as the key because otherwise you might move from one element to another so fast
            // that compose doesn't realize the tooltip should be rerendered
            key(targetElement) {
                Box(
                    PopupStyle.toModifier(variant).position(Position.Absolute).then(absPosModifier).then(modifier),
                    ref = ref { element ->
                        popupBounds = element.getBoundingClientRect()
                    },
                    content = content
                )
            }
        }
    }
}
