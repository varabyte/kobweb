If you want to add a widget to Silk, you must ensure that it meets the following requirements:

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

#### Define a ComponentStyle

You MUST define a `ComponentStyle` for it, even if empty, and set its prefix to `"silk"`

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

*Don't*

```kotlin
LabeledTextArea(
    "User text",
    containerModifier = Modifier.fillMaxWidth(),
    textAreaModifier = Modifier.fillMaxWidth().height(100.px),
    onTextChanged = { /* ... */ }
)
```

*Exception*

It should be rare, but we have found a case where it is acceptable to take an additional `Modifier` parameter that is
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

#### Ordering the modifier chain

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

#### Handling the disabled state

If relevant to the target widget, you SHOULD handle the disabled state in a consistent manner, by:

* taking in an `enabled` parameter (defaulting to true)
* applying the disabled style to the modifier chain when `enabled` is false
* adding `+ not(ariaDisabled)` to the various styles defined for this widget.

*Do*

```kotlin
val ButtonStyle by ComponentStyle(prefix = "silk-") {

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
val ButtonStyle by ComponentStyle(prefix = "silk-") {

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

---

This is a living document. Note that we may add additional requirements throughout the course of development as we
discover them. Thanks for your patience if you end up working on a widget that requires rethinking this document.