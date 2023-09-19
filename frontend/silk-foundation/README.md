This is a foundational layer of Silk that its widgets are built on top of.

Kobweb is designed to allow users to use their own UI framework if they'd like. However, there's a core part of Silk
that is extremely useful and general purpose, so it has been extracted into its own library. If a project includes this
library directly without Silk widgets, they can still:

* define `ComponentStyle`s
* define `Keyframe`s
* set the current color mode (light/dark) which they can refer to from their component styles.
* branch styles and behavior on site breakpoints
* use the `deferRender` method, which should be always be preferred over needing to use `Modifier.zIndex()`

You can think of this library as the core utilities and bits that make it easy to style your Kobweb site above and
beyond the limited tools offered by Compose HTML.

See the README for `silk-widgets` for more information.
