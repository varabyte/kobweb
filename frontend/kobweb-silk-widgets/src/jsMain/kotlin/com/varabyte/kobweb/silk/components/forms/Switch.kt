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
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.ariaDisabled
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.colors.FocusOutlineColorVar
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.shapes.RectF
import com.varabyte.kobweb.silk.theme.shapes.clip
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Label
import org.w3c.dom.HTMLElement

// 9999px forces a pill shape. 0px causes a rectangular shape.
val SwitchBorderRadiusVar by StyleVariable<CSSLengthValue>(prefix = "silk", defaultFallback = 9999.px)

val SwitchTrackWidthVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackHeightVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackPaddingVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackBackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val SwitchFocusColorVar by StyleVariable(prefix = "silk", defaultFallback = FocusOutlineColorVar.value())

val SwitchThumbOffsetVar by StyleVariable<CSSLengthOrPercentageValue>(prefix = "silk") // Should be less than switch height
val SwitchThumbColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val SwitchStyle by ComponentStyle(prefix = "silk") {}

val SwitchTrackStyle by ComponentStyle(prefix = "silk", extraModifiers = Modifier.tabIndex(-1).ariaHidden()) {
    base {
        Modifier
            .position(Position.Relative) // So input can be positioned absolutely without affecting the layout
            .width(SwitchTrackWidthVar.value())
            .minWidth(SwitchTrackWidthVar.value())
            .height(SwitchTrackHeightVar.value())
            .minHeight(SwitchTrackHeightVar.value())
            .padding(SwitchTrackPaddingVar.value())
            .borderRadius(SwitchBorderRadiusVar.value())
            .backgroundColor(SwitchTrackBackgroundColorVar.value())
            .transition(CSSTransition("background-color", duration = 150.ms))
            .boxSizing(BoxSizing.ContentBox)
    }

    (hover + not(ariaDisabled)) { Modifier.cursor(Cursor.Pointer) }
}

val SwitchInputVariant by InputStyle.addVariant {
    // We hide the checkbox itself since the Switch is rendered separately, but keep it a11y-friendly by only limiting
    // its size/appearance (instead of explicitly hiding), matching the approach of many other libraries.
    // See Switch for more context.
    base {
        Modifier
            .border(0.px)
            .size(1.px)
            .margin((-1).px)
            .padding(0.px)
            .clip(RectF(50f))
            .overflow(Overflow.Hidden)
            .whiteSpace(WhiteSpace.NoWrap)
            .position(Position.Absolute)
    }
    // Since the checkbox is hidden, we highlight its sibling (the switch track) when the checkbox is focused(-visible).
    cssRule(":focus-visible + *") {
        Modifier.boxShadow(spreadRadius = 0.1875.cssRem, color = SwitchFocusColorVar.value())
    }
}

val SwitchThumbStyle by ComponentStyle.base(prefix = "silk") {
    Modifier.size(SwitchTrackHeightVar.value())
        .borderRadius(SwitchBorderRadiusVar.value())
        .backgroundColor(SwitchThumbColorVar.value())
        .translateX(SwitchThumbOffsetVar.value())
        .transition(CSSTransition("translate", duration = 150.ms))
}

interface SwitchSize {
    val width: CSSLengthValue
    val height: CSSLengthValue
    val padding: CSSLengthValue get() = 0.188.cssRem

    object SM : SwitchSize {
        override val width = 1.375.cssRem
        override val height = 0.75.cssRem
    }

    object MD : SwitchSize {
        override val width = 1.875.cssRem
        override val height = 1.cssRem
    }

    object LG : SwitchSize {
        override val width = 2.875.cssRem
        override val height = 1.5.cssRem
    }
}

fun SwitchSize.toModifier() = Modifier
    .setVariable(SwitchTrackWidthVar, width)
    .setVariable(SwitchTrackHeightVar, height)
    .setVariable(SwitchTrackPaddingVar, padding)

enum class SwitchShape {
    PILL,
    RECTANGLE,
}

internal fun SwitchShape.toModifier() = Modifier
    .thenIf(this == SwitchShape.RECTANGLE) { Modifier.setVariable(SwitchBorderRadiusVar, 0.px) }

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
 *   appropriate colors from the [SilkPalette].
 * @param thumbColor An optional override for the color of the thumb.
 * @param focusBorderColor An optional override for the border color when the input is focused.
 * @param ref Provides a reference to the *container* of the switch. Its direct children will be the underlying checkbox
 *   element and the switch track, whose direct child will be the thumb element.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    enabled: Boolean = true,
    size: SwitchSize = SwitchSize.MD,
    shape: SwitchShape = SwitchShape.PILL,
    colorScheme: ColorScheme? = null,
    thumbColor: CSSColorValue? = null,
    focusBorderColor: CSSColorValue? = null,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    val colorMode = ColorMode.current
    val switchPalette = colorMode.toSilkPalette().switch
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
                    SwitchTrackBackgroundColorVar,
                    if (checked) colorScheme?.let { if (colorMode.isDark) it._200 else it._700 }
                        ?: switchPalette.backgroundOn else switchPalette.backgroundOff
                )
                .thenIf(thumbColor != null) { Modifier.setVariable(SwitchThumbColorVar, thumbColor!!) }
                .thenIf(focusBorderColor != null) { Modifier.setVariable(SwitchFocusColorVar, focusBorderColor!!) }
                .thenIf(!enabled) { DisabledStyle.toModifier() }
        ) {
            Box(
                SwitchThumbStyle.toModifier()
                    .setVariable(
                        SwitchThumbOffsetVar,
                        if (checked) (size.width - size.height).unsafeCast<CSSLengthOrPercentageValue>() else 0.percent
                    )
            )
        }
    }
}
