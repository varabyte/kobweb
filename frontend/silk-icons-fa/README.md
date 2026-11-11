Support for integration of font awesome icons in your project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/<version>/css/all.min.css" />
```

where `<version>` is this module's `silkIcons.version` (see `build.gradle.kts`). This adds to the size of your page,
something you should be mindful of if you don't plan to use any of these (awesome!) icons.

A page can only load one Font Awesome stylesheet, so if your app also generates its own Font Awesome bindings, the
stylesheet uses your app's version instead.

---

This directory contains a file called `fa-icon-list.txt`, which is parsed and used to generate code used in this
project. It is built from the `metadata/icons.json` file in the
[Font Awesome GitHub repository](https://github.com/FortAwesome/Font-Awesome), at the release tag matching
`silkIcons.version` in `build.gradle.kts`.

The built artifact records `silkIcons.version`, and sites load the Font Awesome stylesheet for that version, so the two
stay in sync automatically.

To update `fa-icon-list.txt` (e.g. after changing `silkIcons.version`):

```bash
./gradlew :frontend:silk-icons-fa:fetchIcons
```

Once updated, run the following to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-fa:generateIcons
```
