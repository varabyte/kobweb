Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

> [!TIP]
> This artifact's bindings are generated at a single Lucide version, chosen by Kobweb. To pick the version yourself
> (e.g. to use newer icons), you can instead generate bindings in your own project, using
> `kobweb { app { icons { lucide { version.set("...") } } } }` or `kobweb { library { ... } }`.

This directory contains a file called `lucide-icons.json`, which is parsed and used to generate code
used in this project.

To update it (e.g. after changing `silkIcons.version` in `build.gradle.kts`):

```bash
./gradlew :frontend:silk-icons-lucide:fetchIcons
```

Once updated, run the following to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-lucide:generateIcons
```

> [!NOTE]
> Each icon is generated as a separate Kotlin file to enable dead code elimination in Kotlin/JS -
> only the icons you actually use will be included in the final bundle.
