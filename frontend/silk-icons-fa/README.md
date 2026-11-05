Support for integration of font awesome icons in your project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
```

which adds to the size of your page, something you should be mindful of if you don't plan to use any of these (awesome!)
icons.

---

This directory contains a file called `fa-icon-list.txt`, which is parsed and used to generate code used in this
project. It is built from the `metadata/icons.json` file in the
[Font Awesome GitHub repository](https://github.com/FortAwesome/Font-Awesome), at the release tag matching `FA_VERSION`
in `build.gradle.kts`.

To update it (e.g. after changing `FA_VERSION`, along with the matching stylesheet URL in
`KobwebGenerateSiteIndexTask`):

```bash
./gradlew :frontend:silk-icons-fa:fetchIcons
```

Once updated, run the following to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-fa:generateIcons
```
