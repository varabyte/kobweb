package com.varabyte.kobweb.silk.components.overlay

import com.varabyte.kobweb.browser.dom.observers.ResizeObserver
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.LayoutScopeMarker
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.vars.animation.TransitionDurationVars
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.events.EventListener

object PopupVars {
    val TransitionDuration by StyleVariable(prefix = "silk", defaultFallback = TransitionDurationVars.Fast.value())
}

val PopupStyle by ComponentStyle.base(prefix = "silk") {
    // NOTE: If any user replaces this style in their own project, they should make sure they still keep this "opacity"
    // transition in their version, even if they change the duration. Otherwise, the popup will break, as it currently
    // uses the "opacity" transition event to detect when it should close.
    Modifier.transition(CSSTransition("opacity", PopupVars.TransitionDuration.value()))
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
 * left edges aligned, while the latter will place the popup to the left of the target, with top edges aligned.
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
@LayoutScopeMarker
class PopupScope(val placement: PopupPlacement?) : BoxScope

private fun HTMLElement.updatePosition(position: PopupPlacementStrategy.Position) {
    style.top = "${position.top}"
    style.left = "${position.left}"
}

/**
 * A contract to control how a popup should be placed relative to some placement element.
 *
 * See [calculate], which must be implemented by any implementing classes.
 */
abstract class PopupPlacementStrategy {
    class Position(val top: CSSLengthNumericValue, val left: CSSLengthNumericValue)
    class PositionAndPlacement(val position: Position, val placement: PopupPlacement? = null)

    /**
     * Initialize this strategy with values it needs to calculate the final position and placement of a popup.
     *
     * @param placementElement The element in the DOM that this popup should be placed relative to.
     * @param popupElement The backing element for the popup itself. Its position at this point is not yet finalized,
     *   so you should usually just need to check its size values (i.e. width and height).
     */
    abstract fun init(placementElement: HTMLElement, popupElement: HTMLElement)

    /**
     * Returns the absolute position and placement that a popup should be placed at.
     *
     * The absolute position is very important, as it is used to position the popup on screen.
     *
     * Specifying the placement is optional. Some widgets may use it to decorate themselves, like tooltips using it to
     * add an appropriate arrow (e.g. left placements result in an arrow on the right), but it is not always required.
     *
     * This method will always be called after [init] and is expected to use the two elements provided to it as part of
     * this calculation.
     */
    abstract fun calculate(): PositionAndPlacement

    /**
     * Clear any state that this strategy may have been holding onto, called when the popup closes.
     */
    abstract fun reset()

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
        fun of(placement: PopupPlacement, offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX) =
            object : PopupPlacementStrategy() {
                private var placementElement: HTMLElement? = null
                private var popupElement: HTMLElement? = null

                private var resizeObserver: ResizeObserver? = null
                private var mutationObserver: MutationObserver? = null

                private fun updatePopupPosition() {
                    popupElement!!.updatePosition(calculate().position)
                }

                private val updatePopupPositionListener = EventListener { updatePopupPosition() }

                override fun init(placementElement: HTMLElement, popupElement: HTMLElement) {
                    this.placementElement = placementElement
                    this.popupElement = popupElement

                    resizeObserver = ResizeObserver { _ -> updatePopupPosition() }.apply {
                        observe(popupElement)
                        observe(placementElement)
                    }
                    // Follow placement element if it moves around (e.g. margin changes)
                    mutationObserver = MutationObserver { _, _ -> updatePopupPosition() }.apply {
                        observe(
                            placementElement,
                            MutationObserverInit(attributes = true, attributeFilter = arrayOf("style"))
                        )
                    }

                    window.addEventListener("scroll", updatePopupPositionListener)
                    window.addEventListener("resize", updatePopupPositionListener)
                }

                override fun calculate(): PositionAndPlacement {
                    val placementBounds = placementElement!!.getBoundingClientRect()
                    val popupBounds = popupElement!!.getBoundingClientRect()
                    val popupWidth = popupBounds.width
                    val popupHeight = popupBounds.height

                    // TODO: Add fallback behavior, so if the requested placement ends up with the popup off the screen,
                    //  try opposite sides so the final placement is always on screen.
                    return PositionAndPlacement(
                        calculateDefaultPosition(placement, popupWidth, popupHeight, placementBounds, offsetPixels),
                        placement
                    )
                }

                override fun reset() {
                    placementElement = null
                    popupElement = null

                    resizeObserver!!.disconnect(); resizeObserver = null
                    mutationObserver!!.disconnect(); mutationObserver = null

                    window.removeEventListener("scroll", updatePopupPositionListener)
                    window.removeEventListener("resize", updatePopupPositionListener)

                }
            }

        /**
         * A helper method when you care about the offset of the placement but not the direction.
         */
        fun of(offsetPixels: Number) = of(PopupPlacement.Bottom, offsetPixels)
    }
}
