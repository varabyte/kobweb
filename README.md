[![version: 0.20.4](https://img.shields.io/badge/kobweb-0.20.4-blue)](COMPATIBILITY.md)
[![version: 0.9.18](https://img.shields.io/badge/kobweb_cli-0.9.18-blue)](https://github.com/varabyte/kobweb-cli)
<br>
[![kotlin: 2.1.10](https://img.shields.io/badge/kotlin-2.1.10-blue?logo=kotlin)](COMPATIBILITY.md)
[![compose: 1.7.3](https://img.shields.io/badge/compose-1.7.3-blue?logo=jetpackcompose)](COMPATIBILITY.md)
[![ktor: 3.1.0](https://img.shields.io/badge/ktor-3.1.0-blue)](https://ktor.io/)
<br>
<a href="https://kobweb.varabyte.com/docs">
![User Guide docs](https://img.shields.io/badge/User_Guide-royalblue?logo=readthedocs)
</a>
<a href="https://varabyte.github.io/kobweb/">
![API Reference docs](https://img.shields.io/badge/API_Reference-grey?logo=readthedocs)
</a>
<br>
[![Varabyte Discord](https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/5NZ2GKV5Cs)
[![Kotlin Slack](https://img.shields.io/badge/%23Kobweb-4A154B?logo=slack&logoColor=white)](https://kotlinlang.slack.com/archives/C04RTD72RQ8)
[![Bluesky](https://img.shields.io/badge/Bluesky-0285FF?logo=bluesky&logoColor=fff)](https://bsky.app/profile/bitspittle.bsky.social)

# K🕸️bweb

Kobweb is an opinionated Kotlin framework for creating websites and web apps, built on top of
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) and inspired by [Next.js](https://nextjs.org)
and [Chakra UI](https://chakra-ui.com).

```kotlin
@Page
@Composable
fun HomePage() {
  Column(
    Modifier.fillMaxWidth().whiteSpace(WhiteSpace.PreWrap).textAlign(TextAlign.Center),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    var colorMode by ColorMode.currentState
    Button(
      onClick = { colorMode = colorMode.opposite },
      Modifier.borderRadius(50.percent).padding(0.px).align(Alignment.End)
    ) {
      // Includes support for Font Awesome icons
      if (colorMode.isLight) FaMoon() else FaSun()
    }
    H1 {
      Text("Welcome to Kobweb!")
    }
    Span {
      Text("Create rich, dynamic web apps with ease, leveraging ")
      Link("https://kotlinlang.org/", "Kotlin")
      Text(" and ")
      Link(
        "https://github.com/JetBrains/compose-multiplatform/#compose-html",
        "Compose HTML"
      )
    }
  }
}
```

<p align="center">
<img src="https://github.com/varabyte/media/raw/main/kobweb/screencasts/kobweb-welcome.gif" />
</p>

---

While Kobweb is still pre-1.0, it has been usable for a while now. It provides escape hatches to lower-level APIs, so
you can accomplish anything even if Kobweb doesn't support it yet. Please consider starring the project to indicate
interest, so we know we're creating something the community wants.
[How ready is it?▼](#can-we-kobweb-yet)

Our goal is to provide:

* an intuitive structure for organizing your Kotlin website or web app
* automatic handling of routing between pages
* a collection of useful _batteries included_ widgets built on top of Compose HTML
* an environment built from the ground up around live reloading
* static site exports for improved SEO
* support for responsive (i.e. mobile and desktop) design
* out-of-the-box Markdown support
* a way to easily define server API routes and persistent API streams
* support for creating and using web workers
* a growing collection of general purpose utilities added on top of Compose HTML and Kotlin/JS ([learn more▼](#general-purpose-improvements-on-top-of-compose-html-and-kotlinjs))
* an open source foundation that the community can extend
* and much, much more!

📚 You can find a detailed guide at https://kobweb.varabyte.com/docs

You can
also [check out my talk at Droidcon SF 24](https://www.droidcon.com/2024/07/17/kobwebcreating-websites-in-kotlin-leveraging-compose-html/)
for a high level overview of Kobweb. The talk showcases what Kobweb can do, introduces Compose HTML (which it builds
on top of), and covers a wide range of frontend and backend functionality. It is light on code but heavy on
understanding the structure and capabilities of the framework.

---

<span id="demo"></span>
Here's a demo where we create a Compose HTML project from scratch with Markdown support and live reloading, in under
10 seconds:

https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4

## Getting started

The first step is to get the Kobweb binary. You can install it, download it, and/or build it, so we'll include
instructions for all these approaches.

### Install the Kobweb binary

*Major thanks to [aalmiray](https://github.com/aalmiray) and [helpermethod](https://github.com/helpermethod) to helping
me get these installation options working. Check out [JReleaser](https://github.com/jreleaser/jreleaser) if you ever
need to do this in your own project!*

#### [Homebrew](https://brew.sh/)

*OS: Mac and Linux*

```bash
$ brew install varabyte/tap/kobweb
```

#### [Scoop](https://scoop.sh/)

*OS: Windows*

```shell
# Note: Adding buckets only has to be done once.

# Feel free to skip java if you already have it
> scoop bucket add java
> scoop install java/openjdk

# Install kobweb
> scoop bucket add varabyte https://github.com/varabyte/scoop-varabyte.git
> scoop install varabyte/kobweb
```

#### [SDKMAN!](https://sdkman.io/)

*OS: Windows, Mac, and \*nix*

```shell
$ sdk install kobweb
```

#### Arch Linux

*Thanks a ton to [aksh1618](https://github.com/aksh1618) for adding support for this target!*

With an [AUR helper](https://wiki.archlinux.org/title/AUR_helpers), e.g.:

```shell
$ yay -S kobweb
$ paru -S kobweb
$ trizen -S kobweb
# etc.
```

Without an AUR helper:

```shell
$ git clone https://aur.archlinux.org/kobweb.git
$ cd kobweb
$ makepkg -si
```

#### Don't see your favorite package manager?

Please see: https://github.com/varabyte/kobweb-cli/issues/11 and consider leaving a comment!

### Download the Kobweb binary

Our binary artifact is hosted on GitHub. To download the latest, you can either
[grab the zip or tar file from GitHub](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.18) or you can fetch
it from your terminal:

```bash
$ cd /path/to/applications

# You can either pull down the zip file

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.18/kobweb-0.9.18.zip
$ unzip kobweb-0.9.18.zip

# ... or the tar file

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.18/kobweb-0.9.18.tar
$ tar -xvf kobweb-0.9.18.tar
```

and I recommend adding it to your path, either directly:

```bash
$ PATH=$PATH:/path/to/applications/kobweb-0.9.18/bin
$ kobweb version # to check it's working
```

or via symbolic link:

```bash
$ cd /path/to/bin # some folder you've created that's in your PATH
$ ln -s /path/to/applications/kobweb-0.9.18/bin/kobweb kobweb
```

### Build the Kobweb binary

Although we host Kobweb artifacts on GitHub, it's easy enough to build your own.

Building Kobweb requires JDK11 or newer. We'll first discuss how to add it.

#### Download a JDK

If you want full control over your JDK install, manually downloading is a good option.

* [Download a JDK for your OS](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Unzip it somewhere
* Update your `JAVA_HOME` variable to point at it.

```bash
JAVA_HOME=/path/to/jdks/corretto-11.0.12
# ... or whatever version or path you chose
```

#### Install a JDK with the IntelliJ IDE

For a more automated approach, you can request IntelliJ install a JDK for you.

Follow their instructions here: https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk

#### Building the Kobweb CLI

The Kobweb CLI is actually maintained in a separate GitHub repo. Once you have the JDK set up, it should be easy to
clone and build it:

```bash
$ cd /path/to/src/root # some folder you've created for storing src code
$ git clone https://github.com/varabyte/kobweb-cli
$ cd kobweb-cli
$ ./gradlew :kobweb:installDist
```

Finally, update your PATH:

```bash
$ PATH=$PATH:/path/to/src/root/kobweb-cli/kobweb/build/install/kobweb/bin
$ kobweb version # to check it's working
```

### Update the Kobweb binary

If you previously installed Kobweb and are aware that a new version is available, the way you update it depends on how
you installed it.

| Method                    | Instructions                                                                                                                         |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Homebrew                  | `brew update`<br/>`brew upgrade kobweb`                                                                                              |
| Scoop                     | `scoop update kobweb`                                                                                                                |
| SDKMAN!                   | `sdk upgrade kobweb`                                                                                                                 |
| Arch Linux                | Rerunning [install steps](#arch-linux) should work. If using an AUR helper, you may need to review its manual.                       |
| Downloaded from<br>Github | Visit the [latest release](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.18). You can find both a zip and tar file there. |

### Create your first Kobweb site

```bash
$ cd /path/to/projects/
$ kobweb create app
```

You'll be asked a few questions required for setting up your project.

You don't need to create a root folder for your project ahead of time - the setup process will prompt you for one to
create. For the remaining parts of this section, let's say you choose the folder "my-project" when asked.

When finished, you'll have a basic project with two pages - a home page and an about page (with the about page written
in markdown) - and some components (which are collections of reusable, composable pieces). Your own directory structure
should look something like this:

```
my-project
└── site/src/jsMain
    ├── kotlin.org.example.myproject
    │   ├── components
    │   │   ├── layouts
    │   │   │   ├── MarkdownLayout.kt
    │   │   │   └── PageLayout.kt
    │   │   ├── sections
    │   │   │   ├── Footer.kt
    │   │   │   └── NavHeader.kt
    │   │   └── widgets
    │   │       └── IconButton.kt
    │   ├── pages
    │   │   └── Index.kt
    │   └── AppEntry.kt
    └── resources/markdown
        └── About.md
```

Note that there's no index.html or routing logic anywhere! We generate that for you automatically when you run Kobweb.
This brings us to the next section...

#### Run your Kobweb site

```bash
$ cd your-project/site
$ kobweb run
```

This command spins up a web server at http://localhost:8080. If you want to configure the port, you can do so by editing
your project's `.kobweb/conf.yaml` file.

You can open your project in IntelliJ and start editing it. While Kobweb is running, it will detect changes, recompile,
and deploy updates to your site automatically.

### Using IntelliJ

If you don't want to keep a separate terminal window open beside your IDE window, you may prefer alternate solutions.

#### Terminal tool window

You can use the [IntelliJ terminal tool window](https://www.jetbrains.com/help/idea/terminal-emulator.html) to run
`kobweb` within it. If you run into a compile error, the stack trace lines will get decorated with
links, making it easy to navigate to the relevant source.

#### Gradle commands

`kobweb` itself delegates to Gradle, but nothing is stopping you from calling the commands yourself. You can create
Gradle run configurations for each of the Kobweb commands.

> [!TIP]
> When you run a Kobweb CLI command that delegates to Gradle, it will log the Gradle command to the console. This is
> how you can discover the Gradle commands discussed in this section.

* To start a Kobweb server, use the `kobwebStart -t` command.
  * The `-t` argument (or, `--continuous`) tells Gradle to watch for file changes, which gives you live loading behavior.
* To stop a running Kobweb server, use the `kobwebStop` command.
* To export a site, use<br>
  `kobwebExport -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=FULLSTACK -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=FULLSTACK`
  * If you want to export a static layout instead, change the last argument to<br>`-PkobwebExportLayout=STATIC`.
* To run an exported site, use<br>
  `kobwebStart -PkobwebEnv=PROD -PkobwebRunLayout=FULLSTACK`
  * If your site was exported using a static layout, change the last argument to<br>`-PkobwebRunLayout=STATIC`.

You can read all about [IntelliJ's Gradle integration here](https://www.jetbrains.com/help/idea/gradle.html). Or to just jump straight into how to create run
configurations for any of the commands discussed above, read [these instructions](https://www.jetbrains.com/help/idea/run-debug-gradle.html).

### Running examples

Kobweb will provide a growing collection of samples for you to learn from. To see what's available, run:

```bash
$ kobweb list

You can create the following Kobweb projects by typing `kobweb create ...`

• app: A template for a minimal site that demonstrates the basic features of Kobweb
• examples/jb/counter: A very minimal site with just a counter (based on the Jetbrains tutorial)
• examples/todo: An example TODO app, showcasing client / server interactions
```

For example, `kobweb create examples/todo` will instantiate a TODO app locally.

### Gradle configuration

#### Declaring repositories to fetch Kobweb artifacts

Kobweb publishes its libraries to Maven Central and its plugins to the Gradle Plugin Portal. Therefore, Kobweb
recommends setting up your project's `settings.gradle.kts` like so:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
```

> [!TIP]
> All Kobweb templates embrace this pattern, so if you start your own project by building on top of any of them, then
> this will already be done for you.

Dependencies on Maven Central and the Gradle Plugin Portal are so standard, it's hard to imagine a project that isn't
already using them, so in most cases, you won't have to do anything.

#### Testing snapshots

Occasionally, especially if you file an issue for a bug fix or a feature request, our team may ask you if you're willing
to try using a snapshot build (a dev build, essentially).

Snapshots are, by design, not supported in either Maven Central nor the Gradle Plugin Portal. Therefore, we host all
plugin and library artifacts in a separate official snapshot repository (at
`https://s01.oss.sonatype.org/content/repositories/snapshots/`). As a result, you will have to declare this repository
for both plugin *and* library blocks.

An easy way to enable this is by adding the following block of code into your `settings.gradle.kts` file:

```diff
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

+ // The following block registers dependencies to enable Kobweb snapshot support. It is safe to delete or comment out
+ // this block if you never plan to use them.
+ gradle.settingsEvaluated {
+     fun RepositoryHandler.kobwebSnapshots() {
+         maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
+             content { includeGroupByRegex("com\\.varabyte\\.kobweb.*") }
+             mavenContent { snapshotsOnly() }
+         }
+     }
+
+     pluginManagement.repositories { kobwebSnapshots() }
+     dependencyResolutionManagement.repositories { kobwebSnapshots() }
+ }
```

> [!CAUTION]
> The above code, adding repositories inside the `settingsEvaluated` block, is actually not Gradle idiomatic -- that
> approach would be to create a settings plugin or just copy/paste the repository declaration in all relevant places --
> but at the moment we are suggesting this approach for its simplicity:
>
> 1. If we could have declared a top level method in the settings file that both blocks could have called, that would
> have been a nice option to recommend. However, the `pluginManagement` block is "magic" and you cannot share code with
> it. This approach lets us at least mimic that kind of solution.
> 2. Keeping the snapshot declaration logic separated in its own block makes it easy to remove it later if you decide
> you don't want to keep it anymore.
> 3. This approach is isolated inside a single file, while a settings plugin would be a lot of work that would require
touching several files, which is probably not worth it just for enabling snapshots.

#### Gradle version catalogs

The project templates created by Kobweb all embrace Gradle version catalogs.

If you're not aware of it, it's a file that exists at `gradle/libs.versions.toml`. If you find yourself wanting to tweak
or add new versions to projects you originally created via `kobweb create`, that's where you'll find them.

For example, here's the
[libs.versions.toml](https://github.com/varabyte/kobweb-site/blob/main/gradle/libs.versions.toml) we use for our own
landing site.

To read more about the feature, please check out the
[official docs](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml).

### Upgrading Kobweb in your project

The latest available version of Kobweb is declared at the top of this README. If a new version has come out, you can
update your own project by editing `gradle/libs.version.toml` and updating the `kobweb` version there.

> [!IMPORTANT]
> You should double-check [COMPATIBILITY.md](COMPATIBILITY.md) to see if you also need to update your `kotlin` and
> `jetbrains-compose` versions as well.

> [!CAUTION]
> It can be confusing, but Kobweb has two versions -- the version for the library itself (the one that is applicable in
> this situation), and the one for the command line tool.

<!--
We collected all the links from the old README so if a user tries to visit them, they'll end up here and NOT at the top
of the README.
-->

<span id="stevdza-san"></span>
<span id="tutorial-videos"></span>
<span id="beginner-topics"></span>
<span id="create-a-page"></span>
<span id="route-override"></span>
<span id="package"></span>
<span id="packagemapping"></span>
<span id="page-context"></span>
<span id="query-parameters"></span>
<span id="dynamic-routes"></span>
<span id="packagemapping-1"></span>
<span id="page"></span>
<span id="querying-dynamic-route-values"></span>
<span id="catch-all-dynamic-routes"></span>
<span id="optional-catch-all-routes"></span>
<span id="static-and-dynamic-siblings"></span>
<span id="public-resources"></span>
<span id="html-styling"></span>
<span id="inline-vs-stylesheet"></span>
<span id="modifier"></span>
<span id="attrsmodifier-and-stylemodifier"></span>
<span id="silk"></span>
<span id="initsilk-methods"></span>
<span id="cssstyle"></span>
<span id="cssstylebase"></span>
<span id="cssstyle-name"></span>
<span id="additional-selectors"></span>
<span id="breakpoints"></span>
<span id="color-mode-aware"></span>
<span id="initial-color-mode"></span>
<span id="persisting-color-mode-preference"></span>
<span id="extending-css-styles"></span>
<span id="component-styles"></span>
<span id="component-variants"></span>
<span id="addvariantbase"></span>
<span id="structuring-code-around-component-styles"></span>
<span id="animations"></span>
<span id="elementrefscope-and-raw-html-elements"></span>
<span id="ref"></span>
<span id="disposableref"></span>
<span id="refscope"></span>
<span id="compose-html-refs"></span>
<span id="style-variables"></span>
<span id="in-many-cases-dont-use-css-variables"></span>
<span id="font-awesome"></span>
<span id="material-design-icons"></span>
<span id="components-layouts-sections-and-widgets"></span>
<span id="layouts-sections-and-widgets"></span>
<span id="markdown"></span>
<span id="front-matter"></span>
<span id="root"></span>
<span id="route-override-1"></span>
<span id="kobweb-call"></span>
<span id="block-syntax"></span>
<span id="inline-syntax"></span>
<span id="imports"></span>
<span id="global-imports"></span>
<span id="local-imports"></span>
<span id="callouts"></span>
<span id="callout-variants"></span>
<span id="custom-callouts"></span>
<span id="iterating-over-all-markdown-files"></span>
<span id="learning-css-through-kobweb"></span>
<span id="ways-kobweb-helps-with-css"></span>
<span id="a-concrete-example"></span>
<span id="css-2-kobweb"></span>
<span id="still-stuck"></span>
<span id="exporting-your-site"></span>
<span id="a-concrete-export-example"></span>
<span id="exporting-requires-a-browser"></span>
<span id="static-layout-vs-full-stack-sites"></span>
<span id="static-layout-sites"></span>
<span id="full-stack-sites"></span>
<span id="choosing-the-right-site-layout-for-your-project"></span>
<span id="exporting-and-running"></span>
<span id="pagecontextisexporting"></span>
<span id="dynamic-routes-and-exporting"></span>
<span id="deploying"></span>
<span id="intermediate-topics"></span>
<span id="specifying-your-application-root"></span>
<span id="updating-default-html-styles-with-silk"></span>
<span id="setting-application-globals"></span>
<span id="globally-replacing-silk-widget-styles"></span>
<span id="server"></span>
<span id="communicating-with-the-server"></span>
<span id="declare-a-full-stack-project"></span>
<span id="define-api-routes"></span>
<span id="responding-to-an-api-request"></span>
<span id="intercepting-api-routes"></span>
<span id="dynamic-api-routes"></span>
<span id="initapi-methods-and-initializing-services"></span>
<span id="api-stream"></span>
<span id="define-api-streams"></span>
<span id="example-api-stream"></span>
<span id="api-stream-conveniences"></span>
<span id="api-routes-vs-api-streams"></span>
<span id="passing-state-across-pages"></span>
<span id="type-safe-storage-values"></span>
<span id="splitting-kobweb-code-across-multiple-modules"></span>
<span id="worker"></span>
<span id="creating-a-kobweb-worker"></span>
<span id="background-what-are-web-workers"></span>
<span id="web-workers-wrapped-in-kobweb"></span>
<span id="example-worker-module-build-file"></span>
<span id="worker-factory"></span>
<span id="worker"></span>
<span id="workerfactory-examples"></span>
<span id="echoworkerfactory"></span>
<span id="countdownworkerfactory"></span>
<span id="findprimesworkerfactory"></span>
<span id="transferables"></span>
<span id="final-notes-about-worker-factories"></span>
<span id="single-worker-factory"></span>
<span id="worker-factory-name-constraint"></span>
<span id="when-to-use-kobweb-workers"></span>
<span id="advanced-topics"></span>
<span id="setting-your-sites-base-path"></span>
<span id="redirects"></span>
<span id="css-layers"></span>
<span id="default-layers"></span>
<span id="registering-layers"></span>
<span id="csslayer-annotation"></span>
<span id="layer-blocks"></span>
<span id="importing-third-party-styles-into-layers"></span>
<span id="generating-site-code-at-compile-time"></span>
<span id="generating-resources"></span>
<span id="adding-kobweb-to-an-existing-project"></span>
<span id="exporting-your-site-in-a-github-workflow"></span>
<span id="arithmetic-for-stylevariables-using-calc"></span>
<span id="kobweb-server-plugins"></span>
<span id="create-a-kobweb-server-plugin"></span>
<span id="register-your-server-plugin-jar"></span>
<span id="hooking-into-ktor-routing-events"></span>
<span id="changing-a-kobweb-server-plugin-requires-a-server-restart"></span>
<span id="using-your-own-backend-with-kobweb"></span>
<span id="cssnumeric"></span>
<span id="cssnumericvalue-type-aliases"></span>
<span id="background"></span>
<span id="csssizevalue"></span>
<span id="cssnumericvalue"></span>
<span id="prefer-using-kobwebs-cssnumericvalue-type-aliases"></span>
<span id="miscellaneous-topics"></span>
<span id="debugging-your-site"></span>
<span id="debugging-the-frontend"></span>
<span id="debugging-the-backend"></span>
<span id="using-a-custom-font"></span>
<span id="font-hosting-service"></span>
<span id="self-hosted-fonts"></span>
<span id="kobweb-server-logs"></span>
<span id="configuring-cors"></span>
<span id="generating-export-traces"></span>

## Read the Guide

If you got this far, it is time to start reading the manual!

📚 https://kobweb.varabyte.com/docs

The guide walks you through all Kobweb concepts, organized into sections to make it easier to read as well as later
continue where you left off.

> [!IMPORTANT]
> All the documentation for Kobweb used to live in this README, but it was getting so long as to be unwieldy. You may
> have ended up at this section after following an old link found in the wild. We apologize for the inconvenience, but
> you should be able to find the relevant information by
> [visiting the guide](https://kobweb.varabyte.com/docs) and using the search bar found in the top right of the page.

## Miscellaneous topics

### General purpose improvements on top of Compose HTML and Kotlin/JS

In the beginning, Kobweb was only intended to be a thin layer on top of Compose HTML, but the more we worked on it, the
more we ran into features that were simply not yet implemented in Compose HTML. In other cases, we found ourselves
reaching for utilities that we wished existed in Kotlin/JS browser APIs. As we began adding these features, we realized
it would have been a shame to bury them deep inside our framework.

As a result, we created two modules:
* [`compose-html-ext`](frontend/compose-html-ext/README.md), where we put code that we would be more than happy for the
  Compose HTML team to fork and migrate over to Compose HTML someday.
* [`browser-ext`](frontend/browser-ext/README.md), a collection of general purpose utilities that we think could be
  useful to any Kotlin/JS project targeting the browser.

The features across these modules include (not comprehensive):

* a *ton* of missing type-safe wrappers around many, many CSS properties
* type-safe wrappers around CSS functions, like gradients, filters,
  `calc` (especially useful when working with CSS variables), etc.
* rich SVG support
* utility methods around saving files to / loading files from the disk
* utility methods and classes built on top
  of [`window.fetch`](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-window-or-worker-global-scope/fetch.html)
  (for example, making it easier to use the most common HTTP verbs like GET, POST, etc.,
  as well as providing `suspend fun` versions of fetch)
* additions for the missing [transition events](https://developer.mozilla.org/en-US/docs/Web/API/TransitionEvent)
* implementations of resize and intersection observers
* utility methods for getting a sequence of descendant/ancestor HTML elements, useful for walking the DOM tree in a
  Kotlin-idiomatic way
* a utility composable, `GenericTag`, which is an easy-to-use API wrapping Compose HTML's `TagElement` composable, with
  additional namespacing support if needed (for example, required when implementing SVG elements)
* a utility class for working with CSS variables, `StyleVariable`, allows specifying a default value, provides
  first-class number/string variable support, and
  fixes [a bug in Compose HTML's `CSSStyleVariable`class](https://github.com/JetBrains/compose-multiplatform/issues/2763)
  where it can accept invalid values.
* `setTimeout` and `setInterval` methods that are more Kotlin-idiomatic (e.g. the lambdas are the last parameter)

> [!NOTE]
> Some users have mentioned we should have opened PRs for the Compose HTML team instead of maintaining a separate
> codebase. However, after observing that JetBrains was focusing more and more of its energy on Compose Multiplatform
> for Web, we decided to implement the features we needed in our own project. This way, we could maintain our velocity
> while allowing their team to pick and choose what they agreed with at some point in the future at their leisure.
> There's so much code here, especially around CSS APIs, that getting mired down in PR discussions would have ground our
> progress to a halt.

If you want to use Compose HTML but *not* Kobweb, or Kotlin/JS but *not* Compose HTML, you can still use and benefit
from `compose-html-ext` or `browser-ext` in your own project. An example build script could look like this (here, for a
non-Kobweb Compose HTML project):

```kotlin
// build.gradle.kts
plugins {
  kotlin("multiplatform") version "..."
}

repositories {
  mavenCentral()
  google()
}

kotlin {
  js().browser()
  sourceSets {
    jsMain.dependencies {
      implementation(compose.html.core)
      implementation(compose.runtime)
      implementation("com.varabyte.kobweb:compose-html-ext:...") // IMPORTANT!!!
    }
  }
}
```

> [!NOTE]
> The `compose-html-ext` dependency automatically provides the `browser-ext` dependency.
>
> And of course, if you use Kobweb, it provides both.

<!-- Some sites link to this section before I changed its name, so adding a span here so they can still find it. -->
### <span id="what-about-multiplatform-widgets"><span id="what-about-compose-for-web-canvas">What about Compose Multiplatform for Web?</span></span>

Jetbrains is working on the "Compose Multiplatform UI Framework", which allows developers to use the same codebase
across Android, iOS, Desktop, and the Web. And it may seem like the Kobweb + Silk approach is obsoleted by it.

It's first worth understanding the core difference between the two approaches. With Compose Multiplatform, the framework
owns its own rendering pipeline, drawing to a buffer. In contrast, Compose HTML modifies an HTML / CSS DOM tree and
leaves it up to the browser to do the final rendering.

This has major implications on how similar the two APIs can get. For example, in Compose Multiplatform, the order you
apply modifiers matters. However, in Compose HTML, this action simply sets html style properties under the hood, where
order does not matter.

Due to its reputation, ditching HTML / CSS entirely at first can seem like a total win, but this approach has several
limitations:

* robots would lose the ability to crawl and index your site, hurting SEO.
* your initial render may take longer (as nothing will be rendered until your site's logic is downloaded and run for one
  frame).
* your site will need to allocate a large canvas buffer, which could be *very* expensive on high-res, wide-screen
  desktops.
* your UI will be opaque to the powerful suite of devtools that come bundled with browsers.
* you won't have the ability to style unvisited vs visited links differently (this information is hidden from you by
  the browser for security reasons and can only be set through HTML / CSS).
* you won't have the ability to turn elements on / off when printing the page.
* accessibility tools for browsers might not work.
* the download size of the rendering components is not insignificant and apparently not very compressible, often
  resulting in a site's basic footprint being 4-6x larger total (e.g. 200-400K vs. 2-3MB for small sites).

It would also prevent a developer from making use of the rich ecosystem of Javascript libraries out there.

Finally, Kobweb is more than just Kotlin-ifying HTML / CSS. It also provides rich integration with powerful web
technologies like [web workers](https://kobweb.varabyte.com/docs/concepts/foundation/workers)
and [websockets](https://kobweb.varabyte.com/docs/concepts/server/fullstack#api-streams).

For now, I am making a bet that there will always be value in embracing the web, providing a framework that sticks to
HTML / CSS but offers a growing suite of UI widgets, layouts, and other features that make it a more comfortable
experience for the Kotlin developer.

For example, [the flexbox layout](https://css-tricks.com/snippets/css/a-guide-to-flexbox/) is a very powerful concept,
but it can be very tricky to use. In most cases, you'll find it's much easier to compose `Row`s and `Column`s together
than trying to remember if you should be justifying your items or aligning your content, even if `Row`s and `Column`s
are just configuring the correct HTML / CSS for you behind the scenes.

Ultimately, I believe there is room for both Compose Multiplatform *and* Kobweb. If you want to make an app experience
that feels the same on Android, iOS, Desktop, and Web, then Compose Multiplatform could be the right choice for you.
However, if you just want to make a traditional website but want to use Kotlin instead of TypeScript, Kobweb can provide
an excellent development experience for that case.

### Can We Kobweb Yet

Current state: **Foundations are in place! You may encounter API gaps.**

You may wish to refer to our [Kobweb 1.0 roadmap document](https://docs.google.com/document/d/1n2Jd02yzuxaatpT7gOhEuijzfaSu9UIaQTV2t4EPcPk/preview).

Kobweb is becoming quite functional. We are already using it to build https://kobweb.varabyte.com and
https://bitspittle.dev. Several users have created working portfolio sites already, and I'm aware of at least two cases
where Kobweb was used in a project for a client.

At this point:

* It is easy to set up a new project and get things running quickly.
* The live reloading flow is pretty nice, and you'll miss it when you switch to projects that don't have it.
* It supports generating pages from Markdown that can reference your Composable code.
* While it's not quite a server-side rendering, you can export static pages which will get hydrated on load.
* A huge range of CSS properties are supported, along with support for style variables and animations.
* You can use the `Modifier` builder for a significant number of CSS properties.
* Silk components are color-mode aware and support responsive behavior.
* There are quite a few widgets available, and it's easy to create your own.

However, there's always more to do.

* I'm trying to add support for every stabilized CSS property, but some are still missing, especially less
  common ones. (You can use a fallback for such cases in the meantime).
* There are still a handful of widgets planned to be added.
* A lot of detailed documentation is planned to go into the Kobweb site (linked just above) but it isn't done yet.

I think there's enough here now to let you do almost anything you'd want to do, as either Kobweb supports it or you can
escape hatch to underlying Compose HTML / Kotlin/JS approaches, but there might be some areas where it's still a bit
DIY. It would be great to get real-world experience to hear what issues users are actually running into.

In general, please understand that we are still pre-1.0, and as such, there is an expectation that you'll be a little
more tolerant to occasional API migrations, unlike if you were using a more stable library.

We strive hard to ensure that any code we deprecate is kept around for *at least* 6 months, but after that, we are
likely to remove it. This allows our very lean team to stay nimble as we focus on getting to a 1.0 release.

So, should you use Kobweb at this point? If you are...

* playing around with Compose HTML for the first time and want to get up and running quickly on a toy project:
  * **YES!!!** Please see the [connecting with us▼](#connecting-with-us) section
    below, we'd definitely love to hear from you. It's still a good time if you want to have a voice in the
    direction of this project.
* a Kotlin developer who wants to write a small web app or create a new blog from scratch:
  * **Probably!** I hope if you evaluate Kobweb at this point, you'll find a lot to like. You can get in touch
    with us at our Discord if you try it and have questions or run into missing features.
* someone who already has an existing project in progress and wants to integrate Kobweb into it:
  * **Maybe not?** Depending on how much work you've done, it may not be a trivial refactor. You can review
    [this guide on adding Kobweb to an existing project](https://kobweb.varabyte.com/docs/guides/existing-project) if
    you want to try anyway.
* a company:
  * **Probably not?** I'm assuming most companies are so risk-averse they would not even use Compose HTML, which Kobweb
    is built on top of. If you *were* considering Compose HTML, however, Kobweb is worth a look.

On the fence but not sure? Connect with us, and I'd be happy to help you assess your situation.

## Community

### Testimonials

I'm pleased to mention that Kobweb has received feedback from some satisfied users. Here are a few:

* "This is a pretty bloody amazing technology you've created here. I have been dreading upgrading [my] website for ages because I didn't want to go back to html and css 🫤 now I can stay with Kotlin 😀"
* "Kobweb looks fantastic and I've been [trying] to use Kotlin in all parts of [my] hobby stuff and work, so I got real excited when I saw Kobweb, [even though] I hadn't been satisfied with a web framework in a long time. Incredible work."
* "I started using Kobweb last week and I have to say this [...] reinvented web development for me. [...] I used to hate html css. After getting my hands on kobweb I’m in love with it."
* "Finally got paid -- all thanks to kobweb 🎉💥"
* "I didn't wanna learn any JS framework so when I first learned about kobweb it felt like a no-brainer; having built 2 Android apps with compose already and a backend with ktor. One could argue Android developers are the best target audience since the additional knowledge needed to move an app to the web with Kobweb is minimal. I love it! 🤩"

### Connecting with us

* [Join my Discord!](https://discord.gg/5NZ2GKV5Cs)
* [GitHub Discussions](https://github.com/varabyte/kobweb/discussions) for this project
* The [Kobweb channel](https://kotlinlang.slack.com/archives/C04RTD72RQ8) on the Kotlin Slack
* You can send direct queries to [my email](mailto:bitspittle@gmail.com)

If you're comfortable with it, using Discord is recommended, because there's a growing community of users in there who
can offer help even when I'm not around.

### Filing issues and leaving feedback

It is still early days, and while we believe we've proven the feasibility of this approach at this point, there's still
plenty of work to do to get to a 1.0 launch! We are hungry for the community's feedback, so please don't hesitate to:

* [Open an issue](https://github.com/varabyte/kobweb/issues/new/choose)
* Contact us (using any of the ways mentioned above) telling us what features you want
* Ask us for guidance, especially as there are no tutorials yet (your questions can help us know what to write first!)

Thank you for your support and interest in Kobweb!

### Supporting the project

You should feel no obligation to pay anything to use Kobweb -- it is licensed liberally quite intentionally and
given to the community without any strings attached.

However, if you like what we are doing and are determined to support our efforts financially, we would gratefully accept
a donation at [ko-fi.com/bitspittle](https://ko-fi.com/bitspittle). Money will go towards development fees and rewarding
contributors.

Alternately, there are countless non-financial ways to support this project, such as:

* Just use Kobweb and spread the word!
* Consider tagging your website with a "Made with [Kobweb](https://github.com/varabyte/kobweb)" blurb in the footer.
* Write articles about Kobweb / share your experience using it.
* Send us feedback, be it appreciative or critical. Please don't be shy about letting us know if there are things you
  feel are missing. If you have ideas that you think can make Kobweb better, please share them.
* Join our community on Discord and/or Slack and answer questions.
* Pick up a bug or feature to work on.

Ultimately, I want Kobweb to be known for having a kind, patient, and welcoming community. As long as you are helping
us accomplish that, then please consider yourself already supporting our efforts.