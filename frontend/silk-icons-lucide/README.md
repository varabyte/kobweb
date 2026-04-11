Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

This directory contains a file called `lucide-icons.json`, which is parsed and used to generate code
used in this project.

To update it:

```bash
./gradlew :frontend:silk-icons-lucide:fetchLucideIcons
```

Once updated, run the following to regenerate the Kotlin source:

```bash
./gradlew :frontend:silk-icons-lucide:generateIcons
```

> [!NOTE]
> Each icon is generated as a separate Kotlin file to enable dead code elimination in Kotlin/JS -
> only the icons you actually use will be included in the final bundle.
