# Releasing

Things to check before every release.

## Update icon sets

Each `frontend/silk-icons-*` module pins an upstream version with `silkIcons.version` in its `build.gradle.kts`. For
each one:

1. Bump `silkIcons.version`. Font Awesome and Lucide use release tags; Material Icons and Material Symbols use a commit
   SHA from https://github.com/google/material-design-icons.
2. Run `./gradlew :frontend:silk-icons-<set>:fetchIcons`, then `generateIcons`, and check the diff for removed or
   renamed icons, which break users.

## Remove old deprecated code

Before 1.0, deprecated code is removed once it's been deprecated for (at least) six months. (Our `@Deprecated` tags
don't record when something was deprecated, so use `git blame` on the annotation to find out.)
