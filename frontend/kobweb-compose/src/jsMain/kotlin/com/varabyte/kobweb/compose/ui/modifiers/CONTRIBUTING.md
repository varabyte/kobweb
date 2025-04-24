# Modifiers for CSS style properties

Before reading this document, you should first read
the [CONTRIBUTING document](../../../../../../../../../../compose-html-ext/src/jsMain/kotlin/com/varabyte/kobweb/compose/css/CONTRIBUTING.md)
in the `com.varabyte.kobweb.compose.css` package inside the `compose-html-ext` module.

This document will be much shorter, because in many cases, the `Modifier` APIs are pretty easy to write once the
style classes and `StyleScope` extensions are in place.

Instead, we'll highlight the differences between the `Modifier` API and `StyleScope` APIs, since we are trying to make
the `Modifier` API layer much more expressive than the underlying `StyleScope` layer.

## Rules for contributing new CSS style property modifiers

### Use the `styleModifier` block

#### Example

```kotlin
import com.varabyte.kobweb.compose.css.styleExample

fun Modifier.styleExample(styleExample: StyleExample) = styleModifier {
    styleExample(styleExample)
}
```

Make sure your imports are correct so that Kotlin doesn't interpret your code as a recursive call! 

---
### Add shortcuts for all of the `of` cases

In contrast to the StyleScope layer (which we are, for the moment, trying to keep as minimal as possible), we want using
modifiers to feel powerful and convenient. Therefore, if you have one or more `of` methods in the underlying style, go
ahead and create shortcut modifiers for them.

#### Example

```kotlin
// StyleScope part

class StyleExample {
    companion object { 
        fun of(value: Int): StyleExample
        fun of(value: String): StyleExample

        val FirstValue: StyleExample
        val SecondValue: StyleExample

        val Inherit: StyleExample
    }
}

fun StyleScope.styleExample(styleExample: StyleExample)

// Modifier part

fun Modifier.styleExample(styleExample: StyleExample) = styleModifier {
  styleExample(styleExample)
}

fun Modifier.styleExample(value: Int) = styleModifier {
  styleExample(StyleExample.of(value))
}

fun Modifier.styleExample(value: String) = styleModifier {
  styleExample(StyleExample.of(value))
}
```

---
### Shorthand properties should use a scope class

There is actually a subtle difference between setting `margin-inline: 5% 10%` and
`margin-inline-start: 5%; margin-inline-end: 10%` (although as written the two cases result in identical
output).

The difference is this: it is a common pattern in webdev where you can use a shorthand property to set some default
value and then use its longhand values to tweak parts of it conditionally (e.g. only when a breakpoint condition is met,
or using a style variable).

After experimenting, we found an approach that visually distinguishes these two approaches. For shorthand properties,
just set the modifier directly; but for longhand properties, set the inner values inside a scope.

#### Example

```kotlin
// assume `example-style` is shorthand and `example-style-min` and `example-style-max` are longhand.

// sets `example-style`
fun Modifier.exampleStyle(min: Int, max: Int) {
    exampleStyle(ExampleStyle.of(min, max))
}

class ExampleStyleScope internal constructor(private val styleScope: StyleScope) {
  fun min(min: Int) { styleScope.exampleStyleMin(min) }
  fun max(max: Int) { styleScope.exampleStyleMax(max) }
}

// Allows setting `example-style-min` and `example-style-max` inside the scope call
fun Modifier.exampleStyle(scope: ExampleStyleScope.() -> Unit): Modifier = styleModifier {
  ExampleStyleScope(this).scope()
}
```

See for yourself:

```kotlin
// `example-style`
Modifier.exampleStyle(min = 2, max = 10)

// `example-style-min` and `example-style-max`
Modifier.exampleStyle {
    min(2)
    max(10)
}
```
