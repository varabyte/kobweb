The part of Silk that is pure widgets and doesn't depend on Kobweb at all.

Although Silk UI is considered a major benefit of the Kobweb package, it's quite possible that some users will want to
use just the widgets -- for example, maybe they have their own existing Compose for Web project which can't be easily
ported to Kobweb, but they still want to use our fancy color mode support or widgest like SimpleGrid.

When using this widget library on its own, you must initialize it yourself, since that's normally something the Kobweb
plugin does for you. To do that, call `initSilk` before your Compose for Web entry point and then `Style(SilkStyleSheet)`
within it!

```kotlin
fun main() {
    initSilk() // <-- MUST DO #1
    renderComposable(rootElementId = "root") {
        Style(SilkStyleSheet) // <-- MUST DO #2
        /* ... */
    }
}
```

You can optionally tweak Silk configurations (such as palette colors) by using a passed-in callback:

```kotlin
fun main() {
    initSilk { ctx ->
        ctx.config.initialColorMode = ColorMode.DARK
        ctx.theme.palettes = ...
    }
    ...
}
```

If you write your own custom widgets and want to take advantage of Silk's powerful `ComponentStyle` support, you'll
have to remember to register them explicitly (another thing the Kobweb plugin normally does for you):

```kotlin
// File: CustomWidget.kt

val CustomStyle = ComponentStyle("custom-widget") {
    hover = Modifier.background(...)
}

@Composable
fun Custom(modifier: Modifier, ...) {
    val finalModifier = CustomStyle.toModifier().then(modifier)
    ...
}

// File: main.kt

fun main() {
    initSilk { ctx ->
        ctx.theme.registerComponentStyle(CustomStyle)
    }
    ...
}
```