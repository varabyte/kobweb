# Updating Your Kobweb Project to Kotlin 2.0.10

[Kotlin 2.0.0 is out](https://kotlinlang.org/docs/whatsnew20.html), featuring support for the new K2 compiler.

Kobweb supports K2 beginning with version 0.19.0 which targets Kotlin 2.0.10[^1]. Unfortunately, due to changes in Kotlin and Compose,
updating projects is not as simple as just upgrading version numbers.

To ease the migration, we have prepared a Gradle task that will attempt to migrate your project in place. After editing
your `gradle/libs.versions.toml` file to use Kobweb 0.19.0:

```toml
[versions]
kobweb = "0.19.0"
```

run  `./gradlew kobwebMigrateToK2` in the root of your project:

```bash
$ ./gradlew kobwebMigrateToK2

> Task :kobwebMigrateToK2
Updated gradle\libs.versions.toml
Updated .gitignore
Updated site\build.gradle.kts
Updated site\.kobweb\conf.yaml

4 file(s) were updated.
```

> [!CAUTION]
> The `kobwebMigrateToK2` task assumes your project follows the standard structure used by the Kobweb templates. For
> projects with non-standard structure, a manual migration may be required. See below for details on both the required
> and recommended migration steps, especially if you run into an error.

## Migration Steps

The migration task performs the following actions (which you can apply manually if needed):

- *(Required)* Updates the Kotlin version to `2.0.10`
- *(Recommended)* Updates the Jetbrains Compose version to `1.6.11`
- *(Required)* Applies
  the [new Compose compiler Gradle plugin](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compiler.html)
  to every Kobweb module
- *(Recommended)* Removes the Jetbrains Compose Gradle plugin from every Kobweb module and replaces Compose dependency
  declarations with new version catalog entries
    - Now that the Compose compiler is applied with its own Gradle plugin, Kobweb modules no longer require using the
      Jetbrains Compose plugin, which focuses on Compose Multiplatform features that are not generally relevant for
      them.
    - As part of migrating away from this plugin, the following dependency entries are added to the
      project's `libs.versions.toml` file:

      ```toml
      [libraries]
      compose-html-core = { module = "org.jetbrains.compose.html:html-core", version.ref = "jetbrains-compose" }
      compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "jetbrains-compose" }
      ```

    - The `build.gradle.kts` files are migrated to use these new dependencies:
      ```diff
      - implementation(compose.runtime)
      - implementation(compose.html.core)
      + implementation(libs.compose.runtime)
      + implementation(libs.compose.html.core)
      ```
- *(Required)* Updates any `.kobweb/conf.yaml` files to refer to the new Kotlin compilation directory, which changed
  from `dist` to `kotlin-webpack`:
  ```diff
  dev:
    contentRoot: "build/processedResources/js/main/public"
  -  script: "build/dist/js/developmentExecutable/<site>.js"
  +  script: "build/kotlin-webpack/js/developmentExecutable/<site>.js"
    api: "build/libs/<site>.jar"
  prod:
  -  script: "build/dist/js/productionExecutable/<site>.js"
  +  script: "build/kotlin-webpack/js/productionExecutable/<site>.js"
  ```
- *(Recommended)* Updates the project's `.gitignore` file to exclude
  the [new `.kotlin` Kotlin data directory](https://kotlinlang.org/docs/whatsnew20.html#new-directory-for-kotlin-data-in-gradle-projects).

## Optional Post-Migration Steps

We encourage taking a look at a few additional items after migrating your project:

- The Kobweb and Kotlin Gradle plugins now
  support the [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html) for faster build
  times. Enable it by adding `org.gradle.configuration-cache=true` to your `gradle.properties` file.

- If you have a Kobweb-only project, you can likely remove the `jetbrains-compose` plugin entry from your version
  catalog entirely:

  ```diff
  - jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }
  ```

  You may also have to remove it from your root `build.gradle.kts` file:

  ```diff
  - alias(libs.plugins.jetbrains.compose) apply false
  ```

- Consider trying the [new Kotlin/JS ES2015 target](https://kotlinlang.org/docs/whatsnew20.html#new-compilation-target):

  ```kotlin
  kotlin {
      configAsKobwebApplication("site")
      js {
          @OptIn(ExperimentalKotlinGradlePluginApi::class)
          compilerOptions.target = "es2015"
      }
  }
  ```
  This is likely to reduce your site's bundle size.

> [!CAUTION]
> The ES2015 target is still new, and using it may cause bugs.

- Check-out the [rest of the changes in Kotlin 2.0](https://kotlinlang.org/docs/whatsnew20.html), especially
  the [ones for Kotlin/JS](https://kotlinlang.org/docs/whatsnew20.html#kotlin-js), which include:
    - Per-file compilation for Kotlin/JS projects
    - Improved collection interoperability
    - Support for type-safe plain JavaScript objects
    - Support for npm package manager

[^1]: Kotlin 2.0.10 includes a critical incremental compilation bug fix needed for live reloading in Kobweb projects. We
appreciate your patience in waiting for this release.
