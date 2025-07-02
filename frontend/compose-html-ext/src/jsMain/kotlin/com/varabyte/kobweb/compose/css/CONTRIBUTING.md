# Kotlin bindings for CSS style properties

When we first began work on Kobweb, we did not realize that bindings for so many CSS properties were still missing (and,
worse, that some of the ones provided are incomplete).

The goal of this package (at the moment) is to do the minimal amount of work filling in the gaps left behind by the
Compose HTML team. We have also decided on some standard patterns that have proven resilient to even the most complex
CSS style properties we've faced.

Consider also
reviewing [the CSS Kobweb Checklist](<https://docs.google.com/spreadsheets/d/1Uu2diibyOzDFPgzzM8BWlw4B9_CTDBACv_1E_tsXbp8>)
to see the list of CSS style properties that are still unimplemented.

## A note about `unsafeCast`

If you are unfamiliar with `unsafeCast` [(link to docs](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.js/unsafe-cast.html)
and **provided in Kotlin/JS only**), it is a way to force the Kotlin compiler to just "trust me" that one value type
should be passed around as another.

This can occasionally be useful in the dynamic world of JavaScript, where sometimes the heavy-handed type checking
aspect of the Kotlin compiler actively gets in the way.

If you unsafe-cast one type to another, you'll be fine as long as...

* You don't call `is` or `as` on it later
* You only call methods that exist on both types

Otherwise, a cast class exception will be thrown.

We use this feature heavily in this package to pass raw strings around as if they were fully-typed values. This works
because Compose HTML only ever calls `toString` on these values internally, and that method is guaranteed to be present.
For users, they should not be doing anything complicated with these string-as-type CSS property values, besides passing
them around to our own code.

## Rules for contributing new CSS style property classes

### Only support properties and features that are baseline widely available

In the [MDN docs](https://developer.mozilla.org/en-US/), confirm in the top right corner of the property's page that
it is labeled as widely available across all major browsers.

Sometimes, this label will be tagged with an asterix (i.e. "Baseline Widely Available *"), which means that while the
property is overall safe to use, some parts of the feature are not. In that case, you should check out its
*Browser Compatibility* section for more information.

As a concrete example, as of 2025, [`touch-action`](https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action) is
baseline widely available but several of its values (the `pan-` cardinal directions) are not, as listed in its
[browser compatibility section](https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action#browser_compatibility).

You may also wish to refer to https://caniuse.com/ for even more detailed reports about what properties and features are
actually supported.

#### Exception

If a feature is not available on one of the less popular browsers (Firefox) AND it otherwise has extremely high coverage
according to `caniuse.com` (95%+), and *especially* if the feature doesn't fundamentally break the page on that browser
(instead, experienced by users as an annoying cosmetic issue), we will allow it.

We did this with the [`widows` CSS property](https://developer.mozilla.org/en-US/docs/Web/CSS/widows) for a specific
example.

### Every CSS style property should link to its MDN docs page

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
```

We will eventually convert these into full-blown header comments, but for now, at least adding the link there is useful
for developers who need to quickly jump to the link to understand the property's behavior and syntax.

---
### Every **non-longhand** CSS style property should have a corresponding sealed interface

Some CSS properties are called *shorthand* and some are called *longhand*. A shorthand property is one that is a
grouped representation of several inner properties, themselves called longhand properties.

For example, `margin-inline` is a shorthand property for `margin-inline-start` and `margin-inline-end`.
`margin-inline-start` and `margin-inline-end` are longhand properties.

Many longhand properties are simple primitive values (such as `border-width`).

There are hundreds of these longhand properties (`border` alone has about fifty of them), so we've decided not to add
classes for these at this point. So far, users haven't told us they needed them.

But we require types for shorthand properties and normal properties as a pre-requisite to releasing 1.0.

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
sealed interface StyleExample /*...*/
```

```kotlin
❌
sealed interface MarginInlineEnd /* ... */
```

Even if the property is 99% of the time a simple integer value (e.g. `column-count`), we still need to support users
being able to pass in global values (e.g. `ColumnCount.Inherit`), so we will always need a container type for every
single property type.

We seal the interface to prevent users from inheriting it. Our intention is for this to be self-contained type that only
we control.

#### Exception

If a longhand property is notably complex, like several of the `animation` longhand properties are, then you can of
course introduce classes for those cases. But we do not plan to support most of them at this time.

---
### Every interface should implement `StylePropertyValue`

#### Example

```kotlin
sealed interface StyleExample : StylePropertyValue
```

This pattern lets us pass these CSS values into the Compose HTML `property` method, which we will introduce an example
of much later in this document. For now, just do it!

---
### Every class should have its companion object inherit from `CssGlobalValues<T>`

#### Example

```kotlin
sealed interface StyleExample : StylePropertyValue {
    companion object : CssGlobalValues<StyleExample>
}
```

This adds the `Inherit`, `Initial`, `Revert`, etc. global keywords to the class's API.

#### Warning

These CSS value classes are often copy-pasted around when starting on new ones. From experience, we have seen it can
be easy to make a mistake like this:

```kotlin
class SourceStyle {
    companion object : CssGlobalValues<SourceStyle>
}


class CopiedStyle {
  // ❌
  companion object : CssGlobalValues<SourceStyle> // <-- Oops, should be CopiedStyle
}
```

⚠️ Be extra vigilant to this mistake. If you miss catching it here, you have one more chance to catch it in the tests.

---
### Additional enum values should be declared in the companion object and use `unsafeCast`

Also, they should be TitleCamelCase, and use `get()` so they are resolved lazily

#### Example

```kotlin
sealed interface StyleExample : StylePropertyValue {
    companion object : CssGlobalValues<StyleExample> {
      val FirstValue get() = "first-value".unsafeCast<StyleExample>()
      val SecondValue get() = "second-value".unsafeCast<StyleExample>()
    }
}
```

---
### Shared enum values can be refactored into an internal, sealed CssValues<T> interface.

#### Example

```kotlin
// The style interfaces here are made up for explanatory purposes only

// Shared by OutlineShape and SolidShape
internal sealed interface CssShapeValues<T: StylePropertyValue> {
  val Circle get() = "circle".unsafeCast<T>()
  val Rectangle get() = "rectangle".unsafeCast<T>()
  val Square get() = "square".unsafeCast<T>()
  val Triangle get() = "triangle".unsafeCast<T>()
}

sealed interface OutlineShape : StylePropertyValue {
    companion object : CssShapeValues<OutlineShape>, CssGlobalValues<OutlineShape>
}
sealed interface SolidShape : StylePropertyValue {
    companion object : CssShapeValues<SolidShape>, CssGlobalValues<SolidShape>
}
```

---
### Create `of` companion object methods for dynamic (non-enum) values

#### Example

```kotlin
sealed interface ExampleStyle : StylePropertyValue {
    companion object : CssGlobalValues<ExampleStyle> {
      fun of(value: Int) = "$value".unsafeCast<ExampleStyle>
      fun of(value: String) = value.unsafeCast<ExampleStyle>
    }
}
```

---
### Expose a child sealed interface if you need to support special keywords

Sometimes, a style has a subset of its keywords that it can accept in two-, three-, or more arguments.

For example, imagine a style that wants you to pass in instructions if it should either round or floor X and Y
coordinates as part of a pixel rendering calculation. Here, `pixel: round floor` or `pixel: floor floor` would be
allowed, but `pixel: inherit inherit` would not.

For this, will need to declare and expose a new child interface. If you can think of a specific, appropriate name that makes
sense, go for it; otherwise, we have tended to use `Mode` as a common default.

#### Example

```kotlin
sealed interface Pixel : StylePropertyValue {
  sealed interface Mode : Pixel

  companion object : CssGlobalValues<Pixel> {
    fun of(x: Mode, y: Mode) = "$x $y".unsafeCast<Pixel>()

    val Round get() = "round".unsafeCast<Mode>()
    val Floor get() = "floor".unsafeCast<Mode>()
  }
}
```

Here, we need to expose multiple interfaces for even more fine-grained control. `Mode` doesn't make sense here:
```kotlin
sealed interface Anchor : StylePropertyValue {
  sealed interface HorizOrCenter : Anchor
  sealed interface VertOrCenter : Anchor
  sealed interface Center: HorizOrCenter, VertOrCenter
  sealed interface Horiz : HorizOrCenter
  sealed interface Vert : VertOrCenter

  companion object : CssGlobalValues<Anchor> {
    fun of(x: HorizOrCenter, y: VertOrCenter) = "$x $y".unsafeCast<Anchor>()

    val Left get() = "left".unsafeCast<Horiz>()
    val Right get() = "right".unsafeCast<Horiz>()
    val Top get() = "top".unsafeCast<Vert>()
    val Bottom get() = "bottom".unsafeCast<Vert>()
    val Center get() = "center".unsafeCast<Center>()
  }
}
```

---
### Handling keywords that modify other keywords

Some CSS properties have keywords that should only appear in conjunction with other keywords and cannot appear alone.

We have so far identified two cases, one where the modifying keyword only ever apply to a single target keyword,
and one where it applies to a collection of other keywords.

Both kinds occur in the `align-content` property so we'll use that to highlight them.

* "first baseline" / "last baseline"
   * `first` and `last` are modifying keywords
   * they only ever affect `baseline`; they are not used with any other keyword

* "safe start" / "unsafe end"
   * `safe` and `unsafe` are modifying keywords
   * they act on any of the positional keywords (e.g. `start`, `end`, `center`).

We take approaches which ensure that our APIs will show up in autocomplete lists, as in the following cases (where `|`
represents the cursor): `AlignContent.Firs|`, `AlignContent.Saf|`

Note that each case requires a slightly different approaches in the Kotlin code, whether there is a single target or
not.

#### The modifying keyword only applies to a single target keyword

This is the "first baseline" / "last baseline" case.

As there is only one target keyword being modified (here, "baseline"), we can keep it simple and create direct
properties to represent these additional cases.

```kotlin
sealed interface AlignSelf : StylePropertyValue {
    companion object : CssGlobalValues<AlignSelf> {
        val Baseline get() = "baseline".unsafeCast<AlignSelf>() // Keyword
        val FirstBaseline get() = "first baseline".unsafeCast<AlignSelf>() // Modified keyword
        val LastBaseline get() = "last baseline".unsafeCast<AlignSelf>() // Modified keyword
    }
}
```

#### The modifying keyword applies to multiple target keywords

This is the "safe $position" / "unsafe $position" case.

We handle this by creating `Safe` and `Unsafe` methods (capitalized, so they feel like keywords) and have them accept a
positional keyword instance as their argument.

```kotlin
sealed interface AlignSelf : StylePropertyValue {
    sealed interface AlignContentPosition : AlignContent

    companion object : CssGlobalValues<AlignContent> {
        // Positional
        val Center: AlignContentPosition
        val Start: AlignContentPosition
        val End: AlignContentPosition
        val FlexStart: AlignContentPosition
        val FlexEnd: AlignContentPosition

        // Overflow
        fun Safe(position: AlignContentPosition) = "safe $position".unsafeCast<AlignContent>()
        fun Unsafe(position: AlignContentPosition) = "unsafe $position".unsafeCast<AlignContent>()
    }
}
```

#### No public `of` methods

Note that in the above classes, we don't provide public `of` methods. We could have added a
`AlignSelf.of(Safety, Position)` method for example. 

This is for the following reasons:

* Keeps the API simple -- there's only one way to do something
* This means you don't have to spend time creating an extra `Modifier` method to accompany it
  * Why call `Modifier.example(Example.First, Example.Second)` when you can call
    `Modifier.example(Example.First(Example.Second))` which is the same number of characters?
* The method syntax can help users build a stronger mental model that the keywords are tightly related
  * `Example.of(First, Second)` represents two values working separately, e.g. on different axes, while
    `Example.of(First(Second))` emphasizes that these two values are connected somehow.

---
### Create `list` methods for listable values

Several CSS properties exist that accept lists of values. But not every valid value for that property is accepted -- it
is a particular subset of them. (For example, you can declare a list of animations, but declaring the word "inherit"
multiple times is invalid.)

Because this pattern is so common, we've developed a standard for it. You should expose a sealed interface called
`Listable` and provide a `list` companion object method which takes `vararg values: Listable`.

#### Example

```kotlin
sealed interface StyleExample : StylePropertyValue {
  sealed interface Listable : StyleExample

  companion object : CssGlobalValues<StyleExample> {
    fun of(value: Int) = "$value".unsafeCast<Listable>()
    fun list(vararg examples: Listable): StyleExample = examples.joinToString().unsafeCast<StyleExample>()

    val None: StyleExample get() = "none".unsafeCast<StyleExample>() // not listable!
  }
}
```

For a time, we considered a pattern that would guarantee at least one item, because "no items" is invalid CSS:

```kotlin
❌
fun list(first: StyleExample.Listable, vararg rest: StyleExample.Listable) =
  (listOf(first) + rest).joinToString().unsafeCast<StyleExample>()
```

Though tempting, in practice it can be convenient for users to build up lists of arguments and then pass those in:

```kotlin
var animations = mutableListOf<Animation>()
if (condX) {
  animations.add(...)
}
if (condY) {
  animations.add(...)
}

Animation.list(animations)
```

so we decided to potentially allow people to pass in empty lists, even if that generates invalid CSS (which the browser
can handle gracefully).

---
### Every CSS style property should have a single StyleScope extension

#### Example

```kotlin
sealed interface StyleExample : StylePropertyValue { /*...*/ }

fun StyleScope.styleExample(styleExample: StyleExample) {
    // styleExample can be passed in here because it is a StylePropertyValue!
    property("style-example", styleExample)
}
```

It can be tempting to get creative with adding a ton of convenience `StyleScope` extensions for common, primitive values
(e.g. `StyleScope.columnCount(count: Int)`), and at some point later, maybe we *will* add those.

But for now, we believe that these Compose HTML APIs are NOT our responsibility. They are the responsibility of the
Compose HTML team.

Therefore, we have decided to keep these extensions minimal and provide a richer experience through the `Modifier` APIs
(which is how most people will interact with CSS styles through Kobweb).

> [!IMPORTANT]
> You may discover there is quite a bit of old code here and there that defies this rule. That's because we added those
> a long time ago and have grandfathered them in for backwards compatibility purposes. We plan to deprecate and remove
> these before Kobweb hits 1.0.

Note that, when defining extension methods for longhand properties that don't have classes associated with them (meaning
they likely represent a primitive, raw value), in that case you should create a `StyleScope` extension which takes in
that value and not a class:

```kotlin
// Longhand property that doesn't have a class
fun StyleScope.outlineStyle(value: LineStyle) {
  property("outline-style", value)
}
```

#### Exception

Longhand CSS properties that don't have an associated class and may have multiple variants will still need multiple
extension methods.

For example:

```kotlin
fun StyleScope.borderStyle(lineStyle: LineStyle) {
  property("border-style", lineStyle.value)
}

fun StyleScope.borderStyle(topBottom: LineStyle = LineStyle.None, leftRight: LineStyle = LineStyle.None) {
  property("border-style", "$topBottom $leftRight")
}

/* etc. */
```

If you think you've run into a case where a single extension method isn't enough but aren't sure, please reach out to
the Kobweb maintainers to discuss it.

---
### Don't explicitly set default values

According to the docs, you'll often find the default values for certain parts of the property have a default value. When
you leave this unset, the browser fills it in for you. We don't want to get in the way of that.

#### Example

```kotlin
// This is a simpler version of the real Animation style property
sealed interface Animation : StylePropertyValue {
  companion object {
    fun of(
      name: String,
      duration: Duration? = null,
      direction: Direction? = null,
      count: AnimationCount? = null,
    ) = buildList {
      add(name)
      duration?.let { add(it) }
      direction?.let { add(it) }
      count?.let { add(it) }
    }.joinToString(" ").unsafeCast<Animation>()
  }
}
```

Notice above we omitted the various values that weren't specified by the user. In contrast, we *could* have done this:

```kotlin
// ❌
sealed interface Animation : StylePropertyValue {
  companion object {
    fun of(
      name: String,
      duration: Duration? = null,
      direction: Direction? = null,
      count: AnimationCount? = null,
    ) = buildList {
      add(name)
      add(duration ?: 0.s)
      add(direction ?: Direction.Normal)
      add(count ?: AnimationCount.of(1))
    }.joinToString(" ").unsafeCast<Animation>()
  }
}
```

However, by letting the browser fill in the defaults, we produce CSS that has less chance of having a mistake, is easier
to debug because the text is cleaner, and it generates smaller final HTML snapshot sizes at export time.

#### Exception

Sometimes, you have to specify a value explicitly to avoid ambiguity. There is actually a value I did not include in the
incomplete Animation example above. I'll add it now -- a `delay` duration:

```kotlin
sealed interface Animation : StylePropertyValue {
  companion object {
    fun of(
      name: String,
      duration: Duration? = null,
      delay: Duration? = null, // <-- New
      direction: Direction? = null,
      count: AnimationCount? = null,
    ) = buildList {
      add(name)
      duration?.let { add(it) }
      // New:
      if (delay != null) {
        if (duration == null) {
          add("0s") // Needed so parser knows that the next time string is for "delay"
        }
        add(delay.toString())
      }

      direction?.let { add(it) }
      count?.let { add(it) }
    }.joinToString(" ").unsafeCast<Animation>()
  }
}
```

This exception should be pretty rare because most shorthand CSS properties are a group of uniquely distinguishable
values.

---
### Add tests to `CssStylePropertyTests`

This will help verify both to yourself AND to the person reviewing the code that the API you designed captured the
full functionality of the target CSS property.

* You MUST add an assertion for every single enum value exposed by your class.
* You SHOULD try to cover as many unique cases as possible for dynamic `of` methods.
  * A good rule of thumb is to look at the associated MDN documentation and many / all the examples they share there.

[Link to tests.](../../../../../../../jsTest/kotlin/com/varabyte/kobweb/compose/css/CssStylePropertyTests.kt)

## The `Css` vs `CSS` prefix

As you work through this codebase, you may come across both types of prefixes. At first glance, this can seem like a
sloppy inconsistency, but there is actually a significant difference.

The `Css` prefix indicates that a target class is describing itself in context of it performing a role in CSS-related
code.

The `CSS` prefix indicates that we would have liked to call a class "Xyz" but, due to conflicts with the Kotlin language
or stdlib etc., that name wasn't available, so "CSSXyz" is our compromise. In other words, the "CSS" prefix acts as a
poor man's namespace.

## Examples

There are generally two types of CSS style properties you'll encounter in the wild -- one which is a glorified enum, and
the other which provides a rich range of functionality. The former can be always represented as a simple class while the
latter often leans on sealed classes to break behavior up.

For a handful of simple enum examples, take a look at:

* `BoxDecorationBreak`
* `FlexBasis`
* `TextAlign`

For slightly more complex cases that still don't need to use sealed classes, take a look at:

* `AccentColor`
* `BorderImage`
* `ColumnCount`

And for more complex examples, take a look at:

* `Animation`
* `FontVariantAlternates`
* `Transition`
