package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.dom.disposableRef
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
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
import org.w3c.dom.HTMLElement

// Convenience class to collect a bunch of parameters into a single place
private class PopoverShowHideSettings(
    hiddenModifier: Modifier,
    showDelayMs: Int,
    hideDelayMs: Int,
) {
    val showDelayMs = showDelayMs.coerceAtLeast(0)
    val hideDelayMs = hideDelayMs.coerceAtLeast(0)
    val hiddenModifier = hiddenModifier.opacity(0)
}

private fun PopupPlacementStrategy.Position.toModifier() = Modifier.top(top).left(left)

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
        val placement: PopupPlacement?
        val modifier: Modifier
    }

    sealed interface Showing : Visible

    /** State for when we're about to show the popup, but we need a bit of time to calculate its width. */
    class Calculating(override var elements: PopoverElements, showHideSettings: PopoverShowHideSettings) : Showing {
        override val modifier = showHideSettings.hiddenModifier
            // Hack - move the popup out of the way while we calculate its width, or else it can block the cursor
            // causing focus to be gained and lost
            .top((-100).percent).left((-100).percent)

        override val placement = null
    }

    class Shown(
        override var elements: PopoverElements,
        placementStrategy: PopupPlacementStrategy,
    ) : Showing {
        private val positionAndPlacement = placementStrategy.calculate()

        override val modifier = Modifier.then(positionAndPlacement.position.toModifier())
        override val placement = positionAndPlacement.placement
    }

    class Hiding(
        override var elements: PopoverElements,
        placementStrategy: PopupPlacementStrategy,
        showHideSettings: PopoverShowHideSettings,
        override val placement: PopupPlacement?,
    ) : Visible {
        override val modifier = showHideSettings
            .hiddenModifier
            // If the popup element is null, that means the popup is closed and our placementStrategy should be
            // considered uninitialized. We can get here in this case if the popup is hidden before it has a chance to
            // show. In that case, it will still be invisible so the placement strategy doesn't need to be consulted in
            // that case anyway.
            .thenIf(elements.popupElement != null) { placementStrategy.calculate().position.toModifier() }
    }
}

private class PopoverStateController(
    openCloseStrategy: OpenClosePopupStrategy,
    private val showHideSettings: PopoverShowHideSettings,
    private val placementStrategy: PopupPlacementStrategy,
    private val keepOpenStrategy: KeepPopupOpenStrategy,
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
            this._state = PopoverState.Calculating(state.elements, showHideSettings)
            // Sometimes, we can end up having a show request happen before a hiding finishes. In that case, we can
            // bypass the calculation step and jump straight into showing the popup
            state.elements.popupElement
                // If the popup element was disposed and recreated at some point, its size will need to be recalculated.
                ?.takeIf { it.getBoundingClientRect().let { rect -> rect.width * rect.height } > 0 }
                ?.let { finishShowing() }
        }, showHideSettings.showDelayMs)
    }

    fun updatePopupElement(popupElement: HTMLElement) {
        val state = _state
        check(state is PopoverState.Initialized)

        keepOpenStrategy.init(popupElement)
        state.elements.popupElement = popupElement
    }

    fun clearPopupElement() {
        val state = _state
        check(state is PopoverState.Initialized)

        keepOpenStrategy.reset()
        state.elements.popupElement = null
    }

    fun finishShowing() {
        val state = _state
        if (state !is PopoverState.Calculating) return

        val popupElement = state.elements.popupElement
        check(popupElement != null)

        _state = PopoverState.Shown(state.elements, placementStrategy)
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
            if (!keepOpenStrategy.shouldKeepOpen) {
                val currentOpacity = state.elements.popupElement?.let {
                    window.getComputedStyle(it).getPropertyValue("opacity").toDouble()
                }
                this._state = PopoverState.Hiding(state.elements, placementStrategy, showHideSettings, state.placement)
                // Normally, the "hiding" state is marked finished once the "onTransitionEnd" event is reached (see
                // later in this file). However, if the following condition is true, it means we're in a state that the
                // event would never fire, so just fire the "finish hiding" event directly.
                if (currentOpacity == null || currentOpacity == 0.0) finishHiding(state.elements)
            } // else a new hide request will be issued automatically when shouldKeepOpen is false
        }, showHideSettings.hideDelayMs)
    }

    fun finishHiding(elements: PopoverElements) {
        val state = _state
        if (state !is PopoverState.Hiding) return

        _state = PopoverState.FoundElements(elements)
        resetTimers()
    }

    init {
        val scope = CoroutineScope(window.asCoroutineDispatcher())

        keepOpenStrategy.keepOpenFlow.onEach { keepOpen ->
            if (!keepOpen) requestHidePopup()
        }.launchIn(scope)

        openCloseStrategy.requestFlow.onEach { request ->
            when (request) {
                OpenClose.OPEN -> requestShowPopup()
                OpenClose.CLOSE -> requestHidePopup()
            }
        }.launchIn(scope)
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
 * Render a general, undecorated composable in a location above and outside some target element.
 *
 * This method should be configurable enough for a majority of cases, but [AdvancedPopover] is also provided for people
 * who need even more control.
 *
 * See also: [Tooltip], which wraps your composable in a sort of chat bubble, making it particularly well-suited for
 * text tooltips.
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 *
 * @param target Indicates which element should listen for mouse enter and leave events in order to cause this popup to
 *   show up and hide.
 * @param offsetPixels How many pixels the popup should be offset from the target element.
 * @param placementTarget If set, indicates which element the popup should be shown relative to. If not set, the
 *   original [target] will be used.
 * @param showDelayMs If set, there will be a delay before the popup is shown after the mouse enters the target.
 * @param hideDelayMs If set, there will be a delay before the popup is hidden after the mouse leaves the target.
 * @param keepOpenStrategy Once a popup is open, this strategy controls how it should decide to stay open. If no
 *   strategy is passed in, the popup will stay open as long as the mouse is over it or if any child inside of it has
 *   focus. See also: [KeepPopupOpenStrategy].
 */
@Composable
fun Popover(
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
    val placementStrategy = remember(placement, offsetPixels) { PopupPlacementStrategy.of(placement, offsetPixels) }

    AdvancedPopover(
        target = target,
        modifier = modifier,
        showDelayMs = showDelayMs,
        hideDelayMs = hideDelayMs,
        placementTarget = placementTarget,
        placementStrategy = placementStrategy,
        keepOpenStrategy = keepOpenStrategy,
        variant = variant,
        ref = ref,
        content = content
    )
}

/**
 * A more generally configurable version of [Popover], with more control at the cost of more verbosity.
 *
 * Please see the header docs for the other Popover method. Only new parameters will be documented here.
 *
 * @param hiddenModifier An additional modifier to apply when the popup is in its hidden state. You can use this to
 *   create contrasting animations between when the popup is hidden and visible, e.g. by adding a scaling or panning
 *   effect. Note that popups will always have their opacity set to 0 when hidden, so you don't need to specify that.
 * @param openCloseStrategy A strategy to control when the popup should open and close. If not specified, the popup
 *   will open when the user either hovers over or sets focus to the target element.
 * @param placementStrategy A strategy to control the popup's final placement and position. If not specified, the popup
 *   will use a default strategy that places the popover below the target element.
 */
@Composable
fun AdvancedPopover(
    target: ElementTarget,
    modifier: Modifier = Modifier,
    hiddenModifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    showDelayMs: Int = 0,
    hideDelayMs: Int = 0,
    openCloseStrategy: OpenClosePopupStrategy? = null,
    placementTarget: ElementTarget? = null,
    placementStrategy: PopupPlacementStrategy? = null,
    keepOpenStrategy: KeepPopupOpenStrategy? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: @Composable PopupScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val openCloseStrategy = remember(openCloseStrategy) {
        openCloseStrategy ?: (OpenClosePopupStrategy.onHover() + OpenClosePopupStrategy.onFocus())
    }

    val showHideSettings =
        remember(hiddenModifier, showDelayMs, hideDelayMs) {
            PopoverShowHideSettings(
                hiddenModifier,
                showDelayMs,
                hideDelayMs
            )
        }

    @Suppress("NAME_SHADOWING")
    val placementStrategy =
        remember(placementStrategy) { placementStrategy ?: PopupPlacementStrategy.of(PopupPlacement.Bottom) }

    @Suppress("NAME_SHADOWING")
    val keepOpenStrategy = remember(keepOpenStrategy) {
        keepOpenStrategy ?: (KeepPopupOpenStrategy.onHover() + KeepPopupOpenStrategy.onFocus())
    }
    val popoverStateController =
        remember(openCloseStrategy, showHideSettings, placementStrategy, keepOpenStrategy) {
            PopoverStateController(openCloseStrategy, showHideSettings, placementStrategy, keepOpenStrategy)
        }

    // Create a dummy element whose purpose is to search for the target element that we want to attach a popup to.
    Box(
        Modifier.display(DisplayStyle.None),
        ref = disposableRef(popoverStateController, target, placementTarget) { element ->
            try {
                val popoverElements = PopoverElements(element, target, placementTarget).apply {
                    // The popupElement is created in the deferRender block below with its own lifecycle, and it should
                    // carry over across this "element finder" element being disposed and recreated.
                    popupElement = (popoverStateController.state as? PopoverState.Initialized)?.elements?.popupElement
                }
                popoverElements.targetElement.apply { openCloseStrategy.init(this) }
                popoverStateController.updateElements(popoverElements)
            } catch (_: IllegalStateException) {
            }
            onDispose { openCloseStrategy.reset() }
        }
    )

    // Copy into local var for smart casting.
    deferRender {
        val visiblePopoverState = (popoverStateController.state as? PopoverState.Visible) ?: return@deferRender
        Box(
            PopupStyle.toModifier(variant)
                .position(Position.Fixed)
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
                    placementStrategy.init(visiblePopoverState.elements.placementElement, popupElement)
                    popoverStateController.updatePopupElement(popupElement)
                    popoverStateController.finishShowing()

                    onDispose {
                        popoverStateController.clearPopupElement()
                        popoverStateController.resetToFoundElements()
                        placementStrategy.reset()
                    }
                }
                add(ref)
            },
        ) {
            PopupScope(placement = visiblePopoverState.placement).content()
        }
    }
}
