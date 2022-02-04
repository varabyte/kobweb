A layer of Jetpack Compose-inspired code on top of Compose for Web

This module introduces the Jetpack Compose concept of `Modifier` but with an implementation designed to work well with
Compose for Web specifically (by delegating to its concept of `AttrBuilder`s and `StyleBuilder`s). It also
introduces a handful of foundation classes that use it, porting (a subset of) Jetpack Compose's foundation layer.

Note that, unlike the approach that Jetbrains is taking with their Multiplatform Widgets approach, this `Modifier`
class and its extension methods are _not_ trying to be 100% compatible with Android / Desktop compose. See the main
README's [What about Multiplatform Widgets](https://github.com/varabyte/kobweb#what-about-multiplatform-widgets) section
for more details on why.

**NOTE**: If you use this library, you must be sure to initialize its stylesheet, like so:

```kotlin
fun main() {
    renderComposable(rootElementId = "root") {
        StyleSheet(KobwebComposeStyleSheet) // REQUIRED
        ...
    }
}
```

If you are using Silk, however, this has been done for you.