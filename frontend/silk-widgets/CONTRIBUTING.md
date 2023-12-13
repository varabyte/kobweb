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

#### Define a ComponentStyle

You MUST define a `ComponentStyle` for your widget, even if empty, and set its prefix to `"silk"`

```kotlin
val MyWidgetStyle by ComponentStyle(prefix = "silk") { /* ... */ }
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
val tm = Modifier.flexGrow(1)
val pm = Modifier.fillMaxSize().padding(20.px)

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

#### The `ComponentVariant` parameter

You MUST declare a parameter `variant: ComponentVariant?` right after the `modifier` parameter, which SHOULD default to
`null`.

Variants encourage users to tweak a widget's appearance in standard ways that build on top of its initial style. As a
mental model, it's useful to think of them as modifier tweaks. As such, it helps to keep variants close to the initial
`modifier` parameter so users can understand that they are related.

As a widget designer, you are of course encouraged to provide variants if appropriate, but even if you don't, you should
still provide the parameter, since a Kobweb user may always create and share their own variant.

*Do*

```kotlin
val MyWidgetStyle by ComponentStyle(prefix = "silk") { /* ... */ }
val MyWidgetItalicizedVariant by MyWidgetStyle.addVariant { /* ... */ }

@Composable
fun MyWidget(modifier: Modifier = Modifier, variant: ComponentVariant? = null, enabled: Boolean = true, ...) {
    /* ... */
}
```

*Don't*

```kotlin
@Composable
fun MyWidget(modifier: Modifier = Modifier, enabled: Boolean = true, ..., variant: ComponentVariant? = null) {
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
    variant: ComponentVariant? = null,
)
```

#### State and style parameters

You SHOULD declare state parameters right after the `variant` parameter, followed by style parameters.

This order is chosen to mimic the order used by Jetpack Compose widgets, which appear to do the same thing. It also
makes some sense as an element's state often effects is visual appearance, and should therefore be prioritized earlier.

```kotlin
@Composable
fun MyWidget(
  text: String,
  modifier: Modifier = Modifier,
  variant: ComponentVariant? = null,
  enabled: Boolean,
  invalid: Boolean,
  size: MyWidgetSize = MyWidgetSize.MD,
  colorScheme: ColorScheme? = null,
  focusOutlineColor: Color? = null,
  ref: ...) {
```

*Don't*

```kotlin
@Composable
fun MyWidget(
  ...,
  variant: ComponentVariant? = null,
  enabled: Boolean,
  colorScheme: ColorScheme? = null,
  size: MyWidgetSize,
  enabled: Boolean,
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

Sizes SHOULD be `cssRem` values, so a site will dynamically resize around a larger font if designed with one.

Sizes MUST be an interface so users can implement their own custom sizes if they prefer.

The default size SHOULD be set to MD.

*Do*

```kotlin
interface MyWidgetSize {
  val fontSize: CSSLengthNumericValue
  object SM : MyWidgetSize { /* ... */ }
  object MD : MyWidgetSize {
      override val fontSize = 1.cssRem
  }
  object LG : MyWidgetSize { /* ... */ }
}

private fun MyWidgetSize.toModifier(): Modifier {
    return Modifier
      .setVariable(MyWidgetFontSizeVar, fontSize)
}

@Composable
fun MyWidget(
  modifier: Modifier = Modifier,
  variant: ComponentVariant? = null,
  size: MyWidgetSize = MyWidgetSize.MD,
  colorScheme: ColorScheme? = null,
  ...) {
    Box(
      MyWidgetStyle.toModifier(variant)
        .then(size.toModifier())
        .thenIf(colorScheme != null) {
            Modifier.setVariable(MyWidgetColorVar, if (ColorMode.current.isDark) colorScheme._200 else colorScheme._700)
        }
    )
}
```

#### Proper modifier chain order

You MUST build your modifier chain in a way that the user's passed-in modifier will overwrite anything from the base
style.

*Do*

```kotlin
val MyWidgetStyle by ComponentStyle(prefix = "silk") { /* ... */ }

@Composable
fun MyWidget(modifier: Modifier = Modifier, variant: ComponentVariant? = null) {
    Box(
        modifier = MyWidgetStyle.toModifier(variant).then(modifier)
    )
}
```

*Do*

It's also OK to have additional explicit modifiers inserted into the chain, as long as the user modifier is applied
after the base style:

```kotlin
val MyWidgetStyle by ComponentStyle(prefix = "silk") { /* ... */ }

@Composable
fun MyWidget(modifier: Modifier = Modifier, variant: ComponentVariant? = null) {
    Box(
        modifier = MyWidgetStyle
            .toModifier(variant)
            .position(Position.Relative)
            .then(modifier)
            .onClick { /* ... */ }
    )
}
```

*Don't*

```kotlin
val MyWidgetStyle by ComponentStyle(prefix = "silk") { /* ... */ }

@Composable
fun MyWidget(modifier: Modifier = Modifier, variant: ComponentVariant? = null) {
    Box(
        modifier = modifier.then(MyWidgetStyle.toModifier(variant))
    )
}
```

#### The `ref` parameter

You MUST declare a parameter `ref: ElementRefScope<HTMLElement>?` as either the last parameter or the second-to-last
parameter, which SHOULD default to `null`.

The parameter SHOULD be last *unless* the last parameter is reserved for a lambda, especially the `content` lambda.

The `ref` parameter allows users to access the underlying DOM element of a widget.

It is usually fine to can use `HTMLElement` as the generic type, but in some cases a more specific type can be
appropriate, such as `HTMLTextAreaElement` for a styled `TextArea` widget.

However, you shouldn't necessarily use a more specific type just because you technically can, as the backing element can
sometimes be an implementation detail. For example, `Box` uses a `Div` under the hood, but that fact is abstracted away
from the user because it shouldn't really matter.

*Do*

```kotlin
@Composable
fun MyWidget(..., ref: ElementRefScope<HTMLElement>? = null, content: @Composable () -> Unit) {
    Box(
        modifier = MyWidgetStyle.toModifier(variant).then(modifier),
        ref = ref
    ) {
        content
    }
}
```

*Do*

```kotlin
@Composable
fun MyWidget(..., ref: ElementRefScope<HTMLElement>? = null) {
    Box(
        modifier = MyWidgetStyle.toModifier(variant).then(modifier),
        ref = ref
    )
}
```

*Do*

Use `registerRefScope` when working with Compose HTML widgets.

```kotlin
@Composable
fun MyWidget(..., ref: ElementRefScope<HTMLElement>? = null, content: @Composable () -> Unit) {
    Div(
        modifier = MyWidgetStyle.toModifier(variant).then(modifier).toAttrs,
    ) {
        registerRefScope(ref)
    }
}
```

*Don't*

```kotlin
@Composable
fun MyWidget(modifier: Modifier = Modifier, ref: ElementRefScope<HTMLElement>? = null, enabled: Boolean = true, ...) {
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
val ButtonStyle by ComponentStyle(prefix = "silk") {

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
val ButtonStyle by ComponentStyle(prefix = "silk") {

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

Using variables allows users to override color values for a targeted subset of buttons, if necessary, by using
`Modifier.setVariable(...)` on either a specific button or a parent container that the button is a child of.

*Do*

```kotlin
// Button.kt -------------------------------------------------
val ButtonBackgroundDefaultColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundFocusColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundHoverColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val ButtonBackgroundPressedColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val ButtonStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier
            .color(ButtonBackgroundDefaultColorVar)
            .backgroundColor(buttonColors.default)
    }

    (hover + not(ariaDisabled)) {
        Modifier.backgroundColor(buttonColors.hover)
    }

    (focusVisible + not(ariaDisabled)) {
        Modifier.boxShadow(spreadRadius = 3.px, color = buttonColors.focus)
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(buttonColors.pressed)
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
setVariable(ButtonBackgroundDefaultColorVar, palette.button.default)
setVariable(ButtonBackgroundFocusColorVar, palette.button.focus)
setVariable(ButtonBackgroundHoverColorVar, palette.button.hover)
setVariable(ButtonBackgroundPressedColorVar, palette.button.pressed)
```

*Don't*

Short version: If you're hardcoding any colors in your Silk widget styles, that will need to be fixed.

```kotlin
val ButtonStyle by ComponentStyle(prefix = "silk") {
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
val ButtonColorVar by StyleVariable(prefix = "silk", defaultFallback = ColorVar.value())
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
