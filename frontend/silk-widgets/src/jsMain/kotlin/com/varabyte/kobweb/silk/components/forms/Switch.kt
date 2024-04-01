package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentKind
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.CssStyle
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.ariaDisabled
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.vars.animation.TransitionDurationVars
import com.varabyte.kobweb.silk.components.style.vars.color.FocusOutlineColorVar
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.colors.palette.Palette
import com.varabyte.kobweb.silk.theme.colors.palette.switch
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Label
import org.w3c.dom.HTMLElement

object SwitchVars {
    // 9999px forces a pill shape. 0px causes a rectangular shape.
    val BorderRadius by StyleVariable<CSSLengthNumericValue>(prefix = "silk", defaultFallback = 9999.px)

    val TrackWidth by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val TrackHeight by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val TrackPadding by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val TrackBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val FocusColor by StyleVariable(prefix = "silk", defaultFallback = FocusOutlineColorVar.value())

    val ThumbOffset by StyleVariable<CSSLengthOrPercentageNumericValue>(prefix = "silk") // Should be less than switch height
    val ThumbColor by StyleVariable<CSSColorValue>(prefix = "silk")

    val TransitionDuration by StyleVariable(prefix = "silk", defaultFallback = TransitionDurationVars.Fast.value())
}

interface SwitchKind : ComponentKind {
    interface Track : ComponentKind
    interface Thumb : ComponentKind
}
val SwitchStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .position(Position.Relative) // So the hidden <input> is positioned relative to the switch root
}

val SwitchTrackStyle by ComponentStyle<SwitchKind.Track>(
    prefix = "silk",
    extraModifiers = Modifier.tabIndex(-1).ariaHidden()
) {
    base {
        Modifier
            .width(SwitchVars.TrackWidth.value())
            .minWidth(SwitchVars.TrackWidth.value())
            .height(SwitchVars.TrackHeight.value())
            .minHeight(SwitchVars.TrackHeight.value())
            .padding(SwitchVars.TrackPadding.value())
            .borderRadius(SwitchVars.BorderRadius.value())
            .backgroundColor(SwitchVars.TrackBackgroundColor.value())
            .transition(CSSTransition("background-color", duration = SwitchVars.TransitionDuration.value()))
            .boxSizing(BoxSizing.ContentBox)
    }

    (hover + not(ariaDisabled)) { Modifier.cursor(Cursor.Pointer) }
}

val SwitchInputVariant by InputStyle.addVariant {
    base { HiddenInputModifier }

    // Since the checkbox is hidden, we highlight its sibling (the switch track) when the checkbox is focused(-visible).
    cssRule(":focus-visible + *") {
        Modifier.boxShadow(spreadRadius = 0.1875.cssRem, color = SwitchVars.FocusColor.value())
    }
}

val SwitchThumbStyle by ComponentStyle.base<SwitchKind.Thumb>(prefix = "silk") {
    Modifier.size(SwitchVars.TrackHeight.value())
        .borderRadius(SwitchVars.BorderRadius.value())
        .backgroundColor(SwitchVars.ThumbColor.value())
        .translateX(SwitchVars.ThumbOffset.value())
        .transition(CSSTransition("translate", duration = SwitchVars.TransitionDuration.value()))
}

class SwitchSize(
    val width: CSSLengthNumericValue,
    val height: CSSLengthNumericValue,
    val padding: CSSLengthNumericValue = 0.188.cssRem
) : CssStyle.Base({
    Modifier
        .setVariable(SwitchVars.TrackWidth, width)
        .setVariable(SwitchVars.TrackHeight, height)
        .setVariable(SwitchVars.TrackPadding, padding)
}) {
    companion object {
        val SM = SwitchSize(1.375.cssRem, 0.75.cssRem)
        val MD = SwitchSize(1.875.cssRem, 1.cssRem)
        val LG = SwitchSize(2.875.cssRem, 1.5.cssRem)
    }
}

enum class SwitchShape {
    PILL,
    RECTANGLE,
}

internal fun SwitchShape.toModifier() = Modifier
    .thenIf(this == SwitchShape.RECTANGLE) { Modifier.setVariable(SwitchVars.BorderRadius, 0.px) }

/**
 * Creates a toggleable switch.
 *
 * Note that visual control of the switch is fairly limited compared to many other widgets -- you can't directly modify
 * the width or height of the track or the thumb parts. Instead, configure your switch by passing in the relevant
 * parameters.
 *
 * Note that this widget is backed by a checkbox input. Use the `ref` callback if you need access to it:
 *
 * ```
 * ref = ref { element -> element.getElementsByTagName("input")[0] as HTMLInputElement }
 * ```
 *
 * @param checked Whether the switch is currently checked or not.
 * @param onCheckedChange A callback which is invoked when the switch is toggled.
 * @param modifier The modifier to apply to the *container* of this switch element. This will not be applied to the
 *   switch itself (since its configuration comes from the other parameters).
 * @param enabled Whether the switch is enabled or not. If not, the switch will be rendered in a disabled state and will
 *   not be interactable.
 * @param size The size of the switch. Defaults to [SwitchSize.MD]. You can implement your own [SwitchSize] if you want
 *   custom sizing.
 * @param colorScheme An optional color scheme to use for the switch. If not provided, the switch will use the
 *   appropriate colors from the [Palette].
 * @param thumbColor An optional override for the color of the thumb.
 * @param focusBorderColor An optional override for the border color when the input is focused.
 * @param ref Provides a reference to the *container* of the switch. Its direct children will be the underlying checkbox
 *   element and the switch track, whose direct child will be the thumb element.
 */
// TODO: should this take a trackVariant and thumbVariant, like Tabs does?
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant<SwitchKind>? = null,
    enabled: Boolean = true,
    size: SwitchSize = SwitchSize.MD,
    shape: SwitchShape = SwitchShape.PILL,
    colorScheme: ColorScheme? = null,
    thumbColor: CSSColorValue? = null,
    focusBorderColor: CSSColorValue? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    val colorMode = ColorMode.current
    val switchPalette = colorMode.toPalette().switch
    // Use a label so it intercepts clicks and passes them to the inner Input
    Label(
        attrs = SwitchStyle.toModifier(variant)
            .then(size.toModifier())
            .then(shape.toModifier())
            .then(modifier)
            .toAttrs()
    ) {
        registerRefScope(ref)
        // We base Switch on a checkbox input for a11y + built-in input/keyboard support, but hide the checkbox itself
        // and render the switch separately. We do however allow it to be focused, which combined with the outer label
        // means that both clicks and keyboard events will toggle the checkbox.
        Input(
            type = InputType.Checkbox,
            value = checked,
            onValueChanged = { onCheckedChange(!checked) },
            variant = SwitchInputVariant,
            enabled = enabled,
        )
        Box(
            SwitchTrackStyle.toModifier()
                .setVariable(
                    SwitchVars.TrackBackgroundColor,
                    if (checked) colorScheme?.let { if (colorMode.isDark) it._200 else it._700 }
                        ?: switchPalette.backgroundOn else switchPalette.backgroundOff
                )
                .setVariable(SwitchVars.ThumbColor, thumbColor)
                .setVariable(SwitchVars.FocusColor, focusBorderColor)
                .thenIf(!enabled) { DisabledStyle.toModifier() }
        ) {
            Box(
                SwitchThumbStyle.toModifier()
                    .setVariable(
                        SwitchVars.ThumbOffset,
                        if (checked) size.width - size.height else 0.percent
                    )
            )
        }
    }
}
