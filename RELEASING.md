# Releasing

Things to check before and after every release.

## Before the release

### Update icon sets

Each `frontend/silk-icons-*` module pins an upstream version with `silkIcons.version` in its `build.gradle.kts`. For
each one:

1. Bump `silkIcons.version`. Font Awesome and Lucide use release tags; Material Icons and Material Symbols use a commit
   SHA from https://github.com/google/material-design-icons.
2. Run `./gradlew :frontend:silk-icons-<set>:fetchIcons`, then `generateIcons`, and check the diff for removed or
   renamed icons, which break users.

### Remove old deprecated code

Before 1.0, deprecated code is removed once it's been deprecated for (at least) six months. (Our `@Deprecated` tags
don't record when something was deprecated, so use `git blame` on the annotation to find out.)

### Verify the IntelliJ plugin's project templates

The IntelliJ plugin's New Project and New Module wizards don't follow Kobweb's latest release. Instead, they use the
version in `kobweb-intellij-plugin/module-templates/verified-kobweb-version.txt` in the `varabyte/data` repo. So a new
release only reaches plugin users once that file is bumped.

1. In the plugin repo, check out `main` and
   run `./gradlew :plugin:runLatestIde -Pkobweb.moduleTemplates.fetchVersionFromBranch=<ref>`, where `<ref>` is any Git
   ref, e.g. `dev`.
2. Create a new Kobweb project (and a new module in an existing one), and check that they sync and build.

If this did not work, then you've got to update logic in the `kobweb-intellij-plugin` codebase to fix the issue.
Otherwise, you're done for now. There are [post-release steps](#update-the-verified-release-version) for this feature
as well.

## After the release

These are steps to follow after all Kobweb artifacts are available on Maven Central and the release has been published.

### Update the verified release version

_This section completes the [project templates](#verify-the-intellij-plugins-project-templates) steps from before. It
is assumed at this point you already verified that the Kobweb IntelliJ Plugin works with the version we just released._

Bump [`verified-kobweb-version.txt`](https://github.com/varabyte/data/blob/main/kobweb-intellij-plugin/module-templates/verified-kobweb-version.txt)
to the new version. If something breaks later, downgrade the file; no plugin release is needed.
