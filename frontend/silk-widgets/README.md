The part of Silk that is pure widgets and doesn't depend on Kobweb at all.

Although Silk UI is considered just a part of the Kobweb ecosystem, it's quite possible that some users will want to use
just the widgets -- for example, maybe they have their own existing Compose HTML project which can't be easily
ported to Kobweb, but they still want to use our fancy color mode support or widgets like SimpleGrid.

When using this widget library on its own, you must initialize it yourself, since that's normally something the Kobweb
plugin does for you. To do that, you must call `SilkFoundationStyles`, `SilkWidgetVariables`, and
`DeferringHost` inside the `renderComposable` block:

```kotlin
fun main() {
    renderComposable(rootElementId = "_kobweb-root") {
        KobwebComposeStyles() // OPTIONAL but recommended for `Box`, `Row`, `Column`, etc. support
        SilkFoundationStyles() // REQUIRED
        SilkWidgetVariables() // REQUIRED
        DeferringHost { // REQUIRED (if you plan to use `Deferred` or widgets that use it like tooltips)
            /* ... your content here ... */ 
        }
    }
}
```

The `SilkFoundationStyles` additionally includes a way to add custom Silk initialization logic. For example, you can
optionally tweak Silk configurations (such as palette colors) or change the initial color mode. You MUST also register
Silk widget styles here:

```kotlin
SilkFoundationStyles(
    initSilk = { ctx ->
        com.varabyte.kobweb.silk.init.initSilkWidgets(ctx) // REQUIRED
        ctx.config.initialColorMode = ColorMode.DARK
        ctx.theme.palettes.light.color = ...
        ctx.theme.palettes.dark.color = ...
    }
) {
    /* ... */
}
```

If you write your own custom widgets and want to take advantage of Silk's powerful `CssStyle` support, you'll
have to remember to register them explicitly (another thing the Kobweb plugin normally does for you):

```kotlin
// File: CustomWidget.kt

val CustomStyle = CssStyle {
    base {
        Modifier.background(...)
    }
    hover {
        Modifier.background(...)
    }
}

// File: main.kt

SilkFoundationStyles(
    initSilk = { ctx ->
        /* ... */
        ctx.theme.registerStyle("custom", CustomStyle)
    }
) {
   /* ... */
}
```

See the README for `silk-foundation` for more information.
