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

### Every CSS style property should link to its MDN docs page

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
```

We will eventually convert these into full-blown header comments, but for now, at least adding the link there is useful
for developers who need to quickly jump to the link to understand the property's behavior and syntax.

---
### Every CSS style property should have a corresponding class

#### Example

```kotlin
// See: https://developer.mozilla.org/en-US/docs/Web/CSS/style-example
class StyleExample /*...*/
```

Even if the property is 99% of the time a simple integer value (e.g. `column-count`), we still need to support users
being able to pass in global values (e.g. `ColumnCount.Inherit`), so we will always need a class for every single
property type.

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
        val INHERIT = SourceStyle("inherit")
    }
}


class CopiedStyle {
    companion object {
        // ❌
        val INHERIT = SourceStyle("inherit")
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
> a long time ago and have grandfathered them in for backwards compatibility purposes.

#### Exception

Occasionally, there can be situations where multiple extension methods are required. For example, the `animation`
property actually supports declaring multiple animations, but this excludes keywords.

For example, `animation: rotate 1s, spin 3s` is allowed but `animation: none, none` is not.

Therefore, we provide two extension methods, one for the "single argument only" case and the other for the "repeated
arguments allowed" case:

```kotlin
fun StyleScope.animation(animation: Animation) {
    property("animation", animation)
}

fun StyleScope.animation(first: Animation.Repeatable, vararg rest: Animation.Repeatable) {
    property("animation", (listOf(first) + rest).joinToString(", "))
}
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

However, exceptions can be made if necessary for API reasons.

A pattern we've run into is that sometimes we need to distinguish between CSS values that can only ever be passed in
as a single value, vs. values that can be repeated. As this seems to happen quite often, we've started regularly using
the `Repeatable` subclass name for this situation, which we expose publicly:

```kotlin
sealed class StyleExample {
    private class Keyword(value: String) : StyleExample(value)
    class Repeatable(vararg values: Int) : StyleExample(values.joinToString(" "))
    
    companion object {
        fun of(vararg values: Int) = Repeatable(*values)
        val None: StyleExample = Keyword("none")
    }
}

fun StyleScope.styleExample(styleExample: StyleExample)
fun StyleScope.styleExample(first: StyleExample.Repeatable, vararg rest: StyleExample.Repeatable)
```

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
