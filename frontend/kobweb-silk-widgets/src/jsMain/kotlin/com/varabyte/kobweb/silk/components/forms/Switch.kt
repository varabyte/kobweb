// Seems like Compose HTML CSSCalcValue instances don't remember the type they came from
@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")

package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.common.ariaDisabled
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.colors.SilkPalette
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLElement

// 9999px forces a pill shape. 0px causes a rectangular shape.
val SwitchBorderRadiusVar by StyleVariable<CSSLengthValue>(prefix = "silk", defaultFallback = 9999.px)

val SwitchTrackWidthVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackHeightVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackPaddingVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val SwitchTrackBackgroundColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val SwitchThumbOffsetVar by StyleVariable<CSSLengthOrPercentageValue>(prefix = "silk") // Should be less than switch height
val SwitchThumbColorVar by StyleVariable<CSSColorValue>(prefix = "silk") // Should be less than switch height

val SwitchStyle by ComponentStyle(prefix = "silk") {}

val SwitchTrackStyle by ComponentStyle(prefix = "silk", extraModifiers = Modifier.tabIndex(0)) {
    base {
        Modifier
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

val SwitchThumbStyle by ComponentStyle.base(prefix = "silk") {
    Modifier.size(SwitchTrackHeightVar.value())
        .borderRadius(SwitchBorderRadiusVar.value())
        .backgroundColor(Colors.White)
        .translateX(SwitchThumbOffsetVar.value())
        .transition(CSSTransition("translate", duration = 150.ms))
}

interface SwitchSize {
    val width: CSSLengthValue
    val height: CSSLengthValue
    val padding: CSSLengthValue get() = 3.px

    object SM : SwitchSize {
        override val width = 22.px
        override val height = 12.px
    }

    object MD : SwitchSize {
        override val width = 30.px
        override val height = 16.px
    }

    object LG : SwitchSize {
        override val width = 46.px
        override val height = 24.px
    }
}

internal fun SwitchSize.toModifier() = Modifier
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
 * @param checked Whether the switch is currently checked or not.
 * @param onCheckedChange A callback which is invoked when the switch is toggled.
 * @param modifier The modifier to apply to the *container* of this switch element. This will not be applied to the
 *   switch itself (since its configuration comes from the other parameters).
 * @param contentAlignment How to align the switch within its container. This should only be relevant if you pass in a
 *   [modifier] value that makes the size of the container larger than the switch itself. Defaults to
 *   [Alignment.CenterStart].
 * @param enabled Whether the switch is enabled or not. If not, the switch will be rendered in a disabled state and will
 *   not be interactable.
 * @param size The size of the switch. Defaults to [SwitchSize.MD]. You can implement your own [SwitchSize] if you want
 *   custom sizing.
 * @param colorScheme An optional color scheme to use for the switch. If not provided, the switch will use the
 *   appropriate colors from the [SilkPalette].
 * @param ref Provides a reference to the *container* of the switch. Its direct child will be the switch track, whose
 *   direct child will be the thumb element.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    contentAlignment: Alignment = Alignment.CenterStart,
    enabled: Boolean = true,
    size: SwitchSize = SwitchSize.MD,
    colorScheme: ColorScheme? = null,
    shape: SwitchShape = SwitchShape.PILL,
    ref: ElementRefScope<HTMLElement>? = null,
) {
    val colorMode = getColorMode()
    val switchPalette = colorMode.toSilkPalette().switch
    Box(SwitchStyle.toModifier(variant).then(size.toModifier().then(shape.toModifier())), contentAlignment, ref = ref) {
        Box(
            modifier = SwitchTrackStyle.toModifier()
                .setVariable(
                    SwitchTrackBackgroundColorVar,
                    if (checked) colorScheme?.let { if (colorMode.isDark()) it._200 else it._700 }
                        ?: switchPalette.backgroundOn else switchPalette.backgroundOff)
                .thenIf(!enabled) { DisabledStyle.toModifier() }
                .then(modifier)
                .thenIf(enabled) {
                    Modifier
                        .onClick { evt -> onCheckedChange(!checked); evt.stopPropagation() }
                        .onKeyDown { evt -> if (evt.key == "Enter" || evt.key == " ") onCheckedChange(!checked); evt.stopPropagation() }
                }
        ) {
            Box(
                modifier = SwitchThumbStyle.toModifier()
                    .setVariable(
                        SwitchThumbOffsetVar,
                        if (checked) ((size.width - size.height) as CSSLengthOrPercentageValue) else 0.percent
                    )
            )
        }
    }
}
