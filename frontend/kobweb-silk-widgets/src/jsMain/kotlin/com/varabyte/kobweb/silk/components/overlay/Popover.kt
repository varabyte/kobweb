package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.CSSTransition
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.web.css.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventListener

val PopoverStyle by ComponentStyle(prefix = "silk-") {
    base {
        Modifier.transition(CSSTransition("opacity", 150.ms))
    }
}

// When first declared, popups need to make several passes to set themselves up. First, they need to find the raw
// html elements that will be associated with the popup's location. Then, they need to calculate the width of the popup,
// which requires the raw element of the popup itself.
private sealed interface PopoverState {
    object Uninitialized : PopoverState

    sealed interface Initialized : PopoverState {
        var elements: PopoverElements
    }

    class FoundElements(override var elements: PopoverElements) : Initialized

    sealed interface Visible : Initialized {
        val modifier: Modifier
    }

    sealed interface Showing : Visible

    /** State for when we're about to show the popup, but we need a bit of time to calculate its width. */
    class Calculating(override var elements: PopoverElements) : Showing {
        override val modifier = Modifier
            // Hack - move the popup out of the way while we calculate its width, or else it can block the cursor
            // causing focus to be gained and lost
            .top((-100).percent).left((-100).percent)
            .opacity(0)
    }
    class Shown(override var elements: PopoverElements, placement: PopupPlacement, bounds: DOMRect, offsetPixels: Number) : Showing {
        private fun getAbsModifier(
            placement: PopupPlacement,
            popupBounds: DOMRect,
            targetBounds: DOMRect,
            offsetPixels: Double,
        ): Modifier {
            return when (placement) {
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

        override val modifier = run {
            val targetBounds = elements.placementElement.getBoundingClientRect()
            getAbsModifier(
                placement,
                bounds,
                targetBounds,
                offsetPixels.toDouble()
            )
        }

    }
    class Hiding(override var elements: PopoverElements, modifier: Modifier) : Visible {
        override val modifier = modifier.opacity(0)
    }
}

private class PopoverStateController(
    private val placement: PopupPlacement,
    private val offsetPixels: Number,
    private val showDelayMs: Int,
    private val hideDelayMs: Int,
    private val stayOpenStrategy: StayOpenStrategy,
) {
    private var _state by mutableStateOf<PopoverState>(PopoverState.Uninitialized)
    val state get() = _state

    private var showTimeoutId = -1
    private var hideTimeoutId = -1

    private fun resetTimers() {
        window.clearTimeout(showTimeoutId)
        window.clearTimeout(hideTimeoutId)
    }

    fun resetToFoundElements() {
        val state = _state
        check(state is PopoverState.Initialized)
        resetTimers()
        _state = PopoverState.FoundElements(state.elements)
    }

    fun updateElements(elements: PopoverElements) {
        val state = _state
        if (state is PopoverState.Initialized) {
            state.elements = elements
        } else {
            _state = PopoverState.FoundElements(elements)
        }
    }

    fun requestShowPopup() {
        val state = _state
        if (state !is PopoverState.Initialized) return

        resetTimers()
        showTimeoutId = window.setTimeout({
            this._state = PopoverState.Calculating(state.elements)
            // Sometimes, we can end up having a show request happen before a hiding finishes. In that case, we can
            // bypass the calculation step and jump straight into showing the popup
            state.elements.popupElement
                // If the popup element was disposed and recreated at some point, its size will need to be recalculated.
                ?.takeIf { it.getBoundingClientRect().let { rect -> rect.width * rect.height } > 0 }
                ?.let { finishShowing(it) }
        }, showDelayMs)
    }

    fun updatePopupElement(popupElement: HTMLElement) {
        val state = _state
        check(state is PopoverState.Initialized)

        stayOpenStrategy.init(popupElement)
        state.elements.popupElement = popupElement
    }

    fun clearPopupElement() {
        val state = _state
        check(state is PopoverState.Initialized)

        stayOpenStrategy.reset()
        state.elements.popupElement = null
    }

    fun finishShowing(popupElement: HTMLElement) {
        val state = _state
        if(state !is PopoverState.Calculating) return

        _state = PopoverState.Shown(
            state.elements,
            placement,
            popupElement.getBoundingClientRect(),
            offsetPixels
        )
    }

    fun requestHidePopup() {
        val state = _state
        if (state is PopoverState.FoundElements) {
            resetTimers()
            return
        }
        check(state is PopoverState.Visible)

        resetTimers()
        hideTimeoutId = window.setTimeout({
            if (!stayOpenStrategy.shouldStayOpen) {
                val currentOpacity = state.elements.popupElement?.let { window.getComputedStyle(it).getPropertyValue("opacity").toDouble() }
                this._state = PopoverState.Hiding(state.elements, state.modifier)
                // Normally, the "hiding" state is marked finished once the "onTransitionEnd" event is reached (see
                // later in this file). However, if the following condition is true, it means we're in a state that the
                // event would never fire, so just fire the "finish hiding" event directly.
                if (currentOpacity == null || currentOpacity == 0.0) finishHiding(state.elements)
            } // else a new hide request will be issued automatically when shouldStayOpen is false
        }, hideDelayMs)
    }

    fun finishHiding(elements: PopoverElements) {
        val state = _state
        if (state !is PopoverState.Hiding) return

        _state = PopoverState.FoundElements(elements)
        resetTimers()
    }

    init {
        stayOpenStrategy.stayOpenFlow.onEach { stayOpen ->
            if (!stayOpen) requestHidePopup()
        }.launchIn(CoroutineScope(window.asCoroutineDispatcher()))
    }
}

private class PopoverElements(
    srcElement: HTMLElement,
    popupTarget: ElementTarget,
    placementTarget: ElementTarget?
) {
    private fun HTMLElement?.resolve(targetFinder: ElementTarget?): HTMLElement? {
        if (this == null || targetFinder == null) return this
        return targetFinder(startingFrom = this)
    }

    val targetElement = srcElement.resolve(popupTarget) ?: error("Target element finder returned null")
    val placementElement = if (placementTarget == null) targetElement else
        (srcElement.resolve(placementTarget) ?: error("Placement element finder returned null"))

    // Kind of a hack, but this field is exposed so that the "Show" step can store its raw element somewhere. That way,
    // if we end up back in the "Calculation" state later (this can happen when you flail your mouse cursor wildly
    // across multiple elements that have popups attached to them), we can fast-forward to the "Show" step.
    var popupElement: HTMLElement? = null
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
 * @param showDelayMs If set, there will be a delay before the popup is shown after the mouse enters the target.
 * @param hideDelayMs If set, there will be a delay before the popup is hidden after the mouse leaves the target.
 * @param stayOpenStrategy Once a popup is open, this strategy controls how it should decide to stay open. If no
 *   strategy is passed in, the popup will stay open as long as the mouse is over it or if any child inside of it has
 *   focus. See also: [StayOpenStrategy].
 */
@Composable
fun Popover(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    offsetPixels: Number = DEFAULT_POPUP_OFFSET_PX,
    placementTarget: ElementTarget? = null,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    stayOpenStrategy: StayOpenStrategy? = null,
    variant: ComponentVariant? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val stayOpenStrategy = remember(stayOpenStrategy) {
        stayOpenStrategy ?: CompositeStayOpenStrategy(
            IsMouseOverStayOpenStrategy(),
            HasFocusStayOpenStrategy()
        )
    }
    val popoverStateController =
        remember(placement, offsetPixels, showDelayMs, hideDelayMs, stayOpenStrategy) {
            PopoverStateController(
                placement, offsetPixels,
                showDelayMs.coerceAtLeast(0), hideDelayMs.coerceAtLeast(0),
                stayOpenStrategy
            )
        }

    // Create a dummy element whose purpose is to search for the target element that we want to attach a popup to.
    Box(
        Modifier.display(DisplayStyle.None),
        ref = disposableRef(popoverStateController, target, placementTarget) { element ->
            val requestShowPopupListener = EventListener { popoverStateController.requestShowPopup() }
            val requestHidePopupListener = EventListener { popoverStateController.requestHidePopup() }

            var popoverElements: PopoverElements? = null
            try {
                popoverElements = PopoverElements(element, target, placementTarget).apply {
                    // The popupElement is created in the deferRender block and it should carry over across this
                    // "element finder" element being recreated.
                    popupElement = (popoverStateController.state as? PopoverState.Initialized)?.elements?.popupElement
                }
                popoverElements.targetElement.apply {
                    addEventListener("mouseenter", requestShowPopupListener)
                    addEventListener("mouseleave", requestHidePopupListener)
                    addEventListener("focusin", requestShowPopupListener)
                    addEventListener("focusout", requestHidePopupListener)
                }
                popoverStateController.updateElements(popoverElements)
            } catch (_: IllegalStateException) {}
            onDispose {
                popoverElements?.targetElement?.apply {
                    removeEventListener("mouseenter", requestShowPopupListener)
                    removeEventListener("mouseleave", requestHidePopupListener)
                    removeEventListener("focusin", requestShowPopupListener)
                    removeEventListener("focusout", requestHidePopupListener)
                }
            }
        }
    )

    // Copy into local var for smart casting.
    deferRender {
        val visiblePopoverState = (popoverStateController.state as? PopoverState.Visible) ?: return@deferRender
        Box(
            PopoverStyle.toModifier(variant)
                .position(Position.Absolute)
                .then(visiblePopoverState.modifier)
                .then(modifier)
                .onTransitionEnd { evt ->
                    val state = popoverStateController.state
                    if (evt.propertyName == "opacity" && state is PopoverState.Hiding) {
                        popoverStateController.finishHiding(state.elements)
                    }
                },
            ref = refScope {
                disposableRef { popupElement ->
                    popoverStateController.updatePopupElement(popupElement)
                    popoverStateController.finishShowing(popupElement)
                    onDispose {
                        popoverStateController.clearPopupElement()
                        popoverStateController.resetToFoundElements()
                    }
                }
                add(ref)
            },
            content = content
        )
    }
}
