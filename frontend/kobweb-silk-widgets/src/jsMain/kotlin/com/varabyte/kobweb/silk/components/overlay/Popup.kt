package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.dom.disposableRef
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

// A small but comfortable amount of space. Also allows the Tooltip composable to extend up a bit with an arrow while
// still leaving a bit of space to go.
const val DEFAULT_POPUP_OFFSET_PX = 15

/**
 * An enumeration for placing a popup outside of while still being aligned to another.
 *
 *      TL  T  TR
 *   LT +-------+ RT
 *      |       |
 *    L |       | R
 *      |       |
 *   LB +-------+ RB
 *      BL  B  BR
 *
 * Note the difference between e.g. [TopLeft] and [LeftTop]. The former will place the popup above the target, with
 * left edges aligned, while the latter will place to popup to the left of the target, with top edges aligned.
 *
 * Note that popups should avoid covering the element itself (as that would make the popup go away since it would cause
 * the mouseleave event to fire, removing the popup), so there is no option for `Center` placement.
 */
enum class PopupPlacement {
    TopLeft,
    Top,
    TopRight,
    LeftTop,
    RightTop,
    Left,
    Right,
    LeftBottom,
    RightBottom,
    BottomLeft,
    Bottom,
    BottomRight,
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
 *
 * @param target Indicates which element should listen for mouse enter and leave events in order to cause this popup to
 *   show up.
 * @param placementTarget If set, indicates which element the popup should be shown relative to. If not set, the
 *   original [target] will be used.
 */
@Composable
fun Popup(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    fun HTMLElement?.apply(targetFinder: ElementTarget?): HTMLElement? {
        if (this == null || targetFinder == null) return this
        return targetFinder(startingFrom = this)
    }

    var srcElement by remember { mutableStateOf<HTMLElement?>(null) }
    val targetElement by remember(srcElement, target) { mutableStateOf(srcElement.apply(target)) }
    val placementElement by remember(srcElement, targetElement, placementTarget) { mutableStateOf(
        if (placementTarget == null) targetElement else srcElement.apply(placementTarget)
    ) }

    var showPopup by remember { mutableStateOf(false) }
    val requestShowPopup: (Event) -> Unit = { showPopup = true }
    val requestHidePopup: (Event) -> Unit = { showPopup = false }

    Box(
        Modifier.display(DisplayStyle.None),
        ref = disposableRef(target) { element ->
            srcElement = element

            // Intentionally shadow here. We want our own copy to avoid infinite recompositions
            // when srcElement changes (srcElement changes targetElement changes this Box changes srcElement...)
            @Suppress("NAME_SHADOWING")
            val targetElement = element.apply(target)
            targetElement?.addEventListener("mouseenter", requestShowPopup)
            targetElement?.addEventListener("mouseleave", requestHidePopup)
            if (targetElement?.matches(":hover") == true) {
                showPopup = true
            }

            onDispose {
                targetElement?.removeEventListener("mouseenter", requestShowPopup)
                targetElement?.removeEventListener("mouseleave", requestHidePopup)
            }
        }
    )

    if (placementElement != null && showPopup) {
        @Suppress("NAME_SHADOWING")
        val placementElement = placementElement!!
        val targetBounds = placementElement.getBoundingClientRect()
        var popupBounds by remember { mutableStateOf<DOMRect?>(null) }
        @Suppress("NAME_SHADOWING")
        val offsetPixels = offsetPixels.toDouble()
        @Suppress("NAME_SHADOWING")
        val absPosModifier = popupBounds?.let { popupBounds ->
            when (placement) {
                PopupPlacement.TopLeft -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left).px)
                        .top((window.pageYOffset + targetBounds.top - offsetPixels - popupBounds.height).px)
                }
                PopupPlacement.Top -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((window.pageYOffset + targetBounds.top - offsetPixels - popupBounds.height).px)
                }
                PopupPlacement.TopRight -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left + (targetBounds.width - popupBounds.width)).px)
                        .top((window.pageYOffset + targetBounds.top - offsetPixels - popupBounds.height).px)
                }

                PopupPlacement.LeftTop -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - offsetPixels - popupBounds.width).px)
                        .top((window.pageYOffset + targetBounds.top).px)

                }
                PopupPlacement.RightTop -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.right + offsetPixels).px)
                        .top((window.pageYOffset + targetBounds.top).px)

                }

                PopupPlacement.Left -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - offsetPixels - popupBounds.width).px)
                        .top((window.pageYOffset + targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                }
                PopupPlacement.Right -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.right + offsetPixels).px)
                        .top((window.pageYOffset + targetBounds.top - (popupBounds.height - targetBounds.height) / 2).px)
                }

                PopupPlacement.LeftBottom -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - offsetPixels - popupBounds.width).px)
                        .top((window.pageYOffset + targetBounds.top + (targetBounds.height - popupBounds.height)).px)
                }
                PopupPlacement.RightBottom -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.right + offsetPixels).px)
                        .top((window.pageYOffset + targetBounds.top + (targetBounds.height - popupBounds.height)).px)
                }

                PopupPlacement.BottomLeft -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left).px)
                        .top((window.pageYOffset + targetBounds.bottom + offsetPixels).px)
                }
                PopupPlacement.Bottom -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left - (popupBounds.width - targetBounds.width) / 2).px)
                        .top((window.pageYOffset + targetBounds.bottom + offsetPixels).px)
                }
                PopupPlacement.BottomRight -> {
                    Modifier
                        .left((window.pageXOffset + targetBounds.left + (targetBounds.width - popupBounds.width)).px)
                        .top((window.pageYOffset + targetBounds.bottom + offsetPixels).px)
                }
            }
        }
            ?: Modifier
                // Hack - move the popup out of the way while we calculate its width, or else it can block the cursor
                // causing focus to be gained and lost
                .top((-100).percent).left((-100).percent)
                .opacity(0)

        if (showPopup) {
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
}
