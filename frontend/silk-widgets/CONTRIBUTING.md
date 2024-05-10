This document describes requirements to be aware of when creating new widgets for Silk.

Once set up correctly, Silk widgets provide unparalleled amounts of customization for users. Also, it is important to
aim for a feeling of consistency across widgets for the best user experience. However, there are a lot of little things
to get right, so we're documenting them here.

| NOTE: This is a living document. We may add additional requirements as we discover them. Thanks for your patience if you end up working on a widget that requires adjusting this document. |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

#### Base requirements

If not already, please familiarize yourself with
the [Compose API guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md)
before proceeding.

To extract a few key points:

* Widgets should be marked `@Composable` and return `Unit`
* Widgets should use `PascalCase`
* Widget names should be nouns
* The first optional parameter should be a `Modifier`
* If the widget is a container, the last parameter should be a callback called `content` of
  type `@Composable () -> Unit`
    * The callback can be scoped, e.g. `@Composable PopupScope.() -> Unit`

#### Boolean naming

When naming booleans, we try to stick with the convention used Compose API widgets, which is to avoid using the `is`
prefix for words that are already adjectives.

For example, `enabled` and `checked`, not `isEnabled` and `isChecked`.

However, sometimes a boolean parameter name is a noun, and in that case, we do use the `is` prefix. For example,
`isDefault` and not `default`.

#### Define a component style

You MUST define a `ComponentKind` sealed interface implementation and associated `CssStyle` for your widget, even if
empty.

You MUST match the name of the style with the name of the kind (minus the "Kind" and "Style" suffixes).

```kotlin
sealed interface WidgetKind : ComponentKind

val WidgetStyle = CssStyle<WidgetKind> { /* ... */ }
```

If a complex style contains multiple inner component styles, you SHOULD nest any additional component kinds inside the
main kind. The inner component kinds MUST NOT end with "Kind" while the main one MUST:

```kotlin
sealed interface TabsKind : ComponentKind {
  sealed interface TabRow : ComponentKind
  sealed interface Tab : ComponentKind
  sealed interface Panel : ComponentKind
}

val TabsStyle = CssStyle<TabsKind> { /* ... */ }
val TabsTabRowStyle = CssStyle<TabsKind.TabRow> { /* ... */ }
val TabsTabStyle = CssStyle<TabsKind.Tab> { /* ... */ }
val TabsPanelStyle = CssStyle<TabsKind.Panel> { /* ... */ }
```

#### Single `Modifier` parameter

You MUST take a single `Modifier` parameter as the first optional parameter, named `modifier` defaulting to `Modifer`.

In other words, you MUST avoid taking multiple `Modifier` parameters that target multiple children inside your widget.

It can be very tempting to create complex, nested widgets that take multiple `Modifier` parameters, but it is better to
find a way instead to nest composables, each with its own single `Modifier` parameter.

Not only will this be less surprising for users, but it will also force you to design your container widgets in ways
that may be more reusable in new contexts.

*Do*

```kotlin
LabeledBox("User text", Modifier.fillMaxWidth()) {
    TextArea(Modifier.fillMaxWidth().height(100.px), onTextChanged = { /* ... */ })
}
```

```kotlin
Tabs(tabsModifier) {
    TabPanel {
        Tab(tabModifier) { /* ... */ }
        Panel(panelModifier) { /* ... */ }
    }
}
```

*Don't*

```kotlin
LabeledTextArea(
    "User text",
    containerModifier = Modifier.fillMaxWidth(),
    textAreaModifier = Modifier.fillMaxWidth().height(100.px),
    onTextChanged = { /* ... */ }
)
```

```kotlin
Tabs {
    TabPanel(tabModifier, panelModifier) { /* ... */ }
}
```

*Exception*

Ultimately, there may be a handful of cases where additional modifiers are acceptable, but they should be rare, and
there should always be a single `modifier: Modifier` parameter where it's not confusing what it applies to.

##### Additional modification

One case where it is acceptable to take an additional `Modifier` parameter that is
used conditionally and applied on top of the base `Modifier` parameter:

```kotlin
Tooltip(
    text = "User text",
    modifier = Modifier.fillMaxWidth(),
    hiddenModifier = Modifier.scale(0.9)
)
```

In `Tooltip`, this extra modifier is applied on top of the base `modifier` parameter only when the tooltip is in an
initial hidden state, a state which is not easily exposed to the user. Most users won't even ever set this value, so its
addition shouldn't make the widget that much harder to understand or use.

##### Convenience methods

The `Tabs` widget is designed in a way that the outer `Tabs` method and inner `Tab` and `Panel` methods each take a
single modifier. However, there are some common cases (where a tab is just some text) where code can be compressed a
lot and, therefore, more readable. In these cases, it is acceptable to provide a convenience extension method that takes
multiple modifiers and delegates to the standard `Tab` and `Panel` methods:

```kotlin
private val tm = Modifier.flexGrow(1)
private val pm = Modifier.fillMaxSize().padding(20.px)

Tabs {
    TabPanel("Tab", tabModifier = tm, panelModifier = pm) {
        /* ... panel definition here ... */
    }
}

// The above is shorthand for:
// Tabs {
//     TabPanel {
//         Tab(modifier = tm) {
//             Text("Tab")
//         }
//         Panel(modifier = pm) {
//             /* ... panel definition here ... */
//         }
//     }
// }
```

#### The `CssStyleVariant` parameter

You MUST declare a parameter `variant: CssStyleVariant<K>?` parameter right after the `modifier` parameter, which SHOULD
default to `null`. The type of `K` here will be determined by the `CssStyle<K>` it is associated with.

Variants encourage users to tweak a widget's appearance in standard ways that build on top of its initial style. As a
mental model, it's useful to think of them as modifier tweaks. As such, it helps to keep variants close to the initial
`modifier` parameter so users can understand that they are related.

As a widget designer, you are of course encouraged to provide variants if appropriate, but even if you don't, you MUST
still provide the parameter, since a Kobweb user may always create and use their own variant.

*Do*

```kotlin
sealed interface WidgetKind : ComponentKind

val WidgetStyle = CssStyle<WidgetKind> { /* ... */ }
val ItalicizedWidgetVariant = WidgetStyle.addVariant { /* ... */ }

@Composable
fun Widget(modifier: Modifier = Modifier, variant: CssStyleVariant<WidgetKind>? = null, ...) {
    /* ... */
}
```

*Don't*

```kotlin
@Composable
fun Widget(modifier: Modifier = Modifier, ..., variant: CssStyleVariant<WidgetKind>? = null) {
    /* ... */
}
```

```kotlin
// Missing variant parameter
@Composable
fun Widget(modifier: Modifier = Modifier) {
  /* ... */
}
```

*Exception*

In the tooltip case, the extra modifier supplied is really an extension of the initial modifier, so it's ok for the
variant to come right after it:

```kotlin
@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    hiddenModifier: Modifier = Modifier,
    variant: CssStyleVariant<TooltipKind>? = null,
)
```

#### State and style parameters

You SHOULD declare optional state parameters right after the `variant` parameter, followed by style parameters.

This order is chosen to mimic the order used by Jetpack Compose widgets, which appear to do the same thing. It also
makes some sense as an element's state often affects is visual appearance, and should therefore be prioritized earlier.

```kotlin
@Composable
fun Widget(
  text: String,
  modifier: Modifier = Modifier,
  variant: CssStyleVariant<WidgetKind>? = null,
  enabled: Boolean = true,
  invalid: Boolean = false,
  size: WidgetSize = WidgetSize.MD,
  colorScheme: ColorScheme? = null,
  focusOutlineColor: CSSColorValue? = null,
  ref: ...) {
```

*Don't*

```kotlin
@Composable
fun Widget(
  ...,
  variant: CssStyleVariant<WidgetKind>? = null,
  enabled: Boolean,
  colorScheme: ColorScheme? = null,
  size: WidgetSize,
  invalid: Boolean,
  ...) {
    /* ... */
}
```

#### Widget sizes

Sizes are useful for widgets that have a visual appearance that often benefit from being scaled up or down, but in a
consistent way across the whole application.

Size names MUST be named after abbreviated T-shirt sizes, with at least SM, MD, and LG sizes defined.
You CAN additionally define XS, XL, and XXL sizes if they seem relevant, but they are not required.

Size properties MUST be declared in a companion object inside the size class.

Sizes SHOULD be `cssRem` values, so a site will dynamically resize around a larger font if designed with one.

Sizes MUST extend `CssStyle.Restricted` or `CssStyle.Restricted.Base`.

The default size SHOULD be set to MD.

*Do*

```kotlin
class WidgetSize(fontSize: CSSLengthNumericValue) :
  CssStyle.Restricted.Base(Modifier.setVariable(WdigetFontSizeVar, fontSize)) {
  companion object {
    val SM = WidgetSize(0.75.cssRem)
    val MD = WidgetSize(1.cssRem)
    val LG = WidgetSize(1.25.cssRem)
  }
}

@Composable
fun Widget(
  modifier: Modifier = Modifier,
  variant: CssStyleVariant<WidgetKind>? = null,
  size: WidgetSize = WidgetSize.MD,
  ...
) {
    Box(
      WidgetStyle.toModifier(variant)
        .then(size.toModifier())
        .then(...)
    )
}
```

#### Proper modifier chain order

You MUST build your modifier chain in a way that the user's passed-in modifier will overwrite anything from the base
style.

*Do*

```kotlin
val WidgetStyle = CssStyle<WidgetKind> { /* ... */ }

@Composable
fun Widget(modifier: Modifier = Modifier, variant: CssStyleVariant<WidgetKind>? = null) {
    Box(
      modifier = WidgetStyle.toModifier(variant).then(modifier)
    )
}
```

*Do*

It's also OK to have additional explicit modifiers inserted into the chain, as long as the user modifier is applied
after the base style:

```kotlin
val WidgetStyle = CssStyle<WidgetKind> { /* ... */ }

@Composable
fun Widget(modifier: Modifier = Modifier, variant: CssStyleVariant<WidgetKind>? = null) {
    Box(
      modifier = WidgetStyle
            .toModifier(variant)
            .position(Position.Relative)
            .then(modifier)
    )
}
```

*Don't*

```kotlin
val WidgetStyle = CssStyle<WidgetKind> { /* ... */ }

@Composable
fun Widget(modifier: Modifier = Modifier, variant: CssStyleVariant<WidgetKind>? = null) {
    Box(
      modifier = modifier.then(WidgetStyle.toModifier(variant))
    )
}
```

*Exception*

Here, we define an on click handler AFTER the user's modifier is applied. If this is done, presumably the click handler
is a core part of the widget's functionality, and we don't want the user overriding it.

```kotlin
val ButtonStyle = CssStyle<ButtonKind> { /* ... */ }

@Composable
fun Button(modifier: Modifier = Modifier, variant: CssStyleVariant<ButtonKind>? = null) {
  Box(
    modifier = WidgetStyle
      .toModifier(variant)
      .position(Position.Relative)
      .then(modifier)
      .onClick { /* ... */ }
  )
}
```

#### The `ref` parameter

You MUST declare a parameter `ref: ElementRefScope<HTMLElement>?` as either the last parameter or the second-to-last
parameter, which SHOULD default to `null`.

The parameter SHOULD be last *unless* the last parameter is reserved for a lambda, especially the `content` lambda.

The `ref` parameter allows users to access the underlying DOM element of a widget.

It is usually fine to use `HTMLElement` as the generic type, but in some cases a more specific type can be appropriate,
such as `HTMLTextAreaElement` for a styled `TextArea` widget.

However, you shouldn't necessarily use a more specific type just because you technically can, as the backing element can
sometimes be an implementation detail. For example, `Box` uses a `Div` under the hood, but that fact is abstracted away
from the user because it shouldn't really matter.

*Do*

```kotlin
@Composable
fun Widget(..., ref: ElementRefScope<HTMLElement>? = null, content: @Composable () -> Unit) {
    Box(
        modifier = WidgetStyle.toModifier(variant).then(modifier),
        ref = ref
    ) {
        content
    }
}
```

*Do*

```kotlin
@Composable
fun Widget(..., ref: ElementRefScope<HTMLElement>? = null) {
    Box(
        modifier = WidgetStyle.toModifier(variant).then(modifier),
        ref = ref
    )
}
```

*Do*

Use `registerRefScope` when working with Compose HTML widgets.

```kotlin
@Composable
fun Widget(..., ref: ElementRefScope<HTMLElement>? = null, content: @Composable () -> Unit) {
    Div(
      modifier = WidgetStyle.toModifier(variant).then(modifier).toAttrs,
    ) {
        registerRefScope(ref)
    }
}
```

*Don't*

```kotlin
@Composable
fun Widget(modifier: Modifier = Modifier, ref: ElementRefScope<HTMLElement>? = null, enabled: Boolean = true, ...) {
    /* ... */
}
```

#### Handle the disabled state

If relevant to the target widget, you SHOULD handle the disabled state in a consistent manner, by:

* taking in an `enabled` parameter (defaulting to true)
* applying the disabled style to the modifier chain when `enabled` is false
* adding `+ not(ariaDisabled)` to the various styles defined for this widget.

*Do*

```kotlin
val ButtonStyle = CssStyle<ButtonKind> {

    base { /* ... */ }

    (hover + not(ariaDisabled)) { /* ... */ }
    (focusVisible + not(ariaDisabled)) { /* ... */ }
    (active + not(ariaDisabled)) { /* ... */ }
}

@Composable
fun Button(..., enabled: Boolean = true, ...) {
    JbButton(
        attrs = ButtonStyle.toModifier(variant)
            .thenIf(!enabled, DisabledStyle.toModifier().tabIndex(-1))
            ...
    )
}
```

*Don't*

```kotlin
val ButtonStyle = CssStyle<ButtonKind> {

    base { /* ... */ }

    hover { /* ... */ }
    focusVisible { /* ... */ }
    active { /* ... */ }
}

@Composable
fun Button(..., disabled: Boolean = false, ...) {
    JbButton(
        attrs = ButtonStyle.toModifier(variant)
            .thenIf(disabled, Modifier.opacity(0.5f))
            ...
    )
}
```

#### Add palette entries and style variables

If you are defining a target widget that requires new colors, you MUST add all them to the `SilkPalette` interface. If
done, you MUST set values in the dark and light `MutableSilkPalette` implementation. Finally, you MUST create
StyleVariable entries for each color and hook those variables up to the right palette colors in `setSilkVariables` in
`InitSilk.kt`.

Using palettes makes it easier for Kobweb users to globally change the colors of their whole app without needing to
override any component styles.

Using variables allows users to override color values for a targeted subset of widgets, if necessary, by using
`Modifier.setVariable(...)` on either a specific widget or a parent container that the widget is a child of.

*Do*

```kotlin
// Button.kt -------------------------------------------------
object ButtonVars {
    val Color by StyleVariable<CSSColorValue>(prefix = "silk")
    val DefaultColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val FocusColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val HoverColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val PressedColor by StyleVariable<CSSColorValue>(prefix = "silk")
}

sealed interface ButtonKind : ComponentKind

val ButtonStyle = CssStyle<ButtonKind> {
    base {
        Modifier
            .color(ButtonVars.Color.value())
            .backgroundColor(ButtonVars.DefaultColor.value())
    }

    (hover + not(ariaDisabled)) {
        Modifier.backgroundColor(ButtonVars.HoverColor.value())
    }

    (focusVisible + not(ariaDisabled)) {
        Modifier.boxShadow(spreadRadius = 3.px, color = ButtonVars.FocusColor.value())
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(ButtonVars.PressedColor.value())
    }
}

// SilkPalette.kt -------------------------------------------------
interface SilkPalette {
    val button: Button

    interface Button {
        val default: Color
        val hover: Color
        val focus: Color
        val pressed: Color
    }
}

class MutableSilkPalettes(
    override val light: MutableSilkPalette = run {
        MutableSilkPalette(
            button = MutableSilkPalette.Button(
                default = ...,
                hover = ...,
                focus = ...,
                pressed = ...
            )
        )
    },
    override val dark: MutableSilkPalette = run {
        MutableSilkPalette(
            button = MutableSilkPalette.Button(
                default = ...,
                hover = ...,
                focus = ...,
                pressed = ...
            )
        )
    }
) : SilkPalettes

// InitSilk.kt -------------------------------------------------
setVariable(ButtonVars.DefaultColor, palette.button.default)
setVariable(ButtonVars.FocusColor, palette.button.focus)
setVariable(ButtonVars.HoverColor, palette.button.hover)
setVariable(ButtonVars.PressedColor, palette.button.pressed)
```

*Don't*

Short version: If you're hardcoding any colors in your Silk widget styles, that will need to be fixed.

```kotlin
val ButtonStyle = CssStyle<ButtonKind> {
    base {
        Modifier
            .color(Colors.Red)
            .backgroundColor(Colors.Green)
    }

    ...
}
```

*Exception*

Variables which are based on global Silk values (like border color) should be connected directly at the variable
declaration point, and not in `InitSilk.kt`:

```kotlin
// in object ButtonVars
val Color by StyleVariable(prefix = "silk", defaultFallback = ColorVar.value())
```

#### evt.stopPropagation

If you handle events, you SHOULD call `evt.stopPropagation()` to prevent the event from bubbling up to parent elements
(unless there's an intentional reason not to).

Otherwise, users may find that their interactions with the widget may unexpectedly interact with one of its containers
as well.

*Do*

```kotlin
Modifier
    .onClick { evt ->
        evt.stopPropagation()
        handleAction()
    }
    .onKeyDown { evt ->
        if (evt.key == "Enter") {
            evt.stopPropagation()
            handleAction()
        }
    }
```

*Don't*

```kotlin
Modifier
    .onClick { handleClick() }
    .onKeyDown { evt -> if (evt.key == "Enter") { handleAction() } }
```
