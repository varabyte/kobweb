package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.rowClasses
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.isBright
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.animation.toAnimation
import com.varabyte.kobweb.silk.components.icons.CheckIcon
import com.varabyte.kobweb.silk.components.icons.IndeterminateIcon
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.vars.animation.TransitionDurationVars
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.components.style.vars.color.FocusOutlineColorVar
import com.varabyte.kobweb.silk.components.style.vars.size.FontSizeVars
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.ColorScheme
import com.varabyte.kobweb.silk.theme.colors.palette.Palette
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Label
import org.w3c.dom.HTMLElement

/**
 * A convenient icon builder where you only care about the non-indeterminate case.
 *
 * This can be useful for cases where you want to create a custom non-check icon for your checkbox but are happy with
 * default indeterminate icon.
 *
 * @param provideIcon Provide an icon that should appear inside a checked checkbox. You should ignore the
 *  [indeterminate][CheckboxIconScope.indeterminate] property as it will always be false in this context.
 */
@Composable
fun CheckboxIconScope.CheckedIcon(provideIcon: @Composable CheckboxIconScope.() -> Unit) {
    if (indeterminate) {
        IndeterminateIcon()
    } else {
        provideIcon()
    }
}

object CheckboxDefaults {
    const val Enabled = true
    val Size = CheckboxSize.MD
    val IconProvider: @Composable CheckboxIconScope.() -> Unit = { CheckedIcon { CheckIcon() } }
}

object CheckboxVars {
    val BorderColor by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
    val BorderRadius by StyleVariable<CSSLengthNumericValue>(prefix = "silk", defaultFallback = 0.125.cssRem)
    val BorderWidth by StyleVariable<CSSLengthNumericValue>(prefix = "silk", defaultFallback = 0.125.cssRem)
    val Size by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val Spacing by StyleVariable<CSSLengthNumericValue>(prefix = "silk", defaultFallback = 0.5.cssRem)
    val FontSize by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val IconSize by StyleVariable<CSSLengthNumericValue>(prefix = "silk")
    val FocusOutlineColor by StyleVariable(prefix = "silk", defaultFallback = FocusOutlineColorVar.value())
    val FocusOutlineSpread by StyleVariable(prefix = "silk", defaultFallback = 0.1875.cssRem)
    val UncheckedBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val IconColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val IconBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val IconBackgroundHoverColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val TransitionDuration by StyleVariable(prefix = "silk", defaultFallback = TransitionDurationVars.VeryFast.value())
}

val CheckboxStyle by ComponentStyle(
    prefix = "silk",
    extraModifiers = Modifier.rowClasses(verticalAlignment = Alignment.CenterVertically)
) {
    base {
        Modifier
            .gap(CheckboxVars.Spacing.value())
            .userSelect(UserSelect.None)
            .fontSize(CheckboxVars.FontSize.value())
            .cursor(Cursor.Pointer)
    }
}

val CheckboxEnabledAnim by Keyframes(prefix = "silk") {
    from { Modifier.opacity(0) }
    to { Modifier.opacity(1) }
}

val CheckboxIconContainerStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier
            .fontSize(CheckboxVars.IconSize.value())
            .size(CheckboxVars.Size.value())
            .border(
                width = CheckboxVars.BorderWidth.value(),
                style = LineStyle.Solid,
                color = CheckboxVars.BorderColor.value()
            )
            .borderRadius(CheckboxVars.BorderRadius.value())
            .transition(
                CSSTransition.group(listOf("background-color", "border-color"), CheckboxVars.TransitionDuration.value())
            )
    }
}

val UncheckedCheckboxIconContainerVariant by CheckboxIconContainerStyle.addVariantBase {
    Modifier.backgroundColor(CheckboxVars.UncheckedBackgroundColor.value())
}

val CheckedCheckboxIconContainerVariant by CheckboxIconContainerStyle.addVariant {
    base {
        Modifier
            .backgroundColor(CheckboxVars.IconBackgroundColor.value())
            .border { color(CheckboxVars.IconBackgroundColor.value()) }
    }
}

val CheckboxIconStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .size(CheckboxVars.Size.value())
        .color(CheckboxVars.IconColor.value())
}

val CheckboxInputVariant by InputStyle.addVariant {
    base { HiddenInputModifier }

    // Since the checkbox is hidden, we highlight its sibling (a div which renders a checkbox icon) when the checkbox is
    // focused(-visible).
    cssRule(":focus-visible + *") {
        Modifier.boxShadow(
            spreadRadius = CheckboxVars.FocusOutlineSpread.value(),
            color = CheckboxVars.FocusOutlineColor.value()
        )
    }
    // If an input's label gets hovered, the input does too.
    cssRule(":not([aria-disabled]):hover + *") {
        Modifier
            .setVariable(CheckboxVars.IconBackgroundColor, CheckboxVars.IconBackgroundHoverColor.value())
    }
}

interface CheckboxSize {
    val boxSize: CSSLengthNumericValue
    val iconSize: CSSLengthNumericValue
    val fontSize: CSSLengthNumericValue

    object SM : CheckboxSize {
        override val boxSize = 0.875.cssRem
        override val iconSize = 0.45.cssRem
        override val fontSize = FontSizeVars.SM.value()
    }

    object MD : CheckboxSize {
        override val boxSize = 1.cssRem
        override val iconSize = 0.625.cssRem
        override val fontSize = FontSizeVars.MD.value()
    }

    object LG : CheckboxSize {
        override val boxSize = 1.25.cssRem
        override val iconSize = 0.8.cssRem
        override val fontSize = FontSizeVars.LG.value()
    }
}

fun CheckboxSize.toModifier() = Modifier
    .setVariable(CheckboxVars.Size, boxSize)
    .setVariable(CheckboxVars.IconSize, iconSize)
    .setVariable(CheckboxVars.FontSize, fontSize)

class CheckboxIconScope internal constructor(val indeterminate: Boolean, val colorMode: ColorMode)

enum class CheckedState {
    Checked,
    Unchecked,
    Indeterminate;

    companion object {
        fun from(value: Boolean) = if (value) Checked else Unchecked
        fun from(vararg values: Boolean) = when (values.count { it }) {
            0 -> Unchecked
            values.size -> Checked
            else -> Indeterminate
        }
    }

    fun toBoolean() = this != Unchecked
}

/**
 * Creates a checkbox that supports three states: on, off, and indeterminate.
 *
 * @param checked The current [CheckedState] of this checkbox.
 * @param onCheckedChange A callback which is invoked when the checkbox state changes.
 * @param enabled Whether the checkbox is enabled or not. If not, the checkbox will be rendered in a disabled state and will
 *   not be interactable.
 * @param icon The composable that renders the icon inside the checkbox. This will be passed a [CheckboxIconScope] which
 *   you can use to customize the icon based on potentially relevant context.
 * @param size The size of the checkbox. Defaults to [CheckboxSize.MD]. You can implement your own [CheckboxSize] if you
 *   want custom sizing.
 * @param spacing An optional spacing parameter to use between the checkbox and any content drawn to the right of it.
 * @param colorScheme An optional color scheme to use for the checkbox. If not provided, the checkbox will use the
 *   appropriate colors from the [Palette].
 * @param borderColor An optional override for the border color of the checkbox when unchecked.
 * @param uncheckedColor An optional override for the background color of the checkbox when unchecked.
 * @param iconColor An optional override for the color of the icon drawn in the checkbox.
 * @param focusOutlineColor An optional override for the border color when the input is focused.
 * @param content Inline content attached to the checkbox. This will be treated as an [HTML label](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/label),
 *   which means when clicked it will toggle the checkbox, and screen readers will read out any text in it when the
 *   checkbox is focused.
 *
 * @see Checkbox
 * @see CheckedState
 */
@Composable
fun TriCheckbox(
    checked: CheckedState,
    onCheckedChange: (CheckedState) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    enabled: Boolean = CheckboxDefaults.Enabled,
    icon: @Composable CheckboxIconScope.() -> Unit = CheckboxDefaults.IconProvider,
    size: CheckboxSize = CheckboxDefaults.Size,
    spacing: CSSLengthNumericValue? = null,
    colorScheme: ColorScheme? = null,
    borderColor: CSSColorValue? = null,
    uncheckedColor: CSSColorValue? = null,
    iconColor: CSSColorValue? = null,
    focusOutlineColor: CSSColorValue? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: (@Composable () -> Unit)? = null,
) {
    // Don't animate if a checkbox is being added to the DOM while already checked
    var shouldAnimate by remember { mutableStateOf(!checked.toBoolean()) }

    val colorMode = ColorMode.current

    // Use a label so it intercepts clicks and passes them to the inner Input
    Label(
        attrs = CheckboxStyle
            .toModifier(variant).thenIf(!enabled, DisabledStyle.toModifier()).then(size.toModifier())
            .setVariable(CheckboxVars.Spacing, spacing)
            .thenIf(colorScheme != null) {
                @Suppress("NAME_SHADOWING") val colorScheme = colorScheme!!
                val isDark = colorMode.isDark
                val isBrightColor = (if (isDark) colorScheme._200 else colorScheme._500).isBright
                Modifier
                    .setVariable(CheckboxVars.IconBackgroundColor, if (isDark) colorScheme._200 else colorScheme._500)
                    .setVariable(
                        CheckboxVars.IconBackgroundHoverColor, if (isDark) colorScheme._300 else colorScheme._600
                    ).setVariable(
                        CheckboxVars.IconColor,
                        (if (isBrightColor) ColorMode.LIGHT else ColorMode.DARK).toPalette().color
                    )
            }
            .setVariable(CheckboxVars.BorderColor, borderColor)
            .setVariable(CheckboxVars.UncheckedBackgroundColor, uncheckedColor)
            .setVariable(CheckboxVars.IconColor, iconColor)
            .setVariable(CheckboxVars.FocusOutlineColor, focusOutlineColor).then(modifier).toAttrs()
    ) {
        registerRefScope(ref)
        // We base Checkbox on a checkbox input for a11y + built-in input/keyboard support, but hide the checkbox itself
        // and render the box + icon separately. We do however allow it to be focused, which combined with the outer
        // label means that both clicks and keyboard events will toggle the checkbox.
        Input(
            type = InputType.Checkbox,
            value = checked.toBoolean(),
            onValueChanged = {
                onCheckedChange(
                    when (checked) {
                        CheckedState.Checked -> CheckedState.Unchecked
                        CheckedState.Unchecked -> CheckedState.Checked
                        CheckedState.Indeterminate -> CheckedState.Checked
                    }
                )
                shouldAnimate = true
            },
            variant = CheckboxInputVariant,
            enabled = enabled,
        )

        Box(
            CheckboxIconContainerStyle.toModifier(
                if (checked.toBoolean()) CheckedCheckboxIconContainerVariant else UncheckedCheckboxIconContainerVariant
            ),
            contentAlignment = Alignment.Center
        ) {
            if (checked.toBoolean()) {
                Box(
                    CheckboxIconStyle.toModifier().thenIf(shouldAnimate) {
                        Modifier.animation(
                            CheckboxEnabledAnim.toAnimation(colorMode, CheckboxVars.TransitionDuration.value())
                        )
                    }, contentAlignment = Alignment.Center
                ) {
                    CheckboxIconScope(
                        indeterminate = checked == CheckedState.Indeterminate, colorMode
                    ).apply { icon() }
                }
            }
        }

        if (content != null) content()
    }
}

/**
 * Creates a checkbox.
 *
 * @param checked Whether the checkbox is currently checked or not.
 * @param onCheckedChange A callback which is invoked when the checkbox is toggled.
 * @param enabled Whether the checkbox is enabled or not. If not, the checkbox will be rendered in a disabled state and will
 *   not be interactable.
 * @param icon The composable that renders the icon inside the checkbox. This will be passed a [CheckboxIconScope] which
 *   you can use to customize the icon based on potentially relevant context.
 * @param size The size of the checkbox. Defaults to [CheckboxSize.MD]. You can implement your own [CheckboxSize] if you
 *   want custom sizing.
 * @param spacing An optional spacing parameter to use between the checkbox and any content drawn to the right of it.
 * @param colorScheme An optional color scheme to use for the checkbox. If not provided, the checkbox will use the
 *   appropriate colors from the [Palette].
 * @param borderColor An optional override for the border color of the checkbox when unchecked.
 * @param uncheckedColor An optional override for the background color of the checkbox when unchecked.
 * @param iconColor An optional override for the color of the icon drawn in the checkbox.
 * @param focusOutlineColor An optional override for the border color when the input is focused.
 * @param content Inline content attached to the checkbox. This will be treated as an [HTML label](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/label),
 *   which means when clicked it will toggle the checkbox, and screen readers will read out any text in it when the
 *   checkbox is focused.
 *
 * @see TriCheckbox
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    enabled: Boolean = CheckboxDefaults.Enabled,
    icon: @Composable CheckboxIconScope.() -> Unit = CheckboxDefaults.IconProvider,
    size: CheckboxSize = CheckboxDefaults.Size,
    spacing: CSSLengthNumericValue? = null,
    colorScheme: ColorScheme? = null,
    borderColor: CSSColorValue? = null,
    uncheckedColor: CSSColorValue? = null,
    iconColor: CSSColorValue? = null,
    focusOutlineColor: CSSColorValue? = null,
    ref: ElementRefScope<HTMLElement>? = null,
    content: (@Composable () -> Unit)? = null,
) {
    TriCheckbox(
        CheckedState.from(checked),
        { onCheckedChange(it.toBoolean()) },
        modifier,
        variant,
        enabled,
        icon,
        size,
        spacing,
        colorScheme,
        borderColor,
        uncheckedColor,
        iconColor,
        focusOutlineColor,
        ref,
        content
    )
}
