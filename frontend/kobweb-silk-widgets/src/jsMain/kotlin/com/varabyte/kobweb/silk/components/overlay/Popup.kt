package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import org.jetbrains.compose.web.css.CSSLengthValue
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement

val PopupStyle by ComponentStyle.base(prefix = "silk-") {
    // NOTE: If any user replaces this style in their own project, they should make sure they still keep this "opacity"
    // transition in their version, even if they change the duration. Otherwise, the popup will break, as it currently
    // uses the "opacity" transition event to detect when it should close.
    Modifier.transition(CSSTransition("opacity", 150.ms))
}

/** A small but comfortable amount of space between a popup and its target. */
const val DEFAULT_POPUP_OFFSET_PX = 15

/**
 * An enumeration for placing a popup outside of while still being aligned to another.
 *
 *       TL  T  TR
 *    LT +-------+ RT
 *       |       |
 *     L |       | R
 *       |       |
 *    LB +-------+ RB
 *       BL  B  BR
 *
 * Note the difference between e.g. [PopupPlacement.TopLeft] and [PopupPlacement.LeftTop]. The former will place the popup above the target, with
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

/**
 * The scope for the content of a popup.
 *
 * Note that this is essentially a [BoxScope] with some extra information added to it relevant to popups.
 */
class PopupScope(val placement: PopupPlacement?) {
    fun Modifier.align(alignment: Alignment) = BoxScope().apply { align(alignment) }
}

/**
 * A contract to control how a popup should be placed relative to some placement element.
 *
 * See [calculate], which must be implemented by any implementing classes.
 */
abstract class PopupPlacementStrategy {
    class Position(val top: CSSLengthValue, val left: CSSLengthValue)
    class PositionAndPlacement(val position: Position, val placement: PopupPlacement? = null)

    /**
     * Returns the absolute position and placement that a popup should be placed at.
     *
     * The absolute position is very important, as it is used to position the popup on screen.
     *
     * Specifying the placement is optional. Some widgets may use it to decorate themselves, like tooltips using it to
     * add an appropriate arrow (e.g. left placements result in an arrow on the right), but it is not always required.
     *
     * This method is given two HTML elements to use for the final calculation. These should be treated as read-only,
     * and final results should be considered unexpected if you modify them.
     *
     * @param placementElement The element in the DOM that this popup should be placed relative to.
     * @param popupElement The backing element for the popup itself. Its position at this point is not yet finalized,
     *   so you should usually just need to check its size values (i.e. width and height).
     */
    abstract fun calculate(placementElement: HTMLElement, popupElement: HTMLElement): PositionAndPlacement

    /**
     * A method that provides a reasonable position calculation for a popup.
     *
     * This is the logic used by the default [PopupPlacementStrategy] implementation, but provided as a convenience for
     * other subclasses to use if they want to.
     */
    protected fun calculateDefaultPosition(
        placement: PopupPlacement,
        popupWidth: Double, popupHeight: Double,
        placementBounds: DOMRect,
        offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    ): Position {
        @Suppress("NAME_SHADOWING") val offsetPixels = offsetPixels.toDouble()
        return when (placement) {
            PopupPlacement.TopLeft -> {
                Position(
                    top = (placementBounds.top - offsetPixels - popupHeight).px,
                    left = (placementBounds.left).px,
                )
            }

            PopupPlacement.Top -> {
                Position(
                    top = (placementBounds.top - offsetPixels - popupHeight).px,
                    left = (placementBounds.left - (popupWidth - placementBounds.width) / 2).px,
                )
            }

            PopupPlacement.TopRight -> {
                Position(
                    top = (placementBounds.top - offsetPixels - popupHeight).px,
                    left = (placementBounds.left + (placementBounds.width - popupWidth)).px,
                )
            }

            PopupPlacement.LeftTop -> {
                Position(
                    top = (placementBounds.top).px,
                    left = (placementBounds.left - offsetPixels - popupWidth).px,
                )
            }

            PopupPlacement.RightTop -> {
                Position(
                    top = (placementBounds.top).px,
                    left = (placementBounds.right + offsetPixels).px,
                )
            }

            PopupPlacement.Left -> {
                Position(
                    top = (placementBounds.top - (popupHeight - placementBounds.height) / 2).px,
                    left = (placementBounds.left - offsetPixels - popupWidth).px,
                )
            }

            PopupPlacement.Right -> {
                Position(
                    top = (placementBounds.top - (popupHeight - placementBounds.height) / 2).px,
                    left = (placementBounds.right + offsetPixels).px,
                )
            }

            PopupPlacement.LeftBottom -> {
                Position(
                    top = (placementBounds.top + (placementBounds.height - popupHeight)).px,
                    left = (placementBounds.left - offsetPixels - popupWidth).px,
                )
            }

            PopupPlacement.RightBottom -> {
                Position(
                    top = (placementBounds.top + (placementBounds.height - popupHeight)).px,
                    left = (placementBounds.right + offsetPixels).px,
                )
            }

            PopupPlacement.BottomLeft -> {
                Position(
                    top = (placementBounds.bottom + offsetPixels).px,
                    left = (placementBounds.left).px,
                )
            }

            PopupPlacement.Bottom -> {
                Position(
                    top = (placementBounds.bottom + offsetPixels).px,
                    left = (placementBounds.left - (popupWidth - placementBounds.width) / 2).px,
                )
            }

            PopupPlacement.BottomRight -> {
                Position(
                    top = (placementBounds.bottom + offsetPixels).px,
                    left = (placementBounds.left + (placementBounds.width - popupWidth)).px,
                )
            }
        }
    }


    companion object {
        /**
         * Returns the general strategy of placing a popup in a particular location based on a desired [PopupPlacement].
         */
        fun of(placement: PopupPlacement, offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX) = object : PopupPlacementStrategy() {
            override fun calculate(placementElement: HTMLElement, popupElement: HTMLElement): PositionAndPlacement {
                val placementBounds = placementElement.getBoundingClientRect()
                val popupBounds = popupElement.getBoundingClientRect()
                val popupWidth = popupBounds.width
                val popupHeight = popupBounds.height

                // TODO: Add fallback behavior, so if the requested placement ends up with the popup off the screen,
                //  try opposite sides so the final placement is always on screen.
                return PositionAndPlacement(
                    calculateDefaultPosition(placement, popupWidth, popupHeight, placementBounds, offsetPixels),
                    placement
                )
            }
        }
    }
}

/**
 * Render a general, undecorated composable in a location above and outside some target element.
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
 * @param showDelayMs If set, there will be a delay before the popup is shown after the mouse enters the target.
 * @param hideDelayMs If set, there will be a delay before the popup is hidden after the mouse leaves the target.
 * @param keepOpenStrategy Once a popup is open, this strategy controls how it should decide to stay open. If no
 *   strategy is passed in, the popup will stay open as long as the mouse is over it or if any child inside of it has
 *   focus. See also: [KeepPopupOpenStrategy].
 */
@Deprecated("Popup has been renamed to Popover", ReplaceWith("Popover(target, modifier, placement, offsetPixels, placementTarget, showDelayMs, hideDelayMs, keepOpenStrategy, variant, ref, content)"))
@Composable
fun Popup(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    placement: PopupPlacement = PopupPlacement.Bottom,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable PopupScope.() -> Unit,
) {
    Popover(
        target = target,
        modifier = modifier,
        placement = placement,
        offsetPixels = offsetPixels,
        placementTarget = placementTarget,
        showDelayMs = showDelayMs,
        hideDelayMs = hideDelayMs,
        keepOpenStrategy = keepOpenStrategy,
        variant = variant,
        ref = ref,
        content = content
    )
}