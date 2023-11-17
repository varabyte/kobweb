# Advanced topics

## Table of contents
- [Advanced topics](#advanced-topics)
  - [Table of contents](#table-of-contents)
    - [Multimodule](#multimodule)
    - [Generating site code at compile time](#generating-site-code-at-compile-time)
    - [Adding Kobweb to an existing project](#adding-kobweb-to-an-existing-project)
    - [Exporting your site in a GitHub workflow](#exporting-your-site-in-a-github-workflow)
    - [Arithmetic for `StyleVariable`s using `calc`](#arithmetic-for-stylevariables-using-calc)
    - [Kobweb Server Plugins](#kobweb-server-plugins)
    - [Create a Kobweb Server Plugin](#create-a-kobweb-server-plugin)
    - [Copy your plugin jar manually](#copy-your-plugin-jar-manually)
    - [Copy your plugin jar automatically](#copy-your-plugin-jar-automatically)
    - [Using your own backend with Kobweb](#using-your-own-backend-with-kobweb)
  - [What about Compose Multiplatform for Web?](#what-about-compose-multiplatform-for-web)

### Multimodule

For simplicity, new projects can choose to put all their pages and widgets inside a single application module.

However, Kobweb is capable of splitting code up across modules. You can define components and/or pages in separate
modules and apply the `com.varabyte.kobweb.library` plugin on them (in contrast to your main module which applies the
`com.varabyte.kobweb.application` plugin.)

In other words, you can lay out your project like this:

```
my-project
├── sitelib
│   ├── build.gradle.kts # apply "com.varabyte.kobweb.library"
│   └── src/jsMain
│       └── kotlin.org.example.myproject.sitelib
│           ├── components
│           └── pages
└── site
    ├── build.gradle.kts # apply "com.varabyte.kobweb.application"
    └── src/jsMain
        └── kotlin.org.example.myproject.site
            ├── components
            └── pages
```

If you'd like to explore a multimodule project example, you can do so by running:

```bash
$ kobweb create examples/chat
```

which demonstrates a chat application with its auth and chat functionality each managed in their own separate modules.

### Generating site code at compile time

Occasionally, you might find yourself wanting code for your site that is better generated programmatically than written
by hand.

The recommended best practice is to create a Gradle task that is associated with its own unique output directory, use
the task to write some code to disk under that directory, and then add that task as a source directory for your project.

> [!NOTE]
> The reason to encourage tasks with their own unique output directory is because this approach is very friendly with
> Gradle caching. You may [read more here](https://docs.gradle.org/current/userguide/build_cache_concepts.html#concepts_overlapping_outputs)
> to learn about this in more detail.
>
> Adding your task as a source directory ensures it will get triggered automatically before the Kobweb tasks responsible
> for processing your project are themselves run.

You want to do this even if you only plan to generate a single file. This is because associating your task with an
output directory is what enables it to be used in place of a source directory.

The structure for this approach generally looks like this:

```kotlin
// e.g. site/build.gradle.kts

val generateCodeTask = tasks.register("generateCode") {
  group = "myproject"
  // You may not need an input file or dir for your task, and if so, you can exclude the next line. If you do need one,
  // I'm assuming it is a data file or files in your resources somewhere.
  val resInputDir = layout.projectDirectory.dir("src/jsMain/resources")
  val genOutputDir = layout.buildDirectory.dir("generated/$group/src/jsMain/kotlin")

  inputs.dir(resInputDir).withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir(genOutputDir)

  doLast {
    genOutputDir.get().file("org/example/pages/SomeCode.kt").asFile.apply {
      parentFile.mkdirs()
      // find and parse file out of resInputDir and write generated code here:
      writeText(/* ... */)

      println("Generated $absolutePath")
    }
  }
}

kotlin {
  configAsKobwebApplication()
  val commonMain by getting { /* ... */ }
  val jsMain by getting {
    kotlin.srcDir(generateCodeTask) // <----- Set your task here
    dependencies { /* ... */ }
  }
}
```

If you want to see this working in action, you
can [check out my blog site's build script here](https://github.com/bitspittle/bitspittle.dev/blob/f7208543046e25337e73b5ede07ff576623962b0/site/build.gradle.kts#L104),
where it is used to generate a listing page for all blog posts.

### Adding Kobweb to an existing project

Currently, Kobweb is still under active development, and due to our limited resources, we are focusing on improving the
path to creating a new project from scratch. However, some users have shown interest in Kobweb but already have an
existing project and aren't sure how to add Kobweb into it.

As long as you understand that this path isn't officially supported yet, we'll provide steps below to take which may
help people accomplish this manually for now. Honestly, the hardest part is creating a correct `.kobweb/conf.yaml`,
which the following steps help you work around:

1. Be sure to check the Kobweb compatibility matrix [(see: COMPATIBILITY.md)](https://github.com/varabyte/kobweb/blob/main/COMPATIBILITY.md)
   to make sure you can match the versions it expects.
2. Create a dummy app project somewhere. Pay attention to the questions it asks you, as you may want to choose a
   package name that matches your project.
   ```bash
   # In some tmp directory somewhere
   kobweb create app
   # or `kobweb create app/empty`, if you are already
   # experienced with Kobweb and know what you're doing
   ```
3. When finished, copy the `site` subfolder out into your own project. (Once done, you can delete the dummy project, as
   it has served its usefulness.)
   ```bash
   cp -r app/site /path/to/your/project
   # delete app
   ```
4. In your own project's root `settings.gradle.kts` file, include the new module *and* add our custom artifact
   repository link so your project can find the Kobweb Gradle plugins.
   ```kotlin
   // settings.gradle.kts
   pluginManagement {
     repositories {
       // ... other repositories you already declared ...
       maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
     }
   }
   // ... other includes you already declared
   include(":site")
   ```
5. In your project's root `build.gradle.kts` file, add our custom artifact repository there as well (so your project can
   find Kobweb libraries)
   ```kotlin
   // build.gradle.kts
   subprojects {
     repositories {
       // ... other repositories you already declared ...
       maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
     }
   }

   // If you prefer, you can just declare this directly inside the
   // repositories block in site's `build.gradle.kts` file, but I
   // like declaring my maven repositories globally.
   ```
6. Kobweb uses version catalogs for its dependencies. Add or update your version catalog under
   `gradle/libs.versions.toml`
   ```toml
   [versions]
   jetbrains-compose = "..." # replace with actual version, see COMPATIBILITY.md!
   kobweb = "..." # replace with actual version
   kotlin = "..." # replace with actual version

   [libraries]
   kobweb-api = { module = "com.varabyte.kobweb:kobweb-api", version.ref = "kobweb" }
   kobweb-core = { module = "com.varabyte.kobweb:kobweb-core ", version.ref = "kobweb" }
   kobweb-silk = { module = "com.varabyte.kobweb:kobweb-silk", version.ref = "kobweb" }
   kobwebx-markdown = { module = "com.varabyte.kobwebx:kobwebx-markdown", version.ref = "kobweb" }
   silk-icons-fa = { module = "com.varabyte.kobwebx:silk-icons-fa", version.ref = "kobweb" }

   [plugins]
   jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }
   kobweb-application = { id = "com.varabyte.kobweb.application", version.ref = "kobweb" }
   kobwebx-markdown = { id = "com.varabyte.kobwebx.markdown", version.ref = "kobweb" }
   kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
   ```

If everything is working as expected, you should be able to run Kobweb within your project now:

```bash
# In /path/to/your/project
cd site
kobweb run
```

If you're still having issues, you may want to [connect with us▼](#connecting-with-us)
for support (but understand that getting Kobweb added to complex existing projects may not be something we can currently
prioritize).

### Exporting your site in a GitHub workflow

While you can always export your site manually on your machine, you may want to automate this process. A common
solution for this is a [GitHub workflow](https://docs.github.com/en/actions/using-workflows).

For your convenience, we include a sample workflow below that exports your site and then uploads the results (which can
be downloaded from a link shown in the workflow summary page):

```yaml
# .github/workflows/export-site.yml

name: Export Kobweb site

on:
  workflow_dispatch:

jobs:
  export_and_upload:
    runs-on: ubuntu-latest
    defaults:
      run:
        shell: bash

    env:
      KOBWEB_CLI_VERSION: 0.9.13

    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: temurin
          java-version: 11

      # When projects are created on Windows, the executable bit is sometimes lost. So set it back just in case.
      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      - name: Setup Gradle
        uses: gradle/gradle-build-action@v2

      - name: Query Browser Cache ID
        id: browser-cache-id
        run: echo "value=$(./gradlew -q :site:kobwebBrowserCacheId)" >> $GITHUB_OUTPUT

      - name: Cache Browser Dependencies
        uses: actions/cache@v3
        id: playwright-cache
        with:
          path: ~/.cache/ms-playwright
          key: ${{ runner.os }}-playwright-${{ steps.browser-cache-id.outputs.value }}

      - name: Fetch kobweb
        uses: robinraju/release-downloader@v1.7
        with:
          repository: "varabyte/kobweb-cli"
          tag: "v${{ env.KOBWEB_CLI_VERSION }}"
          fileName: "kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip"
          tarBall: false
          zipBall: false

      - name: Unzip kobweb
        run: unzip kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip

      - name: Run export
        run: |
          cd site
          ../kobweb-${{ env.KOBWEB_CLI_VERSION }}/bin/kobweb export --notty --layout static

      - name: Upload site
        uses: actions/upload-artifact@v3
        with:
          name: site
          path: site/.kobweb/site/
          if-no-files-found: error
          retention-days: 1
```

You can copy this workflow (or parts of it) into your own GitHub project and then modify it to your needs.

Some notes...

* ***workflow_dispatch***: This means that you can manually trigger this workflow from the GitHub UI, which I
  suggested here to prevent running an expensive export operation more than you need to. Of course, you can also
  configure your workflow to run on a schedule, or on push to a branch, etc.
* ***Setup Gradle***: This action is optional but I recommend it because it configures a bunch of caching for you.
* ***Caching the browser***: `kobweb export` needs to download a browser the first time it is run. This workflow sets up
  a cache that saves it across runs. The cache is tagged with a unique ID so that future Kobweb releases, which may
  change the version of browser downloaded, will use a new cache bucket (allowing GitHub to eventually clean up the old
  one).
* ***Upload site***: This action uploads the exported site as an artifact. You can then download the artifact from the
  workflow summary page. Your own workflow will likely delete this action and do something else here, like upload to a
  web server (or some location accessible by your webserver) or copy files over into a `gh_pages` repository. I've
  included this here (and set the retention days very low) just so you can verify that the workflow is working for your
  project.

For a simple site, the above workflow should take about 2 minutes to run.

### Arithmetic for `StyleVariable`s using `calc`

`StyleVariable`s work in a subtle way that is usually fine until it isn't -- which is often when you try to interact
with their values instead of just passing them around.

For context, you can use a style variable interchangeably with the value it represents. For example, code like this
works because `MyOpacityVar.value()` returns something with type `Number`:

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// later...
Modifier.opacity(MyOpacityVar.value())
```

This generates the following CSS:
```css
opacity: var(--my-opacity);
```

How does something of type `Number` generate output like `var(--my-opacity)`?

This is accomplished through the use of Kotlin/JS's `unsafeCast`, where you can tell the compiler to treat a value as a
different type than it actually is. In this case, `MyOpacityVar.value()` returns some object which the Kotlin compiler
*treats* like a `Number` for compilation purposes, but it is really some class instance whose `toString()` evaluates to
`var(--my-opacity)`.

Therefore, `Modifier.opacity(MyOpacityVar.value())` works seemingly like magic! However, if you try to do some
arithmetic, like `MyOpacityVar.value().toDouble() * 0.5`, the compiler might be happy, but things will break silently at
runtime, when the JS engine is asked to do math with something that's not really a number.

In CSS, doing math with variables is accomplished by using `calc` blocks , so Kobweb offers its own `calc` method to
mirror this. When dealing with raw numerical values, you must wrap them in `num` so we can escape the raw type system
which was causing runtime confusion above:

```kotlin
calc { num(MyOpacityVar.value()) * num(0.5) }
// Output: "calc(var(--my-opacity, 1) * 0.5)"
```

At this point, you can write code like this:

```kotlin
Modifier.opacity(calc { num(MyOpacityVar.value()) * num(0.5) })
```

It's a little hard to remember to wrap raw values in `num`, but you will get compile errors if you do it wrong.

Working with variables representing size values inside a calc block doesn't require wrapping methods and feels much more
natural:

```kotlin
val MyFontSizeVar by StyleVariable<CSSLengthValue>()
calc { MyFontSizeVar.value() + 1.cssRem }
// Output: "calc(var(--my-font-size) + 1rem)"
```

### Kobweb Server Plugins

Many users who create a full stack application generally expect to completely own both the client- and server-side code.

However being an opinionated framework, Kobweb provides a custom Ktor server in order to deliver some of its features.
For example, it implements logic for handling [server API routes▲](#define-api-routes) as well as some live reloading
functionality.

It would not be trivial to refactor this behavior into some library that users could import into their own backend
server. As a compromise, some server configuration is exposed by the `.kobweb/conf.yaml` file, and this has been the
main way users could affect the server's behavior.

That said, there will always be some use-cases that Kobweb won't anticipate. So as an escape hatch, Kobweb allows users
who know what they're doing to write their own plugins to extend the server.

> [!NOTE]
> The Kobweb Server plugin feature is still fairly new. If you use it, please consider
> [filing issues](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=)
> for any missing features and [connecting with us▼](#connecting-with-us) to share any feedback you have about your
> experience.

Creating a Kobweb server plugin is relatively straightforward. You'll need to:

* Create a new module in your project that produces a JAR file which bundles an implementation of
  the `KobwebServerPlugin` interface.
* Move a copy of that jar under your project's `.kobweb/server/plugins` directory.

### Create a Kobweb Server Plugin

The following instructions are based on a Kobweb multimodule setup, like the one created by `kobweb create app`.

* Create a new module in your project.
    * For example, name it "demo-server-plugin".
    * Be sure to update your `settings.gradle.kts` file to include the new project.
* Add a new entry for the `kobweb-server-project` library in `.gradle/libs.versions.toml`:
  ```toml
  [libraries]
  kobweb-server-plugin = { module = "com.varabyte.kobweb:kobweb-server-plugin", version.ref = "kobweb" }
  ```
* **For all remaining steps, create all files / directories under your new module's directory (e.g. `demo-server-plugin/`).**
* Create `build.gradle.kts`:
  ```kotlin
    plugins {
      kotlin("jvm")
    }
    group = "org.example.app" // update to your own project's group
    version = "1.0-SNAPSHOT"

    tasks.jar {
      // Remove the version number
      archiveFileName.set("${project.name}.jar")
    }

    dependencies {
      compileOnly(libs.kobweb.server.plugin)
    }
  ```
    * We omit the version number to prevent the accumulation of multiple versioned copies of the same plugin ended up in
      the Kobweb server. Instead, each new version should replace the previous one.
* Create `src/main/kotlin/DemoKobwebServerPlugin.kt`:
  ```kotlin
  import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
  import io.ktor.server.application.Application
  import io.ktor.server.application.log

  class DemoKobwebServerPlugin : KobwebServerPlugin {
    override fun configure(application: Application) {
      application.log.info("REPLACE ME WITH REAL CONFIGURATION")
    }
  }
  ```
    * As the Kobweb server is written in Ktor, you should familiarize yourself
      with [Ktor's documentation](https://ktor.io/docs/plugins.html).
* Create `src/main/resources/META-INF/services/com.varabyte.kobweb.server.plugin.KobwebServerPlugin`:
  ```text
  org.example.app.DemoKobwebServerPlugin
  ```
    * This helps the JDK discover service implementations bundled within a JAR. You can
      read [this helpful article](https://www.baeldung.com/java-spi) to learn more about this useful Java feature.

### Copy your plugin jar manually

After building your JAR (`./gradlew :demo-server-plugin:jar`), manually copy it from `build/libs/` to your Kobweb
project's `.kobweb/server/plugins` directory.

Upon the next Kobweb server run (e.g., via `kobweb run`), if you check the logs, you should see something like this:

```text
[main] INFO  ktor.application - Auto-reload is disabled because the development mode is off.
[main] INFO  ktor.application - REPLACE ME WITH REAL CONFIGURATION
[main] INFO  ktor.application - Application started in 0.112 seconds.
[main] INFO  ktor.application - Responding at http://0.0.0.0:8080
```

### Copy your plugin jar automatically

For convenience, the Kobweb Gradle Application plugin provides a way to notify it about your JAR task, and it will build
and copy it over for you automatically.

In your Kobweb project's build script, include the following `notify...` line:

```kotlin
// site/build.gradle.kts

kobweb { /* ... */ }

notifyKobwebAboutServerPluginTask(project(":demo-server-plugin").tasks.named("jar", Jar::class))

kotlin { /* ... */ }
```

Once this is set up, you can modify your Kobweb server plugin, quit the server if one is running, and then rerun
`kobweb run` to have it pick up your changes automatically.

### Using your own backend with Kobweb

You may already have an existing and complex backend, perhaps written with Ktor or Spring Boot, and, if so, are
wondering if you can integrate Kobweb with it.

The recommended solution for now is to export your site using a static layout
([read more about static layout sites here▲](#static-layout-vs-full-stack-sites)) and then add code to your backend to
serve the files yourself, as it is fairly trivial.

When you export a site statically, it will generate all files into your `.kobweb/site` folder. Then, if using Ktor, for
example, serving these files is a one-liner:

```kotlin
routing {
    staticFiles("/", File(".kobweb/site"))
}
```

If you need to access HTTP endpoints exposed by your backend, you can use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)
directly, or you can use the convenience `http` property that Kobweb adds to the `window` object and which exposes
all the HTTP methods (`get`, `post`, `put`, etc.):

```kotlin
@Page
@Composable
fun CustomBackendDemoPage() {
  LaunchedEffect(Unit) {
    val endpointResponse = window.http.get("/my/endpoint?id=123").decodeToString()
    /* ... */
  }
}
```

Unfortunately, using your own backend does mean you're opting out of using Kobweb's full stack solution, which means you
won't have access to Kobweb's API routes, API streams, or live reloading support. This is a situation we'd like to
improve someday ([link to tracking issue](https://github.com/varabyte/kobweb/issues/22)), but we don't have enough
resources to be able to prioritize resolving this for a 1.0 release.

<!-- Some sites link to this section before I changed its name, so adding a span here so they can still find it. -->
## <span id="what-about-multiplatform-widgets"><span id="what-about-compose-for-web-canvas">What about Compose Multiplatform for Web?</span></span>

Jetbrains is working on a project called "Compose Multiplatform", which will allow developers to use the same Compose
API across Android, iOS, Desktop, and the Web. And it may seem like the Kobweb + Silk approach will be obsoleted by it.

It's first worth understanding the core difference between the two approaches. With Multiplatform Compose, the framework
owns its own rendering pipeline, drawing to a buffer, while Compose HTML modifies an HTML / CSS DOM tree and leaves it
up to the browser to do the final rendering.

This has major implications on how similar the two APIs can get. For example, in Desktop / Android, the order you apply
modifiers matters, while in HTML, this action simply sets html style properties under the hood, where order does not
matter.

Ditching HTML / CSS entirely at first can seem like a total win, but this approach has several limits:

* robots would lose the ability to crawl and index your site, hurting SEO.
* your initial render may take longer.
* your site will need to allocate a large canvas buffer, which could be *very* expensive on high res, wide-screen
  desktops.
* your UI will be opaque to the powerful suite of devtools that come bundled with browsers.
* you won't have the ability to style unvisited vs visited links differently (this information is hidden from you by
  the browser for security reasons and can only be set through HTML / CSS).
* you won't have the ability to turn elements on / off when printing the page.
* accessibility tools for browsers might not work.

It would also prevent a developer from making use of the rich ecosystem of Javascript libraries out there.

For now, I am making a bet that there will always be value in embracing the web, providing a framework that sticks to
HTML / CSS but offers a growing suite of UI widgets that hopefully makes it relatively rare for the developer to need to
worry about it.

For example, [flexbox](https://css-tricks.com/snippets/css/a-guide-to-flexbox/) is a very powerful CSS concept, but
you'll find it's much easier to compose `Row`s and `Column`s together than trying to remember if you should be
justifying your items or aligning your content, even if `Row`s and `Column`s are just creating the correct HTML / CSS
for you behind the scenes.

Ultimately, I believe there is room for both approaches. If you want to make an app experience that feels the same on
Android, iOS, Desktop, and Web, then "Multiplatform Compose" could be great for you. However, if you just want to make a
traditional website but want to use Kotlin instead of TypeScript, Kobweb can provide an excellent development experience
for that case.