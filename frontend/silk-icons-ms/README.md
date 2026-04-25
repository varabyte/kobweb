Support for integration of [Google Material Symbols](https://developers.google.com/fonts/docs/material_symbols) in
your Kobweb project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined&family=Material+Symbols+Rounded&family=Material+Symbols+Sharp" />
```

which adds to the size of your page, something you should be mindful of if you don't plan to use any of these icons.

Material Symbols ship in three styles: `OUTLINED`, `ROUNDED`, and `SHARP`. You can select a style per-icon via the
`style` parameter, e.g. `MsHome(style = MsIconStyle.ROUNDED)`. If you don't pass one, icons render in the `OUTLINED`
style, which matches Google's documented default.

> [!NOTE]
> Material Symbols also supports four variable-font axes (`FILL`, `wght`, `GRAD`, `opsz`). This module does not yet
> expose them — the generated composables render at each font's default axis values. Configuring axes per-icon will be
> added in a follow-up.

---

This directory contains a file called `ms-icon-list.txt`, which is parsed and used to generate code for this project.

To update it, run the `fetchMsIcons` Gradle task, which downloads the codepoint files from
[`google/material-design-icons`](https://github.com/google/material-design-icons) at the commit pinned in
`build.gradle.kts`:

```bash
./gradlew :frontend:silk-icons-ms:fetchMsIcons
```

Once updated, regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-ms:generateIcons
```
