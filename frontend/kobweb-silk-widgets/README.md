The part of Silk that is pure widgets and doesn't depend on Kobweb at all.

Although Silk UI is considered a major benefit of the Kobweb package, it's quite possible that some users will want to
use just the widgets -- for example, maybe they have their own existing Web Compose project which can't be easily
ported to Kobweb, but they still want to use our fancy color mode support or widgest like SimpleGrid.

When using this widget library on its own, you must initialize it yourself, since you probably won't be using the
Kobweb plugin. To do that, be sure to call `initSilk` before your Web Compose entry point and then
`Style(SilkStyleSheet)` within it!

```kotlin
fun main() {
    initSilk()
    renderComposable(rootElementId = "root") {
        Style(SilkStyleSheet)
        /* ... */
    }
}
```

You can optinally tweak Silk configurations a bit using a passed-in callback:

```kotlin
fun main() {
    initSilk { ctx ->
        ctx.config.initialColorMode = ColorMode.DARK
        ctx.theme.palettes = ...
    }
    ...
}
```

If you add any of your own custom widgets and want to take advantage of Silk's powerful `ComponentStyle` support, you'll
have to remember to register them explicitly:

```kotlin
// File: CustomWidget.kt

val CustomStyle = ComponentStyle("custom-widget") {
    ...
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