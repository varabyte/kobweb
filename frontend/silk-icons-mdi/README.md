Support for integration of material design icons in your project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp" />
```

which adds to the size of your page, something you should be mindful of if you don't plan to use any of these icons.

---

This directory contains a file called `md-icon-list.txt`, which is parsed and used to generate code used in this
project. It is built from the codepoint files under `font/` in
[`google/material-design-icons`](https://github.com/google/material-design-icons), one per style, at the commit
pinned by `MDI_VERSION` in `build.gradle.kts`.

To update it:

```bash
./gradlew :frontend:silk-icons-mdi:fetchIcons
```

Once updated, regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-mdi:generateIcons
```
