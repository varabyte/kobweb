Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

Lucide icons are rendered as inline SVGs using Kobweb's `createIcon` API - no external JavaScript library or CDN
is required. Each icon composable generates the SVG elements directly in the DOM.

## Icon parameters

All icon composables share the same optional parameters:

| Parameter     | Type              | Default        | Description                                                                                    |
|---------------|-------------------|----------------|------------------------------------------------------------------------------------------------|
| `modifier`    | `Modifier`        | `Modifier`     | Standard Kobweb modifier; use it to set CSS classes, layout, etc. (replaces the `class` prop). |
| `size`        | `CSSLengthValue`  | `1.em`         | Sets the width of the SVG icon.                                                                |
| `strokeWidth` | `Number`          | `2`            | Sets the `stroke-width` SVG attribute.                                                         |
| `color`       | `CSSColorValue?`  | `null`         | Sets the `stroke` SVG attribute. Defaults to inheriting the CSS `color` property.              |

Example:

```kt
LucideHome(size = 32.px, color = Colors.Red, strokeWidth = 1.5)
```

---

Note that this directory contains a file called `lucide-icons.json`, which is parsed and used to generate code
used in this project.

Each icon is generated as a separate Kotlin file to enable dead code elimination in Kotlin/JS -
only the icons you actually use will be included in the final bundle.

After updating `lucide-icons.json`, run the Gradle task to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-lucide:generateIcons
```

To fetch the latest icon data from the Lucide repository (updating `lucide-icons.json`):

```bash
./gradlew :frontend:silk-icons-lucide:fetchLucideIcons
```
