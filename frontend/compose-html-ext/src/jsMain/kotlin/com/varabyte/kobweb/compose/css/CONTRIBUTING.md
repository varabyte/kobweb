# Kotlin bindings for CSS style properties

When we first began work on Kobweb, we did not realize that bindings for so many CSS properties were still missing (and,
worse, that some of the ones provided are incomplete).

The goal of this package (at the moment) is to do the minimal amount of work filling in the gaps left behind by the
Compose HTML team. We have also decided on some standard patterns that have proven resilient to even the most complex
CSS style properties we've faced.

Also consider
reviewing [the CSS Kobweb Checklist](<https://docs.google.com/spreadsheets/d/1Uu2diibyOzDFPgzzM8BWlw4B9_CTDBACv_1E_tsXbp8>)
to see the list of CSS style properties that are still unimplemented.

## Rules for contributing new CSS style property classes

### We only support properties and features that are baseline widely available

In the [MDN docs](<https://developer.mozilla.org/en-US/>), confirm in the top right corner of the property's page that
it is labeled as widely available across all major browsers.

Sometimes, this label will be tagged with an asterix (i.e. "Baseline Widely Available *"), which means that while the
property is overall safe to use, some parts of the feature are not. In that case, you should check out its
*Browser Compatibility* section for more information.

As a concrete example, as of 2025, [`touch-action`](https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action) is
baseline widely available but several of its values (the `pan-` cardinal directions) are not, as listed in its
[browser compatibility section](<https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action#browser_compatibility>).

You may also wish to refer to https://caniuse.com/ for even more detailed reports about what properties and features are
actually supported.

### Every CSS style property should link to its MDN docs page

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
```

We will eventually convert these into full-blown header comments, but for now, at least adding the link there is useful
for developers who need to quickly jump to the link to understand the property's behavior and syntax.

---
### Every **non-longhand** CSS style property should have a corresponding class

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
class StyleExample /*...*/
```

Even if the property is 99% of the time a simple integer value (e.g. `column-count`), we still need to support users
being able to pass in global values (e.g. `ColumnCount.Inherit`), so we will always need a class for every single
property type.

#### Exception

Some CSS properties are called *shorthand* and some are called *longhand*. A shorthand property is one that is a
grouped representation of several inner properties, aka longhand properties.

For example, `margin-inline` is a shorthand property, while `margin-inline-start` and `margin-inline-end` are
longhand properties.

Many longhand properties are simple primitive values (such as `border-width`).

There are hundreds of these longhand properties (`border` alone has about fifty of them), so we've decided not to add
classes for these at this point. So far, users haven't needed them.

If a longhand property is notably complex, like several of the `animation` longhand properties are, then you can of
course introduce classes for those cases. But we do not plan to support most of them at this time.

```kotlin
❌
class MarginInlineEnd /* ... */
```

---
### Every class should have a private constructor

#### Example

```kotlin
class StyleExample private constructor(/*...*/)
```

Code in this package this is often copy-pasted around when starting on new classes. From experience, we have seen it can
be easy to make a mistake like this:

```kotlin
class SourceStyle {
    companion object {
        val Inherit = SourceStyle("inherit")
    }
}


class CopiedStyle {
    companion object {
        // ❌
        val Inherit = SourceStyle("inherit")
    }
}
```

By making the constructors private, we ensure that such copy/pasted code will immediately generate a compile error.

---
### Every class should take in a single `String` value, inherit from `StylePropertyValue`, and provide a `toString`

#### Example

```kotlin
class StyleExample private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value
}
```

This pattern lets us pass these classes into the Compose HTML `property` method, which we will observe in the next
section.

---
### Every CSS style property should have a single StyleScope extension

#### Example

```kotlin
class StyleExample /*...*/ : StylePropertyValue { /*...*/ }

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
> a long time ago and have grandfathered them in for backwards compatibility purposes. We may deprecate and remove these
> before Kobweb hits 1.0.

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

```

If you think you've run into a case where a single extension method isn't enough but aren't sure, please reach out to
the Kobweb maintainers to discuss it.

---
### Every class should act like a singleton and provide a companion object

#### Example

```kotlin
class StyleExample /*...*/ {
    companion object {
        fun of(value: Int): StyleExample
        fun of(value: String): StyleExample

        val FirstValue: StyleExample
        val SecondValue: StyleExample

        val Inherit: StyleExample
    }
}
```

This rule is consistent with the Compose HTML APIs, and it also plays very nice with the previous rule about only
providing a single StyleScope extension. This approach makes it so that any Kobweb developer (after 1.0) can look at a
widely available CSS property, type in its corresponding Kotlin name, and explore the whole API for constructing it in a
type-safe way. The instance they create can then be safely used in the relevant StyleScope or Modifier context.

Use simple properties as much as possible (e.g. `FirstValue` / `SecondValue` above) but, for more dynamic behavior
affected by user inputs, use the `of` pattern for that. As a name, `of` is very short and reads well
(e.g. `Animation.of(...)`). And by being consistent, users will know they can rely on it being there.

---
### When using sealed classes, don't expose subclasses unless necessary

Sealed classes can get pretty fancy, but we should strive to hide that complexity from the user. Explicitly hide the
subclass type by casting it to the parent public type instead.

#### Example

```kotlin
sealed class StyleExample {
   private class OfInt(value: Int) : StyleExample(value.toString())
   private class OfString(value: String) : StyleExample(value)

   companion object {
      fun of(value: Int): StyleExample = OfInt(value)
      fun of(value: String): StyleExample = OfString(value)
   }
}
```

With the above approach, the user can be blissfully unaware of the fact that they're working with anything else than
just a simple CSS style class.

#### Exception

Exceptions can be made if necessary for API reasons.

The `AlignItems` (reproduced below with a handful of properties elided) serves as a useful example, where it exposes
keywords that have special safe / unsafe semantics:

```kotlin
sealed class AlignItems private constructor(private val value: String) : StylePropertyValue {
  override fun toString() = value

  private class AlignItemsKeyword(value: String) : AlignItems(value)
  class AlignItemsPosition internal constructor(value: String) : AlignItems(value)

  private class BaselineAlignment(baselineSet: BaselineSet?) : AlignItems(baselineSet.toValue())
  private class OverflowAlignment(strategy: OverflowStrategy, position: AlignItemsPosition) :
    AlignItems(strategy.toValue(position))

  companion object {
    // Basic
    val Normal: AlignItems get() = AlignItemsKeyword("normal")
    val Stretch: AlignItems get() = AlignItemsKeyword("stretch")

    // Positional
    val Center get() = AlignItemsPosition("center")
    val Start get() = AlignItemsPosition("start")
    val End get() = AlignItemsPosition("end")
    /*...*/

    // Overflow
    fun Safe(position: AlignItemsPosition): AlignItems = OverflowAlignment(OverflowStrategy.SAFE, position)
    fun Unsafe(position: AlignItemsPosition): AlignItems = OverflowAlignment(OverflowStrategy.UNSAFE, position)

    /*...*/
  }
}
```

---
### Don't explicitly set default values

According to the docs, you'll often find the default values for certain parts of the property have a default value. When
you leave this unset, the browser fills it in for you. We don't want to get in the way of that.

#### Example

```kotlin
// This is a simpler version of the real Animation style property
class Animation /*...*/ {
    companion object {
        fun of(
            name: String,
            duration: Duration?,
            direction: Direction?,
            count: AnimationCount?,
        ): Animation {
           return Animation(
               buildList {
                    add(name)
                    duration?.let { add(it) }
                    direction?.let { add(it) }
                    count?.let { add(it) }
               }.joinToString(" ")
           )
        }
    }
}
```

Notice above we omitted the various values that weren't specified by the user. In contrast, we *could* have done this:

```kotlin
// ❌
class Animation /*...*/ {
    companion object {
        fun of(
            name: String,
            duration: Duration?,
            direction: Direction?,
            count: AnimationCount?,
        ): Animation {
           return Animation(
               buildList {
                    add(name)
                    add(duration ?: 0.s)
                    add(direction ?: Direction.Normal)
                    add(count ?: AnimationCount.of(1))
               }.joinToString(" ")
           )
        }
    }
}
```

However, by letting the browser fill in the defaults, we produce CSS that is less chance of having a mistake, easier to
debug because the text is cleaner, and it generates smaller final HTML snapshot sizes at export time.

#### Exception

Sometimes, however, you have to specify a value explicitly to avoid ambiguity. There is actually a value I did not
include in the incomplete Animation example above. I'll add it now -- a `delay` duration:

```kotlin
// ❌
class Animation /*...*/ {
    companion object {
        fun of(
            name: String,
            duration: Duration?,
            delay: Duration?, // <--- New
            direction: Direction?,
            count: AnimationCount?,
        ): Animation {
           return Animation(
               buildList {
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

               }.joinToString(" ")
           )
        }
    }
}
```

This exception should be pretty rare, because most shorthand CSS properties are a list of uniquely distinguishable
values.

---
### How to handle CSS property value lists

Several CSS properties exist that accept lists of values. But not every valid value for that property is accepted -- it
is a particular subset of them. (For example, you can declare a list of animations, but declaring the word "inherit"
multiple times is invalid.)

Because this pattern is so common, we've developed a standard for it. You should use a sealed class, expose a subclass
named `Repeatable`, and provide a `list` companion object method which takes vararg parameters.

#### Example

```kotlin
sealed class StyleExample /*...*/ {
  private class Keyword(value: String) : StyleExample(value)
  private class ValueList(value: List<Any>) : StyleExample(value.joinToString())
  class Repeatable(value: String) : StyleExample(value)
  private class OfInt(value: Int) : Repeatable(value.toString())

  companion object {
    fun of(value: Int): Repeatable = OfInt(value)
    fun list(vararg examples: StyleExample.Repeatable): StyleExample = ValueList(examples)
    
    val None: StyleExample get() = Keyword("none")
  }
}
```

For a time, we considered a pattern that would guarantee at least one item, because no items is technically invalid:

```kotlin
❌
fun list(first: StyleExample.Repeatable, vararg rest: StyleExample.Repeatable): StyleExample =
    ValueList(listOf(first) + rest)
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

so we decided to potentially allow people to pass in empty lists, even if that is technically not valid CSS.

---
### Add tests to `CssStylePropertyTests`

This will help verify both to yourself AND to the person reviewing the code that the API you designed captured the
full functionality of the target CSS property.

* You MUST add an assertion for every single enum value exposed by your class.
* You SHOULD try to cover as many unique cases as possible for dynamic `of` methods.
  * A good rule of thumb is to look at the associated MDN documentation and many / all the examples they share there.

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
