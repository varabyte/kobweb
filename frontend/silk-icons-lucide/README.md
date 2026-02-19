Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

Unlike Font Awesome or Material Design Icons, Lucide icons are rendered as inline SVGs via a small JavaScript
library — no CSS font file is needed. You must load the Lucide JS library on your page, for example via CDN:

```html
<script async src="https://unpkg.com/lucide@latest"></script>
<script>lucide.createIcons();</script>
```

Prefer using a specific version instead of `latest` to avoid breaking changes:

```html
<script async src="https://unpkg.com/lucide@0.574.0/dist/umd/lucide.min.js"></script>
```
Or even directly in the `head` of your Kobweb:
```kt
script(type = "text/javascript", src = "https://unpkg.com/lucide@0.574.0/dist/umd/lucide.min.js") {
    async = true // Using async to avoid blocking the page rendering
}
```

Then inside of your Kobweb App or Page, you then create the icons:
```kt
external interface Lucide {
    fun createIcons()
}

external val lucide: Lucide

KobwebApp {
    content()

    LaunchedEffect(Unit) {
        lucide.createIcons()
    }
}
```

Each icon is rendered as an `<i data-lucide="icon-name">` element, which the Lucide JS library replaces with an
inline SVG at runtime.

## Icon parameters

All icon composables (both `LucideIcon` and every named wrapper like `LucideHome`) share the same optional parameters:

| Parameter            | Type      | Default        | Description                                                                                           |
|----------------------|-----------|----------------|-------------------------------------------------------------------------------------------------------|
| `modifier`           | `Modifier`| `Modifier`     | Standard Kobweb modifier; use it to set CSS classes, layout, etc. (replaces the `class` prop).       |
| `absoluteStrokeWidth`| `Boolean` | `false`        | When `true`, keeps stroke thickness visually constant regardless of `size` (`strokeWidth × 24 / size`). |
| `color`              | `String?` | `null` (→ `currentColor`) | Sets the `stroke` SVG attribute. Defaults to inheriting the CSS `color` property.      |
| `size`               | `Int?`    | `null` (→ 24 px) | Sets both `width` and `height` on the SVG.                                                          |
| `strokeWidth`        | `Number?` | `null` (→ 2)   | Sets the `stroke-width` SVG attribute.                                                                |

Example:

```kt
LucideHome(size = 32, color = "red", strokeWidth = 1.5)
LucideIcon("home", size = 32, absoluteStrokeWidth = true)
```

---

Note that this directory contains a file called `lucide-icon-list.txt`, which is parsed and used to generate code
used in this project.

The generated `lucide-icon-list.txt` format is:
```
# Lucide icon list — version X.Y.Z
lucide=icon1,icon2,...
deprecated=old-name>canonical-name,...
```

After updating `lucide-icon-list.txt`, run the Gradle task to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-lucide:generateIcons
```
