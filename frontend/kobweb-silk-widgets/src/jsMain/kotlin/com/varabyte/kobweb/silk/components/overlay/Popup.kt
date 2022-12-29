package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

// A small but comfortable amount of space. Also allows the Tooltip composable to extend up a bit with an arrow while
// still leaving a bit of space to go.
const val DEFAULT_POPUP_OFFSET_PX = 15

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

val PopupStyle = ComponentStyle("silk-popup") {
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
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
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
        val offsetPixels = offsetPixels.toDouble()
        @Suppress("NAME_SHADOWING")
        val absPosModifier = popupBounds?.let { popupBounds ->
            when (placement) {
                PopupPlacement.Top -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((window.pageYOffset + targetBounds.top - offsetPixels - popupBounds.height).px)
                }
                PopupPlacement.Bottom -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((window.pageYOffset + targetBounds.bottom + offsetPixels).px)
                }
                PopupPlacement.Left -> {
                    Modifier
                        .top((window.pageYOffset + targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                        .left((window.pageXOffset + targetBounds.left - offsetPixels - popupBounds.width).px)
                }
                PopupPlacement.Right -> {
                    Modifier
                        .top((window.pageYOffset + targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                        .left((window.pageXOffset + targetBounds.right + offsetPixels).px)
                }
            }
        }
            ?: Modifier
                // Hack - move the popup out of the way while we calculate its width, or else it can block the cursor
                // causing focus to be gained and lost
                .top((-100).percent).left((-100).percent)
                .opacity(0)

        deferRender {
            // Need to set targetElement as the key because otherwise you might move from one element to another so fast
            // that compose doesn't realize the tooltip should be rerendered
            key(targetElement) {
                Box(
                    PopupStyle.toModifier(variant)
                        .position(Position.Absolute)
                        .then(absPosModifier)
                        .then(modifier),
                    ref = refScope {
                        ref { element ->
                            popupBounds = element.getBoundingClientRect()
                        }
                        add(ref)
                    },
                    content = content
                )
            }
        }
    }
}
