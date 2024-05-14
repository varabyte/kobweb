# Kobweb CSS Style

*An overview and migration guide*

**Friendship ended with *ComponentStyle***<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Now**<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;***CssStyle***<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**is my**<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**best friend[.](https://knowyourmeme.com/memes/friendship-ended-with-mudasir)**<br>

## Objective

This document explains why we are introducing the `CssStyle` concept and how it should be used to replace
`ComponentStyle`.

## Background

> [!IMPORTANT]
> For readers who just want to migrate their project, you can jump straight to the [code migration](#migration) section.

> [!NOTE]
> This document assumes some minimal familiarity
> with [CSS styles and stylesheets](https://www.w3schools.com/html/html_css.asp).

### Component styles

One of the earliest features touted in Kobweb was support of something called *component styles*. A component style
represents a collection of style properties, which are a group of styles meant to describe the look and feel of some
HTML component.

```kotlin
val MyWidgetStyle by ComponentStyle {
    base { Modifier.fontSize(32.px).padding(10.px) }
    hover {
        val highlightColor =
            if (colorMode.isDark) Colors.Pink else Colors.Red
        Modifier.backgroundColor(highlightColor)
    }
}

@Composable
fun MyWidget() {
    Button(onClick = { /*...*/ }, MyWidgetStyle.toModifier()) { /*...*/ }
    //                            ^^^^^^^^^^^^^^
}
```

Component styles are declared in code but ultimately get converted into CSS rules inside a CSS stylesheet.

Without this feature, projects would likely find themselves defining a monolithic stylesheet somewhere,
[as demonstrated in the official Compose HTML tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Style_Dsl#stylesheet).
This pattern is actually fairly common in web development in general, and as web projects grow over time, it becomes
easy for their stylesheet to grow as well, making it more and more difficult for developers to understand and maintain
it.

In contrast, breaking up styles makes them much easier to read, and if you ever remove a code file later, the included
styles will automatically be cleaned up.

When you declare a `ComponentStyle`, an associated HTML element will get created for it given a class name auto
generated from the variable name. If you inspect your page using your browser's dev tools, you'll be able to find it and
it should look something like this:

```html
<button class="my-widget-style my-widget-style_dark">
```

This is much easier to read / debug than if all those styles were splatted inline inside the element:

```html
<button style="font-size:32px; padding:10px; background-color: red">
```

The above example is *already* a bit long and would only get more and more unwieldy as you added more and more styles to
your element.

#### How Component Styles work

To understand what's happening behind the scenes, first know that Kobweb does generates a global in-memory stylesheet.

> [!NOTE]
> Even though I just made fun of monolithic stylesheets the previous section, it's not a problem in Kobweb because here
> it is a hidden implementation detail that you never look at directly. In other words, it's generated and not
> maintained by hand, which is a significant difference.

When you compile your project, the Kobweb Gradle Plugin will search your codebase, find all top-level `ComponentStyle`
declarations, and register them into the global stylesheet.

You can see this code for yourself if you open up the `main.kt` file that gets created under your project's
`build/generated` folder as part of the build process (e.g. after you run `kobweb run`).

```kotlin
// build/generated/kobweb/app/src/jsMain/kotlin/main.kt
public fun main() {
    ...
    additionalSilkInitialization = { ctx ->
        ...
        registerComponentStyle(MyWidgetStyle)
        ...
    }
}
```

### Component styles start getting used everywhere

Although originally designed for components only, the `ComponentStyle` concept was so useful that it became the
recommended way to convert inline styles into stylesheet styles.

This is even official guidance in the README (at the time of writing this document):

> As a beginner, or even as an advanced user when prototyping, feel free to use inline modifiers as much as you can,
> pivoting to component styles if you find yourself needing to use pseudo-classes, pseudo-elements, or media queries. It
> is fairly easy to migrate inline styles over to stylesheets in Kobweb.
>
> In my own projects, I tend to use inline styles for really simple layout elements (e.g. Row(Modifier.fillMaxWidth()))
> and component styles for complex and/or re-usable widgets.

Although I didn't notice it at the time, this was probably a sign of trouble -- a feature that had grown beyond its
original design.

### Side note: Component variants

We will not spend too long talking about *component variants*, since they aren't critical to understanding the new
`CssStyle` concept. However, we will still mention them as they are an important part of understanding how to migrate
your code.

When you define a component style, you can additionally create a variant from it:

```kotlin
val LinkStyle by ComponentStyle {
    base { Modifier.textDecorationLine(TextDecorationLine.None) }
    link { Modifier.color(LinkVars.DefaultColor.value()) }
    visited { Modifier.color(LinkVars.VisitedColor.value()) }
    hover { Modifier.textDecorationLine(TextDecorationLine.Underline) }
}

val UndecoratedLinkVariant by LinkStyle.addVariant {
    hover { Modifier.textDecorationLine(TextDecorationLine.None) }
}
```

If both a base style *and* its variant are combined, the base styles will be applied first and the variant styles
layered on top of them.

Variants were designed to allow a library author to create widgets with a core style plus many alternate varieties of
that style. As a bonus, an end user to could add their own custom variants, perhaps something more relevant to their
site's brand.

All Silk widgets allow for a variant to be set as an optional parameter:

```kotlin
@Composable
fun Link(..., variant: ComponentVariant? = null, ...) {
    ...
    val modifier = LinkStyle.toModifier(variant)
    ...
}

// For example, `Link(variant = UndecoratedLinkVariant)`
```

Anyway, the main thing to take away from this section is that component variants were designed as an essential piece of
component styles. In other words, a component style is not just a collection of styles, but it is a *tweakable*
collection of styles.

### The problem

We noticed an issue when we wanted to add the ability to pass a size object into the `Button` widget.

The button size should affect the button's height, font size, and padding values. Users should also be able to define
custom sizes if they want.

Our naive first pass looked something like this:

```kotlin
class ButtonSize(
    val fontSize: CSSLengthNumericValue,
    val height: CSSLengthNumericValue,
    val horizontalPadding: CSSLengthNumericValue
) {
    companion object {
        val SM = ButtonSize(0.8.cssRem, 2.cssRem, 0.75.cssRem)
        val MD = ButtonSize(...)
        val LG = ButtonSize(...)
    }
}

@Composable
fun Button(..., size: ButtonSize = ButtonSize.MD, ...) {
    val sizeModifer = Modifier
        .fontSize(size.fontSize)
        .height(size.height)
        .padding(leftRight = size.horizontalPadding)

    ...
}
```

which was functional, but it resulted in a HTML output like this:

```html
<button class="my-widget-style" style="font-size:0.8rem; height:2rem; padding:0px 0.75rem">
```

Why are some of our properties embedded in a CSS class while the other properties have leaked out into inline styles? At
a glance, can you tell if this button is using small, medium, or large values?

This felt off, and while we weren't sure how, we knew we could do better.

### The wrong solution: ComponentStyle

Our first thought was we could just use component styles for button sizes, since they are what Kobweb uses everywhere
else.

But this would result in code like the following:

```kotlin
object ButtonSizes {
    val SM by ComponentStyle.base {
        Modifier.fontSize(0.8.cssRem)
            .height(2.cssRem)
            .padding(leftRight = 0.75.cssRem)
    }
    val MD by ComponentStyle.base { ... }
    val LG by ComponentStyle.base { ... }
}

@Composable
fun Button(..., size: ComponentStyle = ButtonSizes.MD, ...) {
    val sizeModifer = size.toModifier()
    ...
}
```

While this gets us close, this approach unfortunately has a major problem: you lose type safety for the button size
parameter.

There's nothing that prevents you from passing any component style into that parameter. Pass in a size like you're
supported to? That's fine. But pass in a `LinkStyle`? Yikes, that shouldn't work.

It was quickly clear that this lack of type information was a deal-breaker.

## CssStyle

Kobweb needed a new class which:

1. lets you declare `Modifier`s that get converted into stylesheet entries (like `ComponentStyle` currently does)
2. allows subclassing, for type safety (so we can declare a base class called `ButtonSize` for example)

Since the concept of this new class would be larger than just components, we decided to name it `CssStyle`, to emphasize
that generality.

It turns out, while exploring this concept, we realized we could simplify the codebase and replace `ComponentStyle` with
it as well.

Let's review some examples in the next section.

### CssStyle cases

#### A general style declaration

> [!NOTE]
> This case is also referred to as *unspecified kind* styles.

This is probably the most common way users will use `CssStyle` – basically, almost all old code that used to use
`ComponentStyle` will simply use `CssStyle` instead:

```kotlin
val HeaderStyle = CssStyle {
    base { ... }
    hover { ... }
}

// Later
SpanText("Welcome", HeaderStyle.toModifier())
```

#### A replacement for `ComponentStyle`

> [!NOTE]
> This case is also referred to as *component kind* styles.

In legacy Kobweb, `ComponentStyle`s and `ComponentVariant`s were not typed, meaning you could technically pass the
variant for one widget to an unrelated one.

```kotlin
// ❌ Bad code, do not copy!
Link("https://bad.example.on.purpose.com", variant = SomeButtonVariant) 
//                                         ^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

After `CssStyle`, this is no longer possible, because moving forward, the widget style will be bound to a specific
component type (called a *component kind*).

To create such a style, you must implement the `ComponentKind` interface (the implementation is expected to be empty;
it's just a marker interface) and pass it as a generic type to the `CssStyle` builder.

Here is the new pattern in action:

```kotlin
sealed interface SpinnerKind : ComponentKind

val SpinnerStyle = CssStyle<SpinnerKind> { ... }

@Composable
fun Spinner(
    modifier: Modifier,
    variant: CssStyleVariant<SpinnerKind>? = null
) {
    ...
    val finalModifier = SpinnerStyle.toModifier(variant).then(modifier)
    Box(finalModifier)
}
```

> [!NOTE]
> In the code snippet above, we use a sealed class to express the intention that no one else should ever implement it.
> However, `interface MyWidgetKind : ComponentKind` would technically work as well.

This type of `CssStyle` supports the `addVariant` extension method, which behaves exactly as it did with
`ComponentStyle`.

> [!TIP]
> Any legacy `ComponentStyle` property that has calls to `addVariant` made on it somewhere is a likely candidate for
> this new style type.

#### A restricted style declaration

> [!NOTE]
> This case is also referred to as *restricted kind* styles.

Sometimes you want to create a class that provides a specific constructor with parameters which will get converted into
a style behind the scenes. In other words, you want to constrain the parameters instead of allowing for an open-ended
style.

Supporting this is important for the `ButtonSize` case we mentioned earlier.

To use this approach, you should implement `CssStyle.Restricted` (or `CssStyle.Restricted.Base` for the `base { ... }`
case).

Here is an example implementation for the ButtonSize class:

```kotlin
class ButtonSize(
    val fontSize: CSSLengthNumericValue,
    val height: CSSLengthNumericValue,
    val horizontalPadding: CSSLengthNumericValue,
) : CssStyle.Restricted.Base(
     Modifier
         .fontSize(fontSize)
         .height(height)
         .padding(leftRight = horizontalPadding)
) {
    companion object {
        val XS = ButtonSize(0.75.cssRem, 1.5.cssRem, 0.5.cssRem)
        val SM = ButtonSize(...)
        val MD = ButtonSize(...)
        val LG = ButtonSize(...)
    }
}
```

With that, you can now call `ButtonSize.toModifier()` to get an informative HTML class name:

```html
<button class="button button-size_lg">
```

You can tell from the name exactly what size I used, which is helpful when debugging.

If users want a custom size, they can declare it their own in their own project and use it:

```kotlin
val AppButtonSize = ButtonSize(...)

// Later
Button(..., size = AppButtonSize)
```

### Custom names and prefixes

Previously, the `ComponentStyle` method had options for taking optional `name` and `prefix` parameters. Although most
users probably never used them, it's worth noting that they are gone in `CssStyle`. That's because we moved the property
name generation logic from the code to the KSP processor, so it didn't make sense to manage name values in two places.

Moving forward, if you want to change the name and/or prefix of a `CssStyle`, you can use annotations to do so:

```kotlin
@CssName("info")
@CssPrefix("demo")
val InfoBubbleStyle = CssStyle { ... }

// Generates the class name "demo-info"
```

If you are a library author, you really should choose a prefix for it, as it will help prevent class name collisions
with other libraries as well as in the user's own code.

However, using `@CssPrefix` everywhere would be noisy and easy to miss a case or two. So instead, we recommend setting a
global prefix value for your library via your build script:

```kotlin
kobweb {
    library {
        cssPrefix.set("demo")
    }
}
```

At this point, you can think of the `@CssPrefix` annotation as a way to override that default on a case-by-case basis,
if necessary.

### Extending styles

You may find yourself occasionally wanting to define a partial style that should only be applied when another style is
also applied first.

You might think, "I can use component styles and variants for this!" And it's not exactly an unreasonable approach -- in
fact, during internal test runs, that's what we would recommend to our early adopters.

However, it wasn't a perfect fit because the requirement to create a `ComponentKind` interface when you didn't care
about it was annoying. If the extra type information was something the user didn't need, it could even get in the way.

Therefore, we soon introduced a new concept: extending a base style using the `extendedBy` method:

```kotlin
val GeneralTextStyle = CssStyle {
    base { Modifier.fontSize(16.px).fontFamily("...") }
}
val EmphasizedTextStyle = GenerateTextStyle.extendedBy {
    base { Modifier.fontWeight(FontWeight.Bold) }
}

// Or, using the `base` methods:
// val GeneralTextStyle = CssStyle.base {
//   Modifier.fontSize(16.px).fontFamily("...")
// }
// val EmphasizedTextStyle = GenerateTextStyle.extendedByBase {
//   Modifier.fontWeight(FontWeight.Bold)
// }
```

Once extended, you only need to call `toModifier` on the extended style to include both styles automatically:

```kotlin
SpanText("WARNING", EmphasizedTextStyle.toModifier())
```

At first, it can be confusing to know when to use extended styles and when to use variants. Just realize that their
purposes are different:

* variants are meant to be useless on their own; they are only valid within the context of a base style that is tied to
  exactly one widget. They are meant to be passed in as a parameter to a widget composable.
* extended styles are meant to feel like a stand-alone style, and the fact they extend a base style is an
  invisible implementation detail for the caller. Extended styles can and are expected to be used across many different
  widgets as they are considered generally applicable and not specific to a single element type only.

Extended styles can be very useful way to replace code where you would otherwise find yourself
writing `SomeStyle.toModifier().then(AnotherStyle.toModifier())`

## <span id="migration">Migrating code</span>

The good news is that the migration should be trivial in most cases. The bad news is `ComponentStyle` was a very early
Kobweb concept, so we expect that many users will get hit with a bunch of deprecation warnings when upgrading. Some
issues will require manual intervention.

This section should help you handle every warning case you might encounter.

### Automatic migration

The version of Kobweb that introduced this feature also includes a Gradle task `kobwebMigrateToCssStyle`. You should
absolutely run this first, although make sure you're in a safe environment where you can revert your changes if
something goes wrong.

```bash
$ cd site
$ ../gradlew kobwebMigrateToCssStyle

> Task :site:kobwebMigrateToCssStyle
Updated com/example/site/components/layouts/PageLayout.kt
Updated com/example/site/components/sections/Footer.kt
Updated com/example/site/App.kt

3 file(s) were updated.
```

> [!TIP]
> You may want to optimize imports on all files that were changed by the migration process, in case now some imports are
> unused and stale.

At this point, try to run `kobweb run` on your project. Hopefully, everything will work seamlessly. However, there may
be deprecation warnings or errors that show up after the migration, and you should pay attention to them. When resolving
these issues, please review the remaining sections to understand what you may need to do.

### Updating imports

A lot of classes moved from the package `com.varabyte.kobweb.silk.components.style` to
`com.varabyte.kobweb.silk.style` – in other words, we dropped the `components` package (since, after all, this whole
document describes how we're moving away from `ComponentStyle`s to `CssStyle`s).

Doing a find / replace of `import com.varabyte.kobweb.silk.components.style` to `import com.varabyte.kobweb.silk.style`
can catch most of these.

```kotlin
// Before

import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle

Box(SmoothColorStyle.toModifier().fillMaxSize())
```
```kotlin
// After

import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle

Box(SmoothColorStyle.toModifier().fillMaxSize())
```

One exception is all the provided psuedo-class and psueo-element selectors, like `hover` for example. Those have all
moved from `com.varabyte.kobweb.silk.components.style` into their own new package at
`com.varabyte.kobweb.silk.style.selector`.

```kotlin
// Before

import com.varabyte.kobweb.silk.components.style.hover

CssStyle {
    base { /* ... */ }
    hover { /* ... */ }
}
```
```kotlin
// Before

import com.varabyte.kobweb.silk.style.selector.hover

CssStyle {
  base { /* ... */ }
  hover { /* ... */ }
}
```

Finally, it might be worth knowing that animation code moved from `com.varabyte.kobweb.silk.components.style.animation`
to `com.varabyte.kobweb.silk.style.animation`, and design breakpoint code moved from
`com.varabyte.kobweb.silk.components.style.breakpoint` to `com.varabyte.kobweb.silk.style.breakpoint`. These should have
gotten handled automatically by the find / replace steps recommended earlier in this section, but we mention it here in
case there's a codebase where somehow such an import was missed.

### Converting a ComponentStyle to an unspecified CssStyle

This probably covers almost every, if not every, case in most codebases. You should always try it first:

```kotlin
// Before

val SomeStyle by ComponentStyle {
    ...
}
```
```kotlin
// After

val SomeStyle = CssStyle { // A, B
    ...
}
```

* A: `by` changes to `=`
* B: `ComponentStyle` changes to `CssStyle`

### Converting a legacy ComponentStyle into a CssStyle<ComponentKind>

If you're using `addVariant` anywhere in your code, this case may be for you.

```kotlin
// Before

val WidgetStyle by ComponentStyle {
    ...
}

val SomeWidgetVariant by WidgetStyle.addVariant {
    ...
}

@Composable
fun Widget(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null
) {
    val finalModifier = WidgetStyle.toModifier(variant).then(modifier)
    ...
}
```
```kotlin
// After

sealed interface WidgetKind : ComponentKind // A

val WidgetStyle = CssStyle<WidgetKind> { // B, C
    ...
}

val SomeWidgetVariant by WidgetStyle.addVariant { // D
    ...
}

@Composable
fun Widget(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<WidgetKind>? = null
) { // E
    val finalModifier = WidgetStyle.toModifier(variant).then(modifier)
    ...
}
```

* A: Introduction of a marker interface that inherits from `ComponentKind`. It does not have to be sealed, but it's a
  good practice to do in order to minimize the chance that anyone else ever implements it.
* B: `by` changes to `=`
* C: `ComponentStyle` changes to `CssStyle<ComponentKind>`. It's a good practice to ensure that the name of the style
  and the name of the kind match (above, "Widget").
* D: `by` changes to `=`
* E: `ComponentVariant` changes to `CssStyleVariant<ComponentKind>`. Hooray, we have compiler-enforced type safety now!

### Using CssStyle.extendedBy

There are cases we've seen where users used `addVariant` to essentially bind two styles together, even though
conceptually it was more that they were defining a new style that happened to build on top of another one, rather than
creating a type-safe base style / variant style relationship.

This may be your case if you are using `addVariant` but not using the `WidgetStyle.toModifier(variant)` pattern inside a
widget composable.

You also might use this approach if you are using the same style for multiple different, unrelated widgets.

```kotlin
// Before

val BaseStyle by ComponentStyle {
    ...
}

val ExtendingVariant by BaseStyle.addVariant {
    ...
}

Box(BaseStyle.toModifier(ExtendingVariant))
```
```kotlin
// After

val BaseStyle = CssStyle { // A, B
    ...
}

val ExtendedStyle = BaseStyle.extendedBy { // C, D, E
    ...
}

Box(ExtendedStyle.toModifier()) // F
```

* A: `by` changes to `=`
* B: `ComponentStyle` changes to `CssStyle`
* C: `-Variant` suffix changes to `-Style` suffix
* D: `by` changes to `=`
* E: `addVariant` changes to `extendedBy`
* F: `ComponentStyle.toModifier(ComponentVariant)` changes to `ExtendedStyle.toModifier()`.

### Bye-bye by

If you see any message about `getValue` being deprecated, that means you need to change a `by` to an `=`.

```kotlin
// Before

val MyButtonVariant by ButtonStyle.addVariant { ... }
val MyKeyframes by Keyframes { ... }
```
```kotlin
// After

val MyButtonVariant = ButtonStyle.addVariant { ... }
val MyKeyframes = Keyframes { ... }
```

For legacy support, we are temporarily supporting the `by` keyword for the new `CssStyle` type, so this can happen if
you find yourself migrating code by hand and miss a `by` case.
