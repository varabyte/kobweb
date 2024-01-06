[![version: 0.15.4](https://img.shields.io/badge/kobweb-0.15.4-blue)](COMPATIBILITY.md)
[![version: 0.9.13](https://img.shields.io/badge/kobweb_cli-0.9.13-blue)](https://github.com/varabyte/kobweb-cli)
<br>
[![kotlin: 1.9.21](https://img.shields.io/badge/kotlin-1.9.21-blue?logo=kotlin)](COMPATIBILITY.md)
[![compose: 1.5.11](https://img.shields.io/badge/compose-1.5.11-blue?logo=jetpackcompose)](COMPATIBILITY.md)
[![ktor: 2.3.6](https://img.shields.io/badge/ktor-2.3.6-blue)](https://ktor.io/)
<br>
[![Varabyte Discord](https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/5NZ2GKV5Cs)
[![Kotlin Slack](https://img.shields.io/badge/%23Kobweb-4A154B?logo=slack&logoColor=white)](https://kotlinlang.slack.com/archives/C04RTD72RQ8)
[![Mastodon Follow](https://img.shields.io/mastodon/follow/109382855401210782?domain=https%3A%2F%2Ffosstodon.org&style=social)](https://fosstodon.org/@bitspittle)
# K🕸️bweb

Kobweb is an opinionated Kotlin framework for creating websites and web apps, built on top of
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) and inspired by [Next.js](https://nextjs.org)
and [Chakra UI](https://chakra-ui.com).

```kotlin
@Page
@Composable
fun HomePage() {
  Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Row(Modifier.align(Alignment.End)) {
      var colorMode by ColorMode.currentState
      Button(
        onClick = { colorMode = colorMode.opposite },
        Modifier.borderRadius(50.percent).padding(0.px)
      ) {
        // Includes support for Font Awesome icons
        if (colorMode.isLight) FaSun() else FaMoon()
      }
    }
    H1 {
      Text("Welcome to Kobweb!")
    }
    Row(Modifier.flexWrap(FlexWrap.Wrap)) {
      SpanText("Create rich, dynamic web apps with ease, leveraging ")
      Link("https://kotlinlang.org/", "Kotlin")
      SpanText(" and ")
      Link("https://github.com/JetBrains/compose-multiplatform#compose-html/", "Compose HTML")
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
* a growing collection of general purpose utilities added on top of Compose HTML ([learn more▼](#extending-the-compose-html-library))
* an open source foundation that the community can extend
* and much, much more!

Here's a demo where we create a Compose HTML project from scratch with Markdown support and live reloading, in under
10 seconds:

https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4

<span id="stevdza-san"><span> <!-- Useful way to link to the following tip -->
> [!TIP]
> One of Kobweb's users, Stevdza-San, has created free starting tutorials that demonstrate how to build projects using
> Kobweb.
>
> * [Getting started with Kobweb](https://www.youtube.com/watch?v=F5B-CxJTKlg)
>   * This video introduces basic Kobweb concepts and walks you through the process from creating a simple
>     (static layout) site to exporting it locally on your machine (with files you can then upload to a static hosting
>     provider of your choice).
> * [Deploying a Kobweb site](https://www.youtube.com/watch?v=ciAqQPThXn0)
>   * This video builds upon the previous, showcasing some additional tips and tricks, and walks you all the way through
>     to deploying your site live on the internet using free hosting.
> * [Building a full stack multiplatform site](https://www.youtube.com/watch?v=zcrY0qayWF4)
>   * This demonstrates how to write both frontend and backend logic. It also demonstrates how you can write a separate
>     Android frontend that can also work with your server. (This video is still useful to watch even if you never
>     intend to use Android).
>
> It's easy to start with a static layout site first and migrate to a full stack site later. (You can read more
> about [Static layout vs. Full stack sites▼](#static-layout-vs-full-stack-sites) below.)

# Trying it out yourself

The first step is to get the Kobweb binary. You can install it, download it, and/or build it, so we'll include
instructions for all these approaches.

## Install the Kobweb binary

*Major thanks to [aalmiray](https://github.com/aalmiray) and [helpermethod](https://github.com/helpermethod) to helping
me get these installation options working. Check out [JReleaser](https://github.com/jreleaser/jreleaser) if you ever
need to do this in your own project!*

### [Homebrew](https://brew.sh/)

*OS: Mac and Linux*

```bash
$ brew install varabyte/tap/kobweb
```

### [Scoop](https://scoop.sh/)

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

### [SDKMAN!](https://sdkman.io/)

*OS: Windows, Mac, and \*nix*

```shell
$ sdk install kobweb
```

### Don't see your favorite package manager?

Please see: https://github.com/varabyte/kobweb/issues/117 and consider leaving a comment!

## Download the Kobweb binary

Our binary artifact is hosted on GitHub. To download the latest, you can either
[grab the zip or tar file from GitHub](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.13) or you can fetch
it from your terminal:

```bash
$ cd /path/to/applications

# You can either pull down the zip file

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.13/kobweb-0.9.13.zip
$ unzip kobweb-0.9.13.zip

# ... or the tar file

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.13/kobweb-0.9.13.tar
$ tar -xvf kobweb-0.9.13.tar
```

and I recommend adding it to your path, either directly:

```bash
$ PATH=$PATH:/path/to/applications/kobweb-0.9.13/bin
$ kobweb version # to check it's working
```

or via symbolic link:

```bash
$ cd /path/to/bin # some folder you've created that's in your PATH
$ ln -s /path/to/applications/kobweb-0.9.13/bin/kobweb kobweb
```

## Build the Kobweb binary

Although we host Kobweb artifacts on GitHub, it's easy enough to build your own.

Building Kobweb requires JDK11 or newer. We'll first discuss how to add it.

### Download a JDK

If you want full control over your JDK install, manually downloading is a good option.

* [Download a JDK for your OS](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Unzip it somewhere
* Update your `JAVA_HOME` variable to point at it.

```bash
JAVA_HOME=/path/to/jdks/corretto-11.0.12
# ... or whatever version or path you chose
```

### Install a JDK with the IntelliJ IDE

For a more automated approach, you can request IntelliJ install a JDK for you.

Follow their instructions here: https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk

### Building the Kobweb CLI

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

## Update the Kobweb binary

If you previously installed Kobweb and are aware that a new version is available, the way you update it depends on how
you installed it.

| Method                    | Instructions                                                                                                                         |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Homebrew                  | `brew update`<br/>`brew upgrade kobweb`                                                                                               |
| Scoop                     | `scoop update kobweb`                                                                                                                |
| SDKMAN!                   | `sdk upgrade kobweb`                                                                                                                 |
| Downloaded from<br>Github | Visit the [latest release](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.13). You can find both a zip and tar file there. |

## Create your Kobweb site

```bash
$ cd /path/to/projects/
$ kobweb create app
```

You'll be asked a few questions required for setting up your project.

You don't need to create a root folder for your project ahead of time - the setup process will prompt you for one to
create.

When finished, you'll have a basic project with three pages - a home page, an about page, and a markdown page - and some
components (which are collections of reusable, composable pieces). Your own directory structure should look something
like this:

```
my-project
└── site/src/jsMain
    ├── kotlin.org.example.myproject
    │   ├── components
    │   │   ├── layouts
    │   │   │   └── PageLayout.kt
    │   │   ├── sections
    │   │   │   └── NavHeader.kt
    │   │   └── widgets
    │   │       └── GoHomeLink.kt
    │   ├── pages
    │   │   ├── About.kt
    │   │   └── Index.kt
    │   └── MyApp.kt
    └── resources/markdown
        └── Markdown.md
```

Note that there's no index.html or routing logic anywhere! We generate that for you automatically when you run Kobweb.
This brings us to the next section...

## Run your Kobweb site

```bash
$ cd /path/to/projects/your-project/site
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
  `kobwebExport -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=KOBWEB -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=KOBWEB`
  * If you want to export a static layout instead, change the last argument to<br>`-PkobwebExportLayout=STATIC`.
* To run an exported site, use<br>
  `kobwebStart -PkobwebEnv=PROD -PkobwebRunLayout=KOBWEB`
  * If your site was exported using a static layout, change the last argument to<br>`-PkobwebRunLayout=STATIC`.

You can read all about [IntelliJ's Gradle integration here](https://www.jetbrains.com/help/idea/gradle.html). Or to just jump straight into how to create run
configurations for any of the commands discussed above, read [these instructions](https://www.jetbrains.com/help/idea/run-debug-gradle.html).

## Running examples

Kobweb will provide a growing collection of samples for you to learn from. To see what's available, run:

```bash
$ kobweb list

You can create the following Kobweb projects by typing `kobweb create ...`

• app: A template for a minimal site that demonstrates the basic features of Kobweb
• examples/jb/counter: A very minimal site with just a counter (based on the Jetbrains tutorial)
• examples/todo: An example TODO app, showcasing client / server interactions
```

For example, `kobweb create examples/todo` will instantiate a TODO app locally.

### Gradle version catalogs

The project templates created by Kobweb all embrace Gradle version catalogs.

If you're not aware of it, it's a file that exists at `gradle/libs.versions.toml`. If you find yourself wanting to tweak
or add new versions to projects you originally created via `kobweb create`, that's where you'll find them.

For example, here's the
[libs.versions.toml](https://github.com/varabyte/kobweb-site/blob/main/gradle/libs.versions.toml) we use for our own
landing site.

To read more about the feature, please check out the
[official docs](https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml).

#### Upgrading Kobweb in your project

The latest available version of Kobweb is declared at the top of this README. If a new version has come out, you can
update your own project by editing `gradle/libs.version.toml` and updating the `kobweb` version there.

> [!IMPORTANT]
> You should double-check [COMPATIBILITY.md](COMPATIBILITY.md) to see if you also need to update your `kotlin` and
> `jetbrains-compose` versions as well.

> [!CAUTION]
> It can be confusing, but Kobweb has two versions -- the version for the library itself (the one that is applicable in
> this situation), and the one for the command line tool.

# Beginner topics

Kobweb, at its core, is a handful of classes responsible for trimming away much of the boilerplate around building a
Compose HTML app, such as routing and configuring basic CSS styles.

Kobweb is also a CLI binary of the same name which provides commands to handle the tedious parts of building and/or
running a Compose HTML app. We want to get that stuff out of the way, so you can enjoy focusing on the more
interesting work!

## Create a page

Creating a page is easy! It's just a normal `@Composable` method. To upgrade your composable to a page, all you need to
do is:

1. Define your composable in a file somewhere under the `pages` package in your `jsMain` source directory.
1. Annotate it with `@Page`

Just from that, Kobweb will create a site entry for you automatically.

For example, if I create the following file:

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page
@Composable
fun SettingsPage() {
    /* ... */
}
```

this will create a page that I can then visit by going to `mysite.com/admin/settings`.

> [!IMPORTANT]
> The last part of a URL, here `settings`, is called a *slug*.

By default, the slug comes from the file name, but this behavior can be overridden (more on that shortly).

The file name `Index.kt` is special. If a page is defined inside such a file, it will be treated as the default page
under that URL. For example, a page defined in `.../pages/admin/Index.kt` will be visited if the user visits
`mysite.com/admin/`.

### Route Override

If you ever need to change the route generated for a page, you can set the `Page` annotation's `routeOverride` field:

```kotlin
// jsMain/kotlin/com/mysite/pages/admin/Settings.kt

@Page(routeOverride = "config")
@Composable
fun SettingsPage() {
    /* ... */
}
```

The above would create a page you could visit by going to `mysite.com/admin/config`.

`routeOverride` can additionally contain slashes, and if the value begins and/or ends with a slash, that has a special
meaning.

* Begins with a slash - represent the whole route from the root
* Ends with a slash - a slug will still be generated from the filename and appended to the route.

And if you set the override to "index", that behaves the same as setting the file to `Index.kt` as described above.

Some examples can clarify these rules (and how they behave when combined). Assuming we're defining a page for our site
`example.com` within the file `a/b/c/Slug.kt`:

| Annotation              | Resulting URL                   |
|-------------------------|---------------------------------|
| `@Page`                 | `example.com/a/b/c/slug`        |
| `@Page("other")`        | `example.com/a/b/c/other`       |
| `@Page("index")`        | `example.com/a/b/c/`            |
| `@Page("d/e/f/")`       | `example.com/a/b/c/d/e/f/slug`  |
| `@Page("d/e/f/other")`  | `example.com/a/b/c/d/e/f/other` |
| `@Page("d/e/f/index")`  | `example.com/a/b/c/d/e/f/`      |
| `@Page("/d/e/f/")`      | `example.com/d/e/f/slug`        |
| `@Page("/d/e/f/other")` | `example.com/d/e/f/other`       |
| `@Page("/d/e/f/index")` | `example.com/d/e/f/`            |
| `@Page("/")`            | `example.com/slug`              |
| `@Page("/other")`       | `example.com/other`             |
| `@Page("/index")`       | `example.com/`                  |

> [!CAUTION]
> Despite the flexibility allowed here, you should not be using this feature frequently, if at all. A Kobweb project
> benefits from the fact that a user can easily associate a URL on your site with a file in your codebase, but this
> feature allows you to break those assumptions. It is mainly provided to enable dynamic routing (see the
> [Dynamic Routes▼](https://github.com/varabyte/kobweb?tab=readme-ov-file#dynamic-routes) section) or enabling a URL
> name that uses characters which aren't allowed in Kotlin filenames.

### PackageMapping

If you don't want to change your slug but you *do* want to change a part of the route, you don't have to use a `Page`
annotation for this. You can instead register a package mapping with a `PackageMapping` file annotation. Doing so looks
like this:

```kotlin
// site/pages/blog/_2022/PackageMapping.kt
@file:PackageMapping("2022")

package site.pages.blog._2022

import com.varabyte.kobweb.core.PackageMapping
```

As with the `Page` route overrides, the main reason you'd want to do this is that Java / Kotlin package naming
requirements are much stricter than what you might want to allow in a URL part. `site.com/blog/2022/mypost` reads way
better than `site.com/blog/_2022/mypost`.

### Page context

Every page method provides access to its `PageContext` via the `rememberPageContext()` method.

A page's context provides it access to a router, allowing you to navigate to other pages, as well as other dynamic
information about the current page's URL (discussed in the next section).

```kotlin
@Page
@Composable
fun ExamplePage() {
    val ctx = rememberPageContext()
    Button(onClick = { ctx.router.navigateTo("/other/page") }) {
        Text("Click me")
    }
}
```

### Query parameters

You can use the page context to check the values of any query parameters passed into the current page's URL.

So if you visit `site.com/posts?id=12345&mode=edit`, you can query those values like so:

```kotlin
enum class Mode {
    EDIT, VIEW;

    companion object {
        fun from(value: String) {
           entries.find { it.name.equals(value, ignoreCase = true) }
               ?: error("Unknown mode: $value")
        }
    }
}

@Page
@Composable
fun Posts() {
    val ctx = rememberPageContext()
    // Here, I'm assuming these params are always present, but you can
    // use `get` instead of `getValue` to handle the nullable case.
    val postId = ctx.route.params.getValue("id").toInt()
    val mode = Mode.from(ctx.route.params.getValue("mode"))
    /* ... */
}
```

### Dynamic routes

In addition to query parameters, Kobweb supports embedding arguments directly in the URL itself. For example, you might
want to register the path `users/{user}/posts/{post}` which would be visited if the site visitor typed in a URL like
`users/bitspittle/posts/20211231103156`.

How do we set it up? Thankfully, it's fairly easy.

But first, notice that in the example dynamic route `users/{user}/posts/{post}` there are actually two different dynamic
parts, one in the middle and one at the tail end. These can be handled by the `PackageMapping` and `Page` annotations,
respectively.

#### PackageMapping

Pay attention to the use of the curly braces in the mapping name! That lets Kobweb know that this is a dynamic package.

```kotlin
// pages/users/user/PackageMapping.kt
@file:PackageMapping("{user}") // or @file:PackageMapping("{}")

package site.pages.users.user

import com.varabyte.kobweb.core.PackageMapping
```

If you pass an empty `"{}"` into the `PackageMapping` annotation, it directs Kobweb to use the name of the package
itself (i.e. `user` in this specific case).

#### Page

Like `PackageMapping`, the `Page` annotation can also take curly braces to indicate a dynamic value.

```kotlin
// pages/users/user/posts/Post.kt

@Page("{post}") // Or @Page("{}")
@Composable
fun PostPage() {
   /* ... */
}
```

An empty `"{}"` tells Kobweb to use the name of the current file.

Remember that the `Page` annotation allows you to rewrite the entire route. That value also accepts dynamic parts, so
you could even do something like:

```kotlin
// pages/users/user/posts/Post.kt

@Page("/users/{user}/posts/{post}") // Or @Page("/users/{user}/posts/{}")
@Composable
fun PostPage() {
    /* ... */
}
```

but with great power comes great responsibility. Tricks like this may be hard to find and/or update later, especially as
your project gets larger. While it works, you should only use this format in cases where you absolutely need to (perhaps
after a code refactor where you have to support legacy URL paths).

#### Querying dynamic route values

You query dynamic route values exactly the same as if you were requesting query parameters. That is, use `ctx.params`:

```kotlin
@Page("{}")
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val postId = ctx.route.params.getValue("post")
    /* ... */
}
```

> [!IMPORTANT]
> You should avoid creating URL paths where the dynamic path and the query parameters have the same name, as in
> `mysite.com/posts/{post}?post=...`, as this could be really tricky to debug in a complex project. If there is a
> conflict, then the dynamic route parameters will take precedence. (You can still access the query parameter value via
> `ctx.route.queryParams` in this case if necessary.)

## Silk

Silk is a UI layer included with Kobweb and built upon Compose HTML. (To learn more about Compose HTML, please
visit [the official tutorials](https://github.com/JetBrains/compose-jb/tree/master/tutorials/HTML/Getting_Started)).

While Compose HTML requires you to understand underlying HTML / CSS concepts, Silk attempts to abstract some of that
away, providing an API more akin to what you might experience developing a Compose app on Android or Desktop. Less
"div, span, flexbox, attrs, styles, classes" and more "Rows, Columns, Boxes, and Modifiers".

We consider Silk a pretty important part of the Kobweb experience, but it's worth pointing out that it's designed as an
optional component. You can absolutely use Kobweb without Silk. (You can also use Silk without Kobweb!).

You can also interleave Silk and Compose HTML components easily (as Silk is just composing them itself).

### Inline vs StyleSheet

For those new to web dev, it's worth understanding that there are two ways to set styles on your HTML elements: inline
and stylesheet.

Inline styles are defined on the element tag itself. In raw HTML, this might look like:

```html
<div style="background-color:black">
```

Meanwhile, any given HTML page can reference a list of stylesheets which can define a bunch of styles, where each style
is tied to a selector (a rule which _selects_ what elements those styles apply to).

A concrete example of a very short stylesheet can help here:

```css
body {
  background-color: black;
  color: magenta
}
#title {
  color: yellow
}
```

And you could use that stylesheet to style the following document:

```html
<body>
  <!-- Title gets background-color from "body" and foreground color from "#title" -->
  <div id="title">Yellow on black</div>
  Magenta on black
</body>
```

There's no hard and fast rule, but in general, when writing HTML / CSS by hand, stylesheets are often preferred over
inline styles as it better maintains a separation of concerns. That is, the HTML should represent the content of your
site, while the CSS controls the look and feel.

However! We're not writing HTML / CSS by hand. We're using Compose HTML! Should we even care about this in Kotlin?

As it turns out, there are times when you have to use stylesheets, because without them, you can't define styles for
advanced behaviors
(particularly [pseudo classes](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-classes), [pseudo elements](https://developer.mozilla.org/en-US/docs/Web/CSS/Pseudo-elements),
and [media queries](https://developer.mozilla.org/en-US/docs/Web/CSS/Media_Queries/Using_media_queries)). For example,
you can't override the color of visited links without using a stylesheet approach. So it's worth realizing there are
fundamental differences.

Finally, it can also be much easier debugging your page with browser tools when you lean on stylesheets over inline styles, as it
makes your DOM tree easier to read when your elements are simple (e.g. `<div class="title">`
vs. `<div style="color:yellow; background-color:black; font-size: 24px; ...">`).

---

We'll be introducing and discussing modifiers and component styles in more detail shortly. But in general, when you pass
modifiers directly into a composable widget in Silk, those will result in inline styles, whereas if you use a component
style to define your styles, those will get embedded into the site's stylesheet:

```kotlin
// Uses inline styles
Box(Modifier.color(Colors.Red)) { /* ... */ }

// Uses a stylesheet
val BoxStyle by ComponentStyle {
    base { Modifier.Color(Colors.Red) }
}
Box(BoxStyle.toModifier()) { /* ... */ }
```

As a beginner, or even as an advanced user when prototyping, feel free to use inline modifiers as much as you can,
pivoting to component styles if you find yourself needing to use pseudo-classes, pseudo-elements, or media queries. It
is fairly easy to migrate inline styles over to stylesheets in Kobweb.

In my own projects, I tend to use inline styles for really simple layout elements (e.g. `Row(Modifier.fillMaxWidth())`)
and component styles for complex and/or re-usable widgets. It actually becomes a nice organizational convention to have
all your styles grouped together in one place above the widget itself.

### `@InitSilk` methods

Before going further, we want to quickly mention you can annotate a method with `@InitSilk`, which will be called when
your site starts up.

This method must take a single `InitSilkContext` parameter. A context contains various properties that allow for
adjusting Silk defaults and which will be demonstrated in more detail in sections below.

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  // `ctx` has a handful of properties which allow you to adjust Silk's default behavior.
}
```

> [!TIP]
> The names of your `@InitSilk` methods don't matter, as long as they're public, take a single `InitSilkContext`
> parameter, and don't collide with another method of the same name. You are encouraged to choose a name for readability
> purposes.
>
> You can define as many `@InitSilk` methods as you want, so feel free to break them up into relevant, clearly named
> pieces, instead of declaring a single, monolithic, generically named `fun initSilk(ctx)` method that does everything.

### Modifier

Silk introduces the `Modifier` class, in order to provide an experience similar to what you find in Jetpack Compose.
(You can read [more about them here](https://developer.android.com/jetpack/compose/modifiers) if you're unfamiliar with
the concept).

In the world of Compose HTML, you can think of a `Modifier` as a wrapper on top of CSS styles and attributes.

> [!IMPORTANT]
> Please refer to official documentation if you are not familiar with
> HTML [attributes](https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes)
> and/or [styles](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/style).

So this:

```kotlin
Modifier.backgroundColor(Colors.Red).color(Colors.Green).padding(200.px)
```

when passed into a widget provided by Kobweb, like `Box`:

```kotlin
Box(Modifier.backgroundColor(Colors.Red).color(Colors.Green).padding(200.px)) {
    /* ... */
}
```

would generate an HTML tag with a style property like: `<div style="background:red;color:green;padding:200px">`

#### attrsModifier and styleModifier

There are a bunch of modifier extensions (and they're growing) provided by Kobweb, like `background`, `color`, and
`padding` above. But there are also two escape hatches anytime you run into a modifier that's missing:
`attrsModifier` and `styleModifier`.

At this point, you are interacting with Compose HTML, one layer underneath Kobweb.

Using them looks like this:

```kotlin
// Modify attributes of an element tag
// e.g. the "a", "b", and "c" in <tag a="..." b="..." c="..." />
Modifier.attrsModifier {
    id("example")
}

// Modify styles of an element tag
// e.g. the "x", "y", and "z" in `<tag a="..." b="..." c="..." style="x:...;y:...;z:..." />
Modifier.styleModifier {
    width(100.percent)
    height(50.percent)
}

// Note: Because "style" itself is an attribute, you can define styles in an attrsModifier:
Modifier.attrsModifier {
    id("example")
    style {
        width(100.percent)
        height(50.percent)
    }
}
// ... but in the above case, you should use a styleModifier for simplicity
```

In the occasional (and hopefully rare!) case where Kobweb doesn't provide a modifier and Compose HTML doesn't provide
the attribute or style support you need, you can use `attrsModifier` plus the `attr` method or `styleModifier` plus the
`property` method. This escape hatch within an escape hatch allows you to provide any custom value you need.

The above cases can be rewritten as:

```kotlin
Modifier.attrsModifier {
    attr("id", "example")
}

Modifier.styleModifier {
    property("width", 100.percent)
    // Or even raw CSS:
    // property("width", "100%")
    property("height", 50.percent)
}
```

If you end up needing to use `attr` or `property` in your own codebase, consider
[filing an issue](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=)
with us so that we can add the missing modifier to the library.

### ComponentStyle

With Silk, you can define a style like so, using the `base` block:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.background(Colors.Red)
    }
}
```

and convert it to a modifier by using `CustomStyle.toModifier()`. At this point, you can pass it into any composable
which takes a `Modifier` parameter:

```kotlin
// Approach #1 (uses inline styles)
Box(Modifier.backgroundColor(Colors.Red)) { /* ... */ }

// Approach #2 (uses stylesheets)
Box(CustomStyle.toModifier()) { /* ... */ }
```

> [!IMPORTANT]
> When you declare a `ComponentStyle`, it must be public. This is because code gets generated inside a `main.kt` file by
> the Kobweb Gradle plugin, and that code needs to be able to access your style in order to register it.
>
> In general, it's a good idea to think of styles as global anyway, since technically they all live in a globally
> applied stylesheet, and you have to make sure that the style name is unique across your whole application.
>
> You can technically make a style private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_STYLE")
> private val SomeCustomStyle by ComponentStyle { /* ... */ }
>
> @InitSilk
> fun registerPrivateStyle(ctx: InitSilkContext) {
>   ctx.theme.registerComponentStyle(SomeCustomStyle)
> }
> ```
>
> However, you are encouraged to keep your styles public and let the Kobweb Gradle plugin handle everything for you.

#### `ComponentStyle.base`

You can simplify the syntax of basic component styles a bit further with the `ComponentStyle.base` declaration:

```kotlin
val CustomStyle by ComponentStyle.base {
    Modifier.background(Colors.Red)
}
```

Just be aware you may have to break this out again if you find yourself needing to
support [additional states▼](#additional-states).

#### ComponentStyle name

Note above we used the `by` keyword above to create a component style. This automatically generates a name for your
style under the hood, derived from the property name itself but using [Kebab Case](https://www.freecodecamp.org/news/snake-case-vs-camel-case-vs-pascal-case-vs-kebab-case-whats-the-difference/#kebab-case).

For example, if you write `val TitleTextStyle by ComponentStyle`, its name behind the scenes will be "title-text".

You usually won't need to care about this name, but there are niche cases where it can be useful to understand that is
what's going on.

If you need to set a name manually, there's an alternate constructor version (notice the use of assignment instead of
the `by` keyword):

```kotlin
val CustomStyle = ComponentStyle("my-custom-name") {
    base {
        Modifier.background(Colors.Red)
    }
}
```

#### Additional states

So, what's up with the `base` block?

True, it looks a bit verbose on its own. However, you can define additional styles that take effect conditionally. The
base style will always apply first, but then additional styles can be applied based on what state the element is in. (If
multiple states are applicable at the same time, they will be applied in the order specified.)

Here, we create a style which is red by default, but green when the mouse hovers over it:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.color(Colors.Red)
    }

    hover {
        Modifier.color(Colors.Green)
    }
}
```

Kobweb provides a bunch of these state blocks for you for convenience, but for those who are CSS-savvy, you can always
define the CSS rule directly to enable more complex combinations or reference states that Kobweb hasn't added yet.

For example, this is identical to the above style definition:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.color(Colors.Red)
    }

    cssRule(":hover") {
        Modifier.color(Colors.Green)
    }
}
```

#### Breakpoints

There's a feature in the world of responsive HTML / CSS design called breakpoints, which confusingly have nothing to do
with debugging breakpoints. Rather, they specify size boundaries for your site when styles change. This is how sites
present content differently on mobile vs. tablet vs. desktop.

Kobweb provides four breakpoint sizes you can use for your project, which, including using no breakpoint size at all,
gives you five buckets you can work with when designing your site:

* no breakpoint - mobile (and larger)
* sm - tablets (and larger)
* md - desktops (and larger)
* lg - widescreen (and larger)
* xl - ultra widescreen (and larger)

You can change the default values of breakpoints for your site by adding
an `@InitSilk` method to your code and setting `ctx.theme.breakpoints`:

```kotlin
@InitSilk
fun initializeBreakpoints(ctx: InitSilkContext) {
    ctx.theme.breakpoints = BreakpointSizes(
        sm = 30.cssRem,
        md = 48.cssRem,
        lg = 62.cssRem,
        xl = 80.cssRem,
    )
}
```

To reference a breakpoint in a `ComponentStyle`, just invoke it:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.fontSize(24.px)
    }

    Breakpoint.MD {
        Modifier.fontSize(32.px)
    }
}
```

#### Color-mode aware

When you define a `ComponentStyle`, an optional field is available for you to use called `colorMode`:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.color(if (colorMode.isLight) Colors.Red else Colors.Pink)
    }
}
```

Silk defines a bunch of light and dark colors for all of its widgets, and if you'd like to re-use any of them in your
own widget, you can query them using `colorMode.toPalette()`:

```kotlin
val CustomStyle by ComponentStyle {
    base {
        Modifier.color(colorMode.toPalette().link.default)
    }
}
```

`SilkTheme` contains very simple (e.g. black and white) defaults, but you can override them in
an `@InitSilk` method, perhaps to something that is more brand-aware:

```kotlin
// Assume a bunch of color constants (e.g. BRAND_LIGHT_COLOR) are defined somewhere

@InitSilk
fun overrideSilkTheme(ctx: InitSilkContext) {
  ctx.theme.palettes.light.background = BRAND_LIGHT_BACKGROUND
  ctx.theme.palettes.light.color = BRAND_LIGHT_COLOR
  ctx.theme.palettes.dark.background = BRAND_DARK_BACKGROUND
  ctx.theme.palettes.dark.color = BRAND_DARK_COLOR
}
```

#### ComponentVariant

With a style, you can also create a variant of that style (that is, additional modifications that are always applied
_on top of_ the style).

You define one using the `ComponentStyle.addVariant` method, but otherwise the declaration looks the same as defining a
`ComponentStyle`:

```kotlin
val HighlightedCustomVariant by CustomStyle.addVariant {
    base {
        Modifier.backgroundColor(Colors.Green)
    }
}
```

> [!NOTE]
> A common naming convention for variants is to take their associated style and use its name as a suffix plus the word
> "Variant", e.g. "ButtonStyle" -> "GhostButtonVariant" and "TextStyle" -> "OutlinedTextVariant".

> [!IMPORTANT]
> Like a `ComponentStyle`, your `ComponentVariant` must be public. This is for the same reason: because code gets
> generated inside a `main.kt` file by the Kobweb Gradle plugin, and that code needs to be able to access your variant
> in order to register it.
>
> You can technically make a variant private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_COMPONENT_VARIANT")
> private val SomeCustomVariant by SomeCustomStyle.addVariant {
>   /* ... */ 
> }
>
> @InitSilk
> fun registerPrivateVariant(ctx: InitSilkContext) {
>   ctx.theme.registerComponentVariant(SomeCustomStyle)
> }
> ```
>
> However, you are encouraged to keep your variants public and let the Kobweb Gradle plugin handle everything for you.

Variants can be particularly useful if you're defining a custom widget that has default styles, but you want to give
callers an easy way to deviate from it in special cases.

For example, maybe you define a button widget (perhaps you're not happy with the one provided by Silk):

```kotlin
val ButtonStyle by ComponentStyle { /* ... */ }

// Note: Creates a style called "button-outline"
val OutlineButtonVariant by ButtonStyle.addVariant { /* ... */ }

// Note: Creates a style called "button-inverted"
val InvertedButtonVariant by ButtonStyle.addVariant { /* ... */ }
```

The `ComponentStyle.toModifier(...)` method, mentioned earlier, optionally takes a variant parameter. When passed in,
both styles will be applied -- the base style followed by the variant style.

For example, `MyButtonStyle.toModifier(OutlineButtonVariant)` applies the main button style first followed by additional
outline styling.

> [!CAUTION]
> Using a variant that was created from a different style will have no effect. In other words,
> `LinkStyle.toModifier(OutlineButtonVariant)` will ignore the button variant in that case.

##### `ComponentVariant.addVariantBase`

Like `ComponentStyle.base`, variants that don't need to support additional states can use `addVariantBase` instead to
slightly simplify their declaration:

```kotlin
val HighlightedCustomVariant by CustomStyle.addVariantBase {
    Modifier.backgroundColor(Colors.Green)
}
```

##### ComponentVariantName

Like component styles created using the `by` keyword, variants have their name autogenerated for you. If you need to
control this name for any reason, you can use assignment instead and pass a name into `addVariant`, e.g.
`val InvertedButtonVariant = ButtonStyle.addVariant("custom-name") { /* ... */ }`

#### Writing custom widgets

While Silk methods are all written to support component styles and variants, if you ever want to write your own custom
widget that mimics Silk, your code should look something like:

```kotlin
val CustomWidgetStyle by ComponentStyle { /* ... */ }

@Composable
fun CustomWidget(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    @Composable content: () -> Unit
) {
    val finalModifier = CustomWidgetStyle.toModifier(variant).then(modifier)
    Box(finalModifier, content)
}
```

In other words, you should take in an optional `ComponentVariant` parameter, and then you should apply the modifiers in
order of: base style, then variant, then finally user overrides.

A caller might call your widget one of several ways:

```kotlin
// Approach #1: Use default styling
CustomWidget { /* ... */ }

// Approach #2: Tweak default styling with a variant
CustomWidget(variant = TransparentWidgetVariant) { /* ... */ }

// Approach #3: Tweak default styling with user overrides
CustomWidget(Modifier.backgroundColor(Colors.Blue)) { /* ... */ }

// Approach #4: Tweak default styling with a variant and then user overrides
CustomWidget(Modifier.backgroundColor(Colors.Blue), variant = TransparentWidgetVariant) { /* ... */ }
```

### Animations

In CSS, animations work by letting you define keyframes in a stylesheet which you then reference, by name, in an
animation style. You can read more about them
[on Mozilla's documentation site](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations/Using_CSS_animations).

For example, here's the CSS for an animation of a sliding rectangle
([from this tutorial](https://www.w3schools.com/cssref/tryit.php?filename=trycss3_animation)):

```css
div {
  width: 100px;
  height: 100px;
  background: red;
  position: relative;
  animation: mymove 5s infinite;
}

@keyframes mymove {
  from {left: 0px;}
  to {left: 200px;}
}
```

Kobweb lets you define your keyframes in code by using the `by Keyframes` pattern:

```kotlin
val ShiftRight by Keyframes {
    from { Modifier.left(0.px) }
    to { Modifier.left(200.px) }
}

// Later
Div(
    Modifier
        .size(100.px).backgroundColor(Colors.Red).position(Position.Relative)
        .animation(ShiftRight.toAnimation(
            duration = 5.s,
            iterationCount = AnimationIterationCount.Infinite
        ))
      .toAttrs()
)
```

The name of the keyframes block is automatically derived from the property name (here, `ShiftRight` is converted into
`"shift-right"`). You can then use the `toAnimation` method to convert your collection of keyframes into an animation that
uses them, which you can pass into the `Modifier.animation` modifier.

> [!IMPORTANT]
> When you declare a `Keyframes` animation, it must be public. This is because code gets generated inside a `main.kt`
> file by the Kobweb Gradle plugin, and that code needs to be able to access your variant in order to register it.
>
> In general, it's a good idea to think of animations as global anyway, since technically they all live in a globally
> applied stylesheet, and you have to make sure that the animation name is unique across your whole application.
>
> You can technically make an animation private if you add a bit of boilerplate to handle the registration yourself:
>
> ```kotlin
> @Suppress("PRIVATE_KEYFRAMES")
> private val SomeAnim by Keyframes { /* ... */ }
>
> @InitSilk
> fun registerPrivateAnim(ctx: InitSilkContext) {
>   ctx.stylesheet.registerKeyframes(SomeAnim)
> }
> ```
>
> However, you are encouraged to keep your animations public and let the Kobweb Gradle plugin handle everything for you.

### ElementRefScope and raw HTML elements

Occasionally, you may need access to the raw element backing the Silk widget you've just created. All Silk widgets
provide an optional `ref` parameter which takes a listener that provides this information.

```kotlin
Box(
    ref = /* ... */
) {
    /* ... */
}
```

All `ref` callbacks (discussed more below) will receive an `org.w3c.dom.Element` subclass. You can check out the
[Element](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-element/) class (and its often more
relevant [HTMLElement](https://kotlinlang.org/api/latest/jvm/stdlib/org.w3c.dom/-h-t-m-l-element/) inheritor) to see the
methods and properties that are available on it.

Raw HTML elements expose a lot of functionality not available through the higher-level Compose HTML APIs.

#### `ref`

For a trivial but common example, we can use the raw element to capture focus:

```kotlin
Box(
    ref = ref { element ->
        // Triggered when this Box is first added into the DOM
        element.focus()
    }
)
```

The `ref { ... }` method can actually take one or more optional keys of any value. If any of these keys change on a
subsequent recomposition, the callback will be rerun:

```kotlin
val colorMode by ColorMode.currentState
Box(
    // Callback will get triggered each time the color mode changes
    ref = ref(colorMode) { element -> /* ... */ }
)
```

#### `disposableRef`

If you need to know both when the element enters AND exits the DOM, you can use `disposableRef` instead. With
`disposableRef`, the very last line in your block must be a call to `onDispose`:

```kotlin
val activeElements: MutableSet<HTMLElement> = /* ... */

/* ... later ... */

Box(
    ref = disposableRef { element ->
        activeElements.put(element)
        onDispose { activeElements.remove(element) }
    }
)
```

The `disposableRef` method can also take keys that rerun the listener if any of them change. The `onDispose` callback
will also be triggered in that case, as the old effect gets discarded.

#### `refScope`

And, finally, you may want to have multiple listeners that are recreated independently of one another based on different
keys. You can use `refScope` as a way to combine two or more `ref` and/or `disposableRef` calls in any combination:

```kotlin
val isFeature1Enabled: Boolean = /* ... */
val isFeature2Enabled: Boolean = /* ... */

Box(
    ref = refScope {
        ref(isFeature1Enabled) { element -> /* ... */ }
        disposableRef(isFeature2Enabled) { element -> /* ... */; onDispose { /* ... */ } }
    }
)
```

#### Compose HTML refs

You may occasionally want the backing element of a normal Compose HTML widget, such as a `Div` or `Span`. However, these
widgets don't have a `ref` callback, as that's a convenience feature provided by Silk.

You still have a few options in this case.

The official way to retrieve a reference is by using a `ref` block inside an `attrs` block. This version of `ref` is
actually more similar to Silk's `disposableRef` concept than its `ref` one, as it requires an `onDispose` block:

```kotlin
Div(attrs = {
    ref { element -> /* ... */; onDispose { /* ... */ } }
})
```

*The above snippet was adapted from [the official tutorials](https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/HTML/Using_Effects#ref-in-attrsbuilder).*

You could put that exact same logic inside the `Modifier.toAttrs` block if you're terminating some modifier chain:

```kotlin
Div(attrs = Modifier.toAttrs {
  ref { element -> /* ... */; onDispose { /* ... */ } }
})
```

Unlike Silk's version of `ref`, Compose HTML's version does not accept keys. If you need this behavior and if the
Compose HTML widget accepts a content block (many of them do), you can call Silk's `registerRefScope` method directly
within it:

```kotlin
Div {
  registerRefScope(
    disposableRef { element -> /* ... */; onDispose { /* ... */ } }
    // or ref { element -> /* ... */ }
  )
}
```

### Style Variables

Kobweb supports CSS variables (also called CSS custom properties), which is a feature where you can store and retrieve
property values from variables declared within your CSS styles. It does this through a class called `StyleVariable`.

> [!NOTE]
> You can find [official documentation for CSS custom properties here](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties).

Using style variables is fairly simple. You first declare one without a value (but lock it down to a type) and later you
can initialize it within a style using `Modifier.setVariable(...)`:

```kotlin
val dialogWidth by StyleVariable<CSSLengthNumericValue>()

// This style will be applied to a div that wraps the whole page
val RootStyle by ComponentStyle {
  base { Modifier.setVariable(dialogWidth, 600.px) }
}
```

> [!TIP]
> Compose HTML provides a `CSSLengthValue`, which represents concrete values like `10.px` or `5.cssRem`. However, Kobweb
> provides a `CSSLengthNumericValue` type which represents the concept more generally, e.g. as the result of
> intermediate calculations. There are `CSS*NumericValue` types provided for all relevant units, and it is recommended
> to use them when declaring style variables as they more naturally support being used in calculations.

You can later query variables using the `value()` method to extract their current value:

```kotlin
val DialogStyle by ComponentStyle {
  base { Modifier.width(dialogWidth.value()) }
}
```

You can also provide a fallback value, which, if present, would be used in the case that a variable hadn't already been
set previously:

```kotlin
val DialogStyle by ComponentStyle {
  // Will be the value of the dialogWidth variable if it was set, otherwise 500px
  base { Modifier.width(dialogWidth.value(500.px)) }
}
```

Additionally, you can also provide a default fallback value when declaring the variable:

```kotlin
val dialogWidth by StyleVariable<CSSLengthNumericValue>(100.px)

// This style will be applied to a div that wraps the whole page
val DialogStyle100 by ComponentStyle {
  // Uses default fallback. width = 100px
  base { Modifier.width(dialogWidth.value()) }
}
val DialogStyle200 by ComponentStyle {
  // Uses specific fallback. width = 200px
  base { Modifier.width(dialogWidth.value(200.px)) } // Uses fallback = 200.px
}
val DialogStyle300 by ComponentStyle {
  // Fallback ignored because variable is set explicitly. width = 300px
  base { Modifier.setVariable(dialogWidth, 300.px).width(dialogWidth.value(400.px)) }
}
```

> [!CAUTION]
> In the above example in the `DialogStyle300` style, we set a variable and query it in the same line, which we did
> purely for demonstration purposes. In practice, you would probably never do this -- the variable would have been set
> separately elsewhere.

To demonstrate these concepts all together, below we declare a background color variable, create a root container scope
which sets it, a child style that uses it, and, finally, a child style variant that overrides it:

```kotlin
// Default to a debug color, so if we see it, it indicates we forgot to set it later
val bgColor by StyleVariable<CSSColorValue>(Colors.Magenta)

val ContainerStyle by ComponentStyle {
    base { Modifier.setVariable(bgColor, Colors.Blue) }
}
val SquareStyle by ComponentStyle {
    base { Modifier.size(100.px).backgroundColor(bgColor.value()) }
}
val RedSquareVariant by SquareStyle.addVariant {
    base { Modifier.setVariable(bgColor, Colors.Red) }
}
```

The following code brings the above styles together (and in some cases uses inline styles to override the background
color further):

```kotlin
@Composable
fun ColoredSquares() {
    Box(ContainerStyle.toModifier()) {
        Column {
            Row {
                // 1: Read color from ancestor's component style
                Box(SquareStyle.toModifier())
                // 2: Override color via variant
                Box(SquareStyle.toModifier(RedSquareVariant))
            }
            Row {
                // 3: Override color via inline styles
                Box(SquareStyle.toModifier().setVariable(bgColor, Colors.Green))
                Span(Modifier.setVariable(bgColor, Colors.Yellow).toAttrs()) {
                    // 4: Read color from parent's inline style
                    Box(SquareStyle.toModifier())
                }
            }
        }
    }
}
```

The above renders the following output:

![Kobweb CSS Variables, Squares example](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-variable-squares-example.png)

---

You can also set CSS variables directly from code if you have access to the backing HTML element. Below, we use the
`ref` callback to get the backing element for a fullscreen `Box` and then use a `Button` to set it to a random color
from the colors of the rainbow:

```kotlin
// We specify the initial color of the rainbow here, since the variable
// won't otherwise be set until the user clicks a button.
val bgColor by StyleVariable<CSSColorValue>(Colors.Red)

val ScreenStyle by ComponentStyle {
    base {
        Modifier.fillMaxSize().backgroundColor(bgColor.value())
    }
}

@Page
@Composable
fun RainbowBackground() {
    val roygbiv = listOf(Colors.Red, /*...*/ Colors.Violet)

    var screenElement: HTMLElement? by remember { mutableStateOf(null) }
    Box(ScreenStyle.toModifier(), ref = ref { screenElement = it }) {
        Button(onClick = {
            // You can call `setVariable` on the backing HTML element to set the variable value directly
            screenElement!!.setVariable(bgColor, roygbiv.random())
        }) {
            Text("Click me")
        }
    }
}
```

The above results in the following UI:

![Kobweb CSS Variables, Rainbow example](https://github.com/varabyte/media/raw/main/kobweb/screencasts/kobweb-variable-roygbiv-example.gif)

#### In most cases, don't use CSS Variables

Most of the time, you can actually get away with not using CSS Variables! Your Kotlin code is often a more natural place
to describe dynamic behavior than HTML / CSS is.

Let's revisit the "colored squares" example from above. Note it's much easier to read if we don't try to use variables
at all.

```kotlin
val SquareStyle by ComponentStyle {
    base { Modifier.size(100.px) }
}

@Composable
fun ColoredSquares() {
    Column {
        Row {
            Box(SquareStyle.toModifier().backgroundColor(Colors.Blue))
            Box(SquareStyle.toModifier().backgroundColor(Colors.Red))
        }
        Row {
            Box(SquareStyle.toModifier().backgroundColor(Colors.Green))
            Box(SquareStyle.toModifier().backgroundColor(Colors.Yellow))
        }
    }
}
```

And the "rainbow background" example is similarly easier to read by using Kotlin variables
(i.e. `var someValue by remember { mutableStateOf(...) }`) instead of CSS variables:

```kotlin
val ScreenStyle by ComponentStyle {
    base { Modifier.fillMaxSize() }
}

@Page
@Composable
fun RainbowBackground() {
    val roygbiv = listOf(Colors.Red, /*...*/ Colors.Violet)

    var currColor by remember { mutableStateOf(Colors.Red) }
    Box(ScreenStyle.toModifier().backgroundColor(currColor)) {
        Button(onClick = { currColor = roygbiv.random() }) {
            Text("Click me")
        }
    }
}
```

Even though you should rarely need CSS variables, there may be occasions where they can be a useful tool in your
toolbox. The above examples were artificial scenarios used as a way to show off CSS variables in relatively isolated
environments. But here are some situations that might benefit from CSS variables:

* You have a site which allows users to choose from a list of several themes (e.g. primary and secondary colors). It
  would be trivial enough to add CSS variables for `themePrimary` and `themeSecondary` (applied at the site's root)
  which you can then reference throughout your styles.
* You need more control for colors in your theming than can be provided for by the simple light / dark color mode. For
  example, Wordle has light / dark + normal / contrast modes.
* You want to create a widget which dynamically changes its behavior based on the context it is added within. For
  example, maybe your site has a dark area and a light area, and the widget should use white outlines in the dark area
  and black outlines in the light. This can be accomplished by exposing an outline color variable, which each area of
  your site is responsible for setting.
* You want to allow the user to tweak values within a pseudo-class selector (e.g. hover, focus, active) for some
  widget (e.g. color or border size), which is much easier to do using variables than listening to events and setting
  inline styles.
* You have a widget that you ended up creating a bunch of variants for, but instead you realize you could replace them
  all with one or two CSS variables.

When in doubt, lean on Kotlin for handling dynamic behavior, and occasionally consider using style variables if you feel
doing so would clean up the code.

### Font Awesome

Kobweb provides the `silk-icons-fa` artifact which you can use in your project if you want access to all the free
Font Awesome (v6) icons.

Using it is easy! Search the [Font Awesome gallery](https://fontawesome.com/search?o=r&m=free), choose an
icon, and then call it using the associated Font Awesome icon composable.

For example, if I wanted to add the Kobweb-themed
[spider icon](https://fontawesome.com/icons/spider?s=solid&f=classic), I could call this in my Kobweb code:

```kotlin
FaSpider()
```

That's it!

Some icons have a choice between solid and outline versions, such as "Square"
([outline](https://fontawesome.com/icons/square?s=solid&f=classic) and
[filled](https://fontawesome.com/icons/square?s=regular&f=classic)). In that case, the default choice will be an outline
mode, but you can pass in a style enum to control this:

```kotlin
FaSquare(style = IconStyle.FILLED)
```

All Font Awesome composables accept a modifier parameter, so you can tweak it further:

```kotlin
FaSpider(Modifier.color(Colors.Red))
```

> [!NOTE]
> When you create a project using our `app` template, Font Awesome icons are included.

### Material Design Icons

Kobweb provides the `kobweb-silk-icons-mdi` artifact which you can use in your project if you want access to all the
free Material Design icons.

Using it is easy! Search the [Material Icons gallery](https://fonts.google.com/icons?icon.set=Material+Icons), choose an
icon, and then call it using the associated Material Design Icon composable.

For example, let's say after a search I found and wanted to use their
[bug report icon](https://fonts.google.com/icons?icon.set=Material+Icons&icon.query=bug+report), I could call this in my
Kobweb code by converting the name to camel case:

```kotlin
MdiBugReport()
```

That's it!

Most material design icons support multiple styles: outlined, filled, rounded, sharp, and two-tone. Check the gallery
search link above to verify what styles are supported by your icon. You can identify the one you want to use by passing
it into the method's `style` parameter:

```kotlin
MdiLightMode(style = IconStyle.TWO_TONE)
```

All Material Design Icon composables accept a modifier parameter, so you can tweak it further:

```kotlin
MdiError(Modifier.color(Colors.Red))
```

<!-- Some template READMEs link to this section from before I simplified its name, so adding a span here so they can still find it. -->

## <span id="components-layouts-sections-and-widgets">Layouts, Sections, and Widgets</span>

Outside of pages, it is common to create reusable, composable parts. While Kobweb doesn't enforce any particular rule
here, we recommend a convention that, if followed, may make it easier to allow new readers of your codebase to get
around.

First, as a sibling to pages, create a folder called **components**. Within it, add:

* **layouts** - High-level composables that provide entire page layouts. Most (all?) of your `@Page` pages will start by
  calling a page layout function first. It's possible that you will only need a single layout for your entire site.
* **sections** - Medium-level composables that represent compound areas inside your pages, organizing a collection of
  many children composables. If you have multiple layouts, it's likely sections would be shared across them. For
  example, nav headers and footers are great candidates for this subfolder.
* **widgets** - Low-level composables. Focused UI pieces that you may want to reuse all around your site. For example,
  a stylized visitor counter would be a good candidate for this subfolder.

## Markdown

If you create a markdown file under the `jsMain/resources/markdown` folder, a corresponding page will be created for you
at build time, using the filename as its path.

For example, if I create the following file:

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.kt

# Kobweb Tutorial

...
```

this will create a page that I can then visit by going to `mysite.com/docs/tutorial/kobweb`

### Front Matter

Front Matter is metadata that you can specify at the beginning of your document, like so:

```markdown
---
title: Tutorial
author: bitspittle
---

...
```

In a following section, we'll discuss how to embed code in your markdown, but for now, know that these key / value pairs
can be queried in code using the page's context:

```kotlin
@Composable
fun AuthorWidget() {
  val ctx = rememberPageContext()
  // Note: You can use `markdown!!` only if you're sure that
  // this composable is called while inside a page generated
  // from Markdown.
  val author = ctx.markdown!!.frontMatter.getValue("author").single()
  Text("Article by $author")
}
```

> [!IMPORTANT]
> If you're not seeing `ctx.markdown` autocomplete, you need to make sure you depend on the
> `com.varabyte.kobwebx:kobwebx-markdown` artifact in your project's `build.gradle`.

#### Root

Within your front matter, there's a special value which, if set, will be used to render a root `@Composable` that wraps
the code your markdown file would otherwise create. This is useful for specifying a layout for example:

```markdown
---
root: .components.layout.DocsLayout
---

# Kobweb Tutorial
```

The above will generate code like the following:

```kotlin
import com.mysite.components.layout.DocsLayout

@Composable
@Page
fun KobwebPage() {
  DocsLayout {
    H1 {
      Text("Kobweb Tutorial")
    }
  }
}
```

#### Route Override

Kobweb Markdown front matter supports a `routeOverride` key. If present, its value will be passed into the
generated `@Page` annotation (see the [Route Override section▲](#route-override) for valid values here).

This allows you to give your URL a name that normal Kotlin filename rules don't allow for, such as a hyphen:

`# AStarDemo.md`

```markdown
---
routeOverride: a-star-demo
---
```

The above will generate code like the following:

```kotlin
@Composable
@Page("a-star-demo")
fun AStarDemoPage() { /* ... */
}
```

You can additionally override the algorithm used for converting ALL markdown files to their final name, by setting the
markdown block's `routeOverride` callback:

```kotlin
kobweb {
  markdown { //
    // Given "Example.md", name will be "Example" and output will be "post_example"
    routeOverride.set { name -> "post_${name.lowercase()}" }
  }
}
```

This callback will be triggered on all Markdown pages *except* `Index.md` files.

Some common algorithms are provided which you can use instead of writing your own:

```kotlin
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock.RouteOverride

kobweb {
  markdown {
    routeOverride.set(RouteOverride.KebabCase) // e.g. "ExamplePage" to "example-page"
  }
}
```

If you specify both a global route override and a local route override in the front matter, the front matter setting
will take precedence.

### Kobweb Call

The power of Kotlin + Compose HTML is interactive components, not static text! Therefore, Kobweb Markdown support
enables special syntax that can be used to insert Kotlin code.

#### Block syntax

Usually, you will define widgets that belong in their own section. Just use three triple-curly braces to insert a
function that lives in its own block:

```markdown
# Kobweb Tutorial

...

{{{ .components.widgets.VisitorCounter }}}
```

which will generate code for you like the following:

```kotlin
@Composable
@Page
fun KobwebPage() {
  /* ... */
  com.mysite.components.widgets.VisitorCounter()
}
```

You may have noticed that the code path in the markdown file is prefixed with a `.`. When you do that, the final path
will automatically be prepended with your site's full package.

#### Inline syntax

Occasionally, you may want to insert a smaller widget into the flow of a single sentence. For this case, use the
`${...}` inline syntax:

```markdown
Press ${.components.widgets.ColorButton} to toggle the site's current color.
```

> [!CAUTION]
> Spaces are not allowed within the curly braces! If you have them there, Markdown skips over the whole thing and leaves
> it as text.

### Imports

You may wish to add imports to the code generated from your markdown. Kobweb Markdown supports registering both
*global* imports (imports that will be added to every generated file) and *local* imports (those that will only apply
to a single target file).

#### Global Imports

To register a global import, you configure the `markdown` block in your build script:

```kotlin
// site/build.gradle.kts

kobweb {
  markdown {
    imports.add(".components.widgets.*")
  }
}
```

Notice that you can begin your path with a "." to tell the Kobweb Markdown plugin to prepend your site's package to it.
The above would ensure that every markdown file generated would have the following import:

```kotlin
import com.mysite.components.widgets.*
```

Imports can help you simplify your Kobweb calls. Revisiting an example from just above:

```markdown
# Without imports

Press ${.components.widgets.ColorButton} to toggle the site's current color.

# With imports

Press ${ColorButton} to toggle the site's current color.
```

#### Local Imports

Local imports are specified in your markdown's Front Matter (and can even affect its root declaration!):

```markdown
---
root: DocsLayout
imports:
  - .components.sections.DocsLayout
  - .components.widgets.VisitorCounter
---

...

{{{ VisitorCounter }}}
```

## Learning CSS through Kobweb

Many developers new to web development have heard horror stories about CSS, and they might hope that Kobweb, by
leveraging Kotlin and a Jetpack Compose-inspired API, means they won't have to learn it.

It's worth dispelling that illusion! CSS is inevitable.

That said, CSS's reputation is probably worse than it deserves to be. Many of its features are actually fairly
straightforward and some are quite powerful. For example, you can efficiently declare that your element should be
wrapped with a thin border, with round corners, casting a drop shadow beneath it to give it a feeling of depth,
painted with a gradient effect for its background, and animated with an oscillating, tilting effect.

It's hoped that, once you've learned a bit of CSS through Kobweb, you'll find yourself actually enjoying it (sometimes)!

### Ways Kobweb helps with CSS

Kobweb offers enough of a layer of abstraction that you can learn CSS in a more incremental way.

First and most importantly, Kobweb gives you a Kotlin-idiomatic type-safe API to CSS properties. This is a major
improvement over writing CSS in text files which fail silently at runtime.

Next, layout widgets like `Box`, `Column`, and `Row` can get you up and running quickly with rich, complex layouts
before ever having to understand what a "flex layout" is.

Meanwhile, using `ComponentStyle` can help you break your CSS up into smaller, more manageable
pieces that live close to the code that actually uses them, allowing your project to avoid a giant, monolithic CSS file.
(Such giant CSS files are one of the reasons CSS has an intimidating reputation).

For example, a CSS file that could easily look like this:

```css
/* Dozens of rules... */

.important {
  background-color: red;
  font-weight: bold;
}

.important:hover {
  background-color: pink;
}

/* Dozens of other rules... */

.post-title {
    font-size: 24px;
}

/* A dozen more more rules... */
```

can migrate to this in Kobweb:

```kotlin
//------------------ CriticalInformation.kt

val ImportantStyle by ComponentStyle {
  base {
    Modifier.backgroundColor(Colors.Red).fontWeight(FontWeight.Bold)
  }

  hover {
    Modifier.backgroundColor(Colors.Pink)
  }
}

//------------------ Post.kt

val PostTitleStyle by ComponentStyle.base { Modifier.fontSize(24.px) }
```

Next, Silk provides a `deferRender` method which lets you declare code that won't get rendered until the rest of the
DOM finishes first, meaning it will appear on top of everything else. This is a clean way to avoid setting CSS z-index
values (another aspect of CSS that has a bad reputation).

And finally, Silk aims to provide widgets with default styles that look good for many sites. This means you should be
able to rapidly develop common UIs without running into some of the more complex aspects of CSS.

### A concrete example

Let's walk through an example of layering CSS effects on top of a basic element.

> [!TIP]
> Two of the best learning resources for CSS properties are `https://developer.mozilla.org`
> and `https://www.w3schools.com`. Keep an eye out for these when you do a search.

We'll create the bordered, floating, oscillating element we discussed earlier. Rereading it now, here are the concepts
we need to figure out how to do:

* Create a border
* Round out the corners
* Add a drop shadow
* Add a gradient background
* Add a wobble animation

Let's say we want to create an attention grabbing "welcome" widget
on our site. You can always start with an empty box, which we'll put some text in:

```kotlin
Box(Modifier.padding(topBottom = 5.px, leftRight = 30.px)) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 1 (base)](https://github.com/varabyte/media/raw/main/kobweb/images/css/css-example-step-1.png)

**Create a border**

Next, search the internet for "CSS border". One of the top links should be: https://developer.mozilla.org/en-US/docs/Web/CSS/border

Skim the docs and play around with the interactive examples. With an understanding of the border property now, let's use
code completion to discover the Kobweb version of the API:

```kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 2 (border added)](https://github.com/varabyte/media/raw/main/kobweb/images/css/css-example-step-2.png)

**Round out the corners**

Search for "CSS rounded corners". It turns out the CSS property in this case is called a "border
radius": https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius

```kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 3 (corners rounded)](https://github.com/varabyte/media/raw/main/kobweb/images/css/css-example-step-3.png)

**Add a drop shadow**

Search for "CSS shadow". There are a few types of CSS shadow features, but after some quick reading, we
realize we want to use box shadows: https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow

After playing around with blur and spread values, we get something that looks decent:

```kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 4 (box shadow added)](https://github.com/varabyte/media/raw/main/kobweb/images/css/css-example-step-4.png)

**Add a gradient background**

Search for "CSS gradient background". This isn't a straightforward CSS property like the previous cases, so we instead
get a more general documentation page explaining the feature: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_images/Using_CSS_gradients

This case turns out to be a little trickier to ultimately find the Kotlin, type-safe equivalent, but if you dig a bit
more into the CSS docs, you'll learn that a linear gradient is a type of background image.

```kotlin
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
    .backgroundImage(linearGradient(LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen))
) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 5 (gradient background added)](https://github.com/varabyte/media/raw/main/kobweb/images/css/css-example-step-5.png)

**Add a wobble animation**

And finally, search for "CSS
animations": https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_animations/Using_CSS_animations

You can review the [Animations▲](#animations) section above for a refresher on how Kobweb supports this feature, which
requires declaring a top-level `Keyframes` block which then gets referenced inside an animation modifier:

```kotlin
// Top level property
val WobbleKeyframes by Keyframes {
  from { Modifier.rotate((-5).deg) }
  to { Modifier.rotate(5.deg) }
}

// Inside your @Page composable
Box(
  Modifier
    .padding(topBottom = 5.px, leftRight = 30.px)
    .border(1.px, LineStyle.Solid, Colors.Black)
    .borderRadius(5.px)
    .boxShadow(blurRadius = 5.px, spreadRadius = 3.px, color = Colors.DarkGray)
    .backgroundImage(linearGradient(LinearGradient.Direction.ToRight, Colors.LightBlue, Colors.LightGreen))
    .animation(
      WobbleKeyframes.toAnimation(
        duration = 1.s,
        iterationCount = AnimationIterationCount.Infinite,
        timingFunction = AnimationTimingFunction.EaseInOut,
        direction = AnimationDirection.Alternate,
      )
    )
) {
  Text("WELCOME!!")
}
```

![Learning CSS in Kobweb, Step 6 (wobble animation added)](https://github.com/varabyte/media/raw/main/kobweb/screencasts/css/css-example-step-6.gif)

**And we're done!**

The above element isn't going to win any style awards, but I hope this demonstrates how much power CSS can give you in
just a few declarative lines of code. And thanks to the nature of CSS, combined with Kobweb's live reloading experience,
we were able to experiment with our idea incrementally.

### CSS 2 Kobweb

One of our main project contributors created a site called [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/)
which aims to simplify the process of converting CSS examples to equivalent Kobweb ComponentStyle and/or Modifier
declarations.

![CSS 2 Kobweb example](https://github.com/varabyte/media/raw/main/kobweb/images/css/css2kobweb.png)

> [!TIP]
> [CSS 2 Kobweb](https://opletter.github.io/css2kobweb/) also supports specifying class name selectors and keyframes.
> For example, see what happens when you paste in the following CSS code:
> ```css
> .site-banner {
>   display: relative;
>   padding-left: 10px;
>   padding-top: 5%;
>   animation: slide-in 3s linear 1s infinite;
>   background-position: bottom 10px right;
>   background-image: linear-gradient(to bottom, #eeeeee, white 25px);
> }
> .site-banner:hover {
>   color: rgb(40, 40, 40);
> }
> @keyframes slide-in {
>   from {
>     transform: translateX(-2rem) scale(0.5);
>   }
>   to {
>     transform: translateX(0);
>     opacity: 1;
>   }
> }
> ```

The web is full of examples of interesting CSS effects. Almost any CSS-related search will result in tons of
StackOverflow answers, interactive playgrounds featuring WYSIWYG editors, and blog posts. Many of these introduce some
really novel CSS examples. This is a great way to learn more about web development!

However, as the previous section demonstrated, it can sometimes be a pain to go from a CSS example to the equivalent
Kobweb code. We hope that *CSS 2 Kobweb* can help with that.

This project is already very useful, but it's still early days. If you find cases of *CSS 2 Kobweb* that are
incorrect, please consider [filing an issue](https://github.com/opLetter/css2kobweb/issues) in their repository.

### Still stuck?

Hopefully this section gave you insight into how you can explore CSS APIs on your own, but if you're stuck on getting
an effect working, remember you can reach out to one of the options in the [connecting with us▼](#connecting-with-us)
section, and someone in the community can probably help!

# Intermediate topics

## Specifying your application root

By default, Kobweb will automatically root every page to the [`KobwebApp` composable](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-core/src/jsMain/kotlin/com/varabyte/kobweb/core/App.kt)
(or, if using Silk, to a [`SilkApp` composable](https://github.com/varabyte/kobweb/blob/main/frontend/kobweb-silk/src/jsMain/kotlin/com/varabyte/kobweb/silk/SilkApp.kt)).
These perform some minimal common work (e.g. applying CSS styles) that should be present across your whole site.

This means if you register a page:

```kotlin
// jsMain/kotlin/com/mysite/pages/Index.kt

@Page
@Composable
fun HomePage() {
    /* ... */
}
```

then the final result that actually runs on your site will be:

```kotlin
// In a generated main.kt somewhere...

KobwebApp {
  HomePage()
}
```

It is likely you'll want to configure this further for your own application. Perhaps you have some initialization logic
that you'd like to run before any page gets run (like logic for updating saved settings into local storage). And for
many apps it's a great place to specify a full screen Silk `Surface` as that makes all children beneath it transition
between light and dark colors smoothly.

In this case, you can create your own root composable and annotate it with `@App`. If present, Kobweb will use that
instead of its own default. You should, of course, delegate to `KobwebApp` (or `SilkApp` if using Silk), as the
initialization logic from those methods should still be run.

Here's an example application composable override that I use in many of my own projects:

```kotlin
@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
  SilkApp {
    val colorMode = ColorMode.current
    LaunchedEffect(colorMode) { // Relaunched every time the color mode changes
      localStorage.setItem("color-mode", colorMode.name)
    }

    // A full screen Silk surface. Sets the background based on Silk's palette and animates color changes.
    Surface(SmoothColorStyle.toModifier().minHeight(100.vh)) {
      content()
    }
  }
}
```

You can define *at most* a single `@App` on your site, or else the Kobweb Application plugin will complain at build
time.

## Updating default HTML styles with Silk

The default styles picked by browsers for many HTML elements rarely fit most site designs, and it's likely you'll want
to tweak at least some of them. A very common example of this is the default web font, which if left as is will make
your site look a bit archaic.

Most traditional sites overwrite styles by creating a CSS stylesheet and then linking to it in their HTML. However, if
you are using Silk in your Kobweb application, you can use an approach very similar to `ComponentStyle` discussed above
but for general HTML elements.

To do this, create an `@InitSilk` method. The context parameter includes a `stylesheet` property that represents the CSS
stylesheet for your site, providing a Silk-idiomatic API for adding CSS rules to it.

Below is a simple example that sets the whole site to more aesthetically pleasing fonts than the browser defaults, one
for regular text and one for code:

```kotlin
@InitSilk
fun initSilk(ctx: InitSilkContext) {
  ctx.stylesheet.registerStyleBase("body") {
    Modifier.fontFamily("Ubuntu", "Roboto", "Arial", "Helvetica", "sans-serif")
      .fontSize(18.px)
      .lineHeight(1.5)
  }

  ctx.stylesheet.registerStyleBase("code") {
    Modifier.fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
  }
}
```

> [!TIP]
> The `registerStyleBase` method is commonly used for registering styles with minimal code, but you can also use
> `registerStyle`, especially if you want to add some support for one or more psuedo-classes (
> e.g. `hover`, `focus`, `active`):
>
> ```kotlin
> ctx.stylesheet.registerStyle("code") {
>   base {
>     Modifier
>       .fontFamily("Ubuntu Mono", "Roboto Mono", "Lucida Console", "Courier New", "monospace")
>       .userSelect(UserSelect.None) // No copying code allowed!
>   }
>   hover {
>     Modifier.cursor(Cursor.NotAllowed)
>   }
> } 
> ``` 

## Setting application globals

Occasionally you might find yourself with a value at build time that you want your site to know at runtime.

For example, maybe you want to specify a version based on the current UTC timestamp. Or maybe you want to read a system
environment variable's value and pass that into your Kobweb site as a way to configure its behavior.

This is supported via Kobweb's `AppGlobals` singleton, which is like a `Map<String, String>` whose values you can set
from your project's build script using the `kobweb.app.globals` property.

Let's demonstrate this with the UTC version example.

In your application's `build.gradle.kts`, add the following code:

```kotlin
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
  /* ... */
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    globals.put(
      "version",
      LocalDateTime
          .now(ZoneId.of("UTC"))
          .format(DateTimeFormatter.ofPattern("yyyyMMdd.kkmm"))
    )
  }
}
```

You can then access them via the `AppGlobals.get` or `AppGlobals.getValue` methods:

```kotlin
val version = AppGlobals.getValue("version")
```

In your Kotlin project somewhere, it is recommended that you either add some type-safe extension methods, or you can
create your own wrapper object (based on your preference):

```kotlin
// SiteGlobals.kt

import com.varabyte.kobweb.core.AppGlobals

// Extension method approach ---------------------

val AppGlobals.version: String
  get() = getValue("version")

// Wrapper object approach -----------------------

object SiteGlobals {
  val version: String = AppGlobals.getValue("version")
}
```

At this point, you can access this value in your site's code, say for a tiny label that would look good in a footer
perhaps:

```kotlin
// components/widgets/SiteVersion.kt

val VersionTextStyle by ComponentStyle.base {
  Modifier.fontSize(0.6.cssRem)
}

@Composable
fun SiteVersion(modifier: Modifier = Modifier) {
  SpanText("v" + SiteGlobals.version, VersionTextStyle.toModifier().then(modifier))
}
```

## Globally replacing Silk widget styles

Silk widgets all use [component styles▲](#componentstyle) to power their look and feel.

Normally, if you want to tweak a style in select locations within your site, you just create a variant from that style:

```kotlin
val TweakedButtonStyle by ButtonStyle.addVariantBase { /* ... */ }

// Later...
Button(variant = TweakedButtonStyle) { /* ... */ }
```

But what if you want to globally change the look and feel of a widget across your entire site?

You could of course create your own composable which wraps some underlying composable with its own new style, e.g.
`MyButton` which defines its own `MyButtonStyle` that internally delegates to `Button`. However, you'd have to be
careful to make sure all new developers who add code to your site know to use `MyButton` instead of `Button` directly.

Silk provides another way, allowing you to modify any of its declared styles and/or variants in place.

You can do this via an `@InitSilk` method, which takes an `InitSilkContext` parameter. This context provides the `theme`
property, which provides the following family of methods for rewriting styles and variants:

```kotlin
@InitSilk
fun replaceStylesAndOrVariants(ctx: InitSilkContext) {
  ctx.theme.replaceComponentStyle(SomeStyle) { /* ... */ }
  ctx.theme.replaceComponentVariant(SomeStyle) { /* ... */ }
  ctx.theme.modifyComponentStyle(SomeStyle) { /* ... */ }
  ctx.theme.modifyComponentVariant(SomeStyle) { /* ... */ }
}
```

> [!NOTE]
> Technically, you can use these methods with your own site's declared styles and variants as well, but there should be
> no reason to do so since you can just go to the source and change those values directly. However, this can still be
> useful if you're using a third-party Kobweb library that provides its own styles and/or variants.

Use the `replace` versions if you want to define a whole new set of CSS rules from scratch, or use the `modify` versions
to layer additional changes on top of what's already there.

> [!CAUTION]
> Using `replace` on some of the more complex Silk styles can be tricky, and you may want to familiarize yourself with
> the details of how those widgets are implemented before attempting to do so. Additionally, once you replace a style
> in your site, you will be opting-out of any future improvements to that style that may be made in future versions of
> Silk.

Here's an example of replacing `ImageStyle` on a site that wants to force all images to have rounded corners and
automatically scale down to fit their container:

```kotlin
@InitSilk
fun replaceSilkImageStyle(ctx: InitSilkContext) {
  ctx.theme.replaceComponentStyleBase(ImageStyle) {
    Modifier
      .clip(Rect(cornerRadius = 8.px))
      .fillMaxWidth()
      .objectFit(ObjectFit.ScaleDown)
  }
}
```

and here's an example for a site that always wants its horizontal dividers to fill max width:

```kotlin
@InitSilk
fun makeHorizontalDividersFillWidth(ctx: InitSilkContext) {
  ctx.theme.modifyComponentStyleBase(HorizontalDividerStyle) {
    Modifier.fillMaxWidth()
  }
}
```

## Static layout vs. Full stack sites

There are two flavors of Kobweb sites: *static* and *full stack*.

A *static* site (or, more completely, a *static layout* site) is one where you export a bunch of frontend files (e.g.
`html`, `js`, and public resources) into a single, organized folder that gets served in a direct way by
a [static website hosting provider](https://en.wikipedia.org/wiki/Web_hosting_service#Static_page_hosting). In other
words, the name *static* does not refer to the behavior of your site but rather that of your hosting provider solution.

A *full stack* site is one where you write both the logic that runs on the frontend (i.e. on the user's machine) as well
as the logic that runs on the backend (i.e. on a server somewhere). This custom server must serve requested files (much
like a static web hosting service does) plus it should also define endpoints providing unique functionality tailored to
your site's needs.

> [!NOTE]
> Kobweb supports full stack sites using a non-standard file layout that a Kobweb server knows how to consume. It was
> designed to support a powerful, live-reloading experience during development. This layout is called the "kobweb"
> layout, to emphasize how tightly coupled it is to a Kobweb server.

When Kobweb was first written, it only provided the full stack solution, as being able to write your own server logic
enabled a maximum amount of power and flexibility. The mental model for using Kobweb during this early time was simple
and clear.

However, in practice, most projects didn't need this power. A website can give users a very clean, dynamic experience
simply by writing responsive frontend logic to make it look good, e.g. with animations and delightful user interactions.

Additionally, many "*Feature* as a Service" solutions have popped up over the years, which can provide a ton of
convenient functionality that used to require a custom server. These days, you can easily integrate auth, database, and
analytics solutions all without writing a single line of backend code.

The process for exporting a bunch of files in a way that can be consumed by a static web hosting provider tends to be
*much* faster *and* cheaper than using a full stack solution. Therefore, you should prefer a static site layout unless
you have a specific need for a full stack approach.

Some possible reasons to use a custom server are:
* needing to communicate with other, private backend services in your company.
* intercepting requests as an intermediary for some third-party service where you own a very sensitive API key that you
  don't want to leak (such as a service that delegates to ChatGPT).
* acting as a hub to connect multiple clients together (such as a chat server).

If you aren't sure which category you fall into, then you should probably be creating a static layout site. It's much
easier to migrate from a static layout site to a full stack site later than the other way around.

### Exporting and running

Both site flavors require an export. To export your site with a static layout, use the `kobweb export --layout static`
command, while for full stack the command is `kobweb export --layout kobweb` (or just `kobweb export` since `kobweb` is
the default layout as it originally was the only way).

Once exported, you can test your site by running it locally before uploading. You run a static site with
`kobweb run --env prod --layout static` and a full stack site with `kobweb run --env prod --layout kobweb` (or just
`kobweb run --env prod`).

### Deploying

A static site gets exported into `.kobweb/site` by default (you can configure this location in your `.kobweb/conf.yaml`
file if you'd like). You can then upload the contents of that folder to the static web hosting provider of your choice.

Deploying a full stack site is a bit more complex, as different providers have wildly varying setups, and some users may
even decide to run their own web server themselves. However, when you export your Kobweb site, scripts are generated for
running your server, both for *nix platforms (`.kobweb/server/start.sh`) and the Windows
platform (`.kobweb/server/start.bat`). If the provider you are using speaks Dockerfile, you can set `ENTRYPOINT` to
either of these scripts (depending on the server's platform).

Going in more detail than this is outside the scope of this README. However, you can read my blog posts for a lot more
information and some clear, concrete examples:

* [Static site generation and deployment with Kobweb](https://bitspittle.dev/blog/2022/staticdeploy)
* [Deploying Kobweb into the cloud](https://bitspittle.dev/blog/2023/clouddeploy)

## Communicating with the server

Let's say you've decided on creating a full stack website using Kobweb. This section walks you through setting it up as
well as introducing the various APIs for communicating to the backend from the frontend.

### Declare a full stack project

A Kobweb project will always at least have a JavaScript component, but if you declare a JVM target, that will be used to
define custom server logic that can then be used by your Kobweb site.

It's easiest to let Kobweb do it for you. In your site's build script, make sure you've declared
`configAsKobwebApplication(includeServer = true)`:

```kotlin
// site/build.gradle.kts
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kobweb.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
}

/* ... */

kotlin {
    configAsKobwebApplication(includeServer = true)
    /* ... */
}
```

> [!IMPORTANT]
> `configAsKobwebApplication(includeServer = true)` declares and sets up both `js()` and `jvm()`
> [Kotlin Multiplatform targets](https://kotlinlang.org/docs/multiplatform-set-up-targets.html) for you. If you don't
> set `includeServer = true` explicitly, only the JS target will be declared.

The easy way to check if everything is set up correctly is to open your project inside IntelliJ IDEA, wait for it to
finish indexing, and check that the `jvmMain` folder is detected as a module (if so, it will be given a special icon
and look the same as the `jsMain` folder):

![Kobweb JVM main set up correctly](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-jvm-main.png)

### Define API routes

You can define and annotate methods which will generate server endpoints you can interact with. To add one:

1. Define your method (optionally `suspend`able) in a file somewhere under the `api` package in your `jvmMain` source
   directory.
1. The method should take exactly one argument, an `ApiContext`.
1. Annotate it with `@Api`

For example, here's a simple method that echoes back an argument passed into it:

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

@Api
fun echo(ctx: ApiContext) {
    // ctx.req is for the incoming request, ctx.res for responding back to the client

    // Params are parsed from the URL, e.g. here "/api/echo?message=..."
    val msg = ctx.req.params["message"] ?: ""
    ctx.res.setBodyText(msg)
}
```

After running your project, you can test the endpoint by visiting `mysite.com/api/echo?message=hello`

You can also trigger the endpoint in your frontend code by using the extension `api` property added to the
`kotlinx.browser.window` class:

```kotlin
@Page
@Composable
fun ApiDemoPage() {
  val coroutineScope = rememberCoroutineScope()

  Button(onClick = {
    coroutineScope.launch {
      println("Echoed: " + window.api.get("echo?message=hello").decodeToString())
    }
  }) { Text("Click me") }
}
```

All the HTTP methods are supported (`post`, `put`, etc.).

These methods will throw an exception if the request fails for any reason. Note that for every HTTP method, there's a
corresponding "try" version that will return null instead (`tryPost`, `tryPut`, etc.).

If you know what you're doing, you can of course always use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)
directly.

### `@InitApi` methods and initializing services

Kobweb also supports declaring methods that should be run when your server starts up, which is particularly useful for
initializing services that your `@Api` methods can then use. These methods must be annotated with `@InitApi` and must
take a single `InitApiContext` parameter.

> [!IMPORTANT]
> If you are running a development server and change any of your backend code, causing a live reloading event, the
> init methods will be run again.

The `InitApiContext` class exposes a mutable set property (called `data`) which you can put anything into. Meanwhile,
`@Api` methods expose an immutable version of `data`. This allows you to initialize a service in an `@InitApi` method
and then access it in your `@Api` methods.

Let's demonstrate a concrete example, imagining we had an interface called `Database` with a mutable
subclass `MutableDatabase` that implements it and provides additional APIs for mutating the database.

The skeleton for registering and later querying such a database instance might look like this:

```kotlin
@InitApi
fun initDatabase(ctx: InitApiContext) {
  val db = MutableDatabase()
  db.createTable("users", listOf("id", "name")).apply {
    addRow(listOf("1", "Alice"))
    addRow(listOf("2", "Bob"))
  }
  db.loadResource("products.csv")

  ctx.data.add<Database>(db)
}

@Api
fun getUsers(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.GET) return
  val db = ctx.data.get<Database>()
  ctx.res.setBodyText(db.query("SELECT * FROM users").toString())
}
```

### Define API streams

Kobweb servers also support persistent connections via streams. Streams are essentially named channels that maintain
continuous contact between the client and the server, allowing either to send messages to the other at any time. This is
especially useful if you want your server to be able to communicate updates to your client without needing to poll.

Additionally, multiple clients can connect to the same stream. In this case, the server can choose to not only send a
message back to your client, but also to broadcast messages to all users (or a filtered subset of users) on the same
stream. You could use this, for example, to implement a chat server with rooms.

#### Example API stream

Like API routes, API streams must be defined under the `api` package in your `jvmMain` source directory. By default, the
name of the stream will be derived from the file name and path that it's declared in (e.g. "api/lobby/Chat.kt" will
create a channel named "lobby/chat").

Unlike API routes, API streams are defined as properties, not methods. This is because API streams need to be a bit more
flexible than routes, since streams consist of multiple distinct events: client connection, client messages, and
client disconnection.

Also unlike API routes, streams do not have to be annotated. The Kobweb Application plugin can automatically detect
them.

For example, here's a simple stream, declared on the backend, that echoes back any argument it receives:

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

val echo = object : ApiStream {
  override suspend fun onClientConnected(ctx: ClientConnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that ctx.clientId connected
    // Optional: Update ctx.data here, initializing data associated with ctx.clientId
  }
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
  override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that ctx.clientId disconnected
    // Optional: Update ctx.data here, removing data associated with ctx.clientId
  }
}
```

To communicate with an API stream from your site, you need to create a stream connection on the client:

```kotlin
@Page
@Composable
fun ApiStreamDemoPage() {
  val echoStream = rememberApiStream("echo", object : ApiStreamListener {
    override fun onConnected(ctx: ConnectedContext) {}
    override fun onTextReceived(ctx: TextReceivedContext) {
      console.log("Echoed: ${ctx.text}")
    }
    override fun onDisconnected(ctx: DisconnectedContext) {}
  })

  Button(onClick = {
    echoStream.send("hello!")
  }) { Text("Click me") }
}
```

After running your project, you can click on the button and check the console logs. If everything is working properly,
you should see "Echoed: hello!" for each time you press the button.

> [!TIP]
> The `examples/chat` template project uses API streams to implement a very simple chat application, so you can
> reference that project for a more realistic example.

#### API stream conveniences

The above example was intentionally verbose, to showcase the broader functionality around API streams. However,
depending on your use-case, you can elide a fair bit of boilerplate.

First of all, the connect and disconnect handlers are optional, so you can omit them if you don't need them. Let's
simplify the echo example:

```kotlin
// Backend
val echo = object : ApiStream {
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
}

// Frontend
val echoStream = rememberApiStream("echo", object : ApiStreamListener {
  override fun onTextReceived(ctx: TextReceivedContext) {
    console.log("Echoed: ${ctx.text}")
  }
})
```

Additionally, if you only care about the text event, there are convenience methods for that:

```kotlin
// Backend
val echo = ApiStream { ctx -> ctx.stream.send(ctx.text) }

// Frontend
val echoStream = rememberApiStream("echo") {
  ctx -> console.log("Echoed: ${ctx.text}") 
}
```

In practice, your API streams will probably be a bit more involved than the echo example above, but it's nice to know
that you can handle some cases only needing a one-liner on the server and another on the client to create a persistent
client-server connection!

> [!NOTE]
> If you need to create an API stream with stricter control around when it actually connects to the server, you can
> create the `ApiStream` object directly instead of using `rememberApiStream`:
> ```kotlin
> val echoStream = remember { ApiStream("echo") }
> val scope = rememberCoroutineScope()
>
> // Later, perhaps after a button is clicked...
> scope.launch {
>   echoStream.connect(object : ApiStreamListener { /* ... */ })
> }
> ```

### API routes vs. API streams

When faced with a choice, use API routes as often as you can. They are conceptually simpler, and you can query API
endpoints with a CLI program like curl and sometimes even visit the URL directly in your browser. They are great for
handling queries of or updates to server resources in response to user-driven actions (like visiting a page or clicking
on a button). Every operation you perform returns a clear response code in addition to some payload information.

Meanwhile, API streams are very flexible and can be a natural choice to handle high-frequency communication. But they
are also more complex. Unlike a simple request / response pattern, you are instead opting in to manage a potentially
long lifetime during which you can receive any number of events. You may have to concern yourself about interactions
between all the clients on the stream as well. API streams are fundamentally stateful.

You often need to make a lot of decisions when using API streams. What should you do if a client or server disconnects
earlier than expected? How do you want to communicate to the client that their last action succeeded or failed (and you
need to be clear about exactly which action because they might have sent another one in the meantime)? What structure do
you want to enforce, if any, between a client and server connection where both sides can send messages to each other at
any time?

Most importantly, API streams may not horizontally scale as well as API routes. At some point, you may find yourself in
a situation where a new web server is spun up to handle some intense load.

If you're using API routes, you're already probably delegating to a database service as your data backend, so this may
just work seamlessly.

But for API streams, you many naturally find yourself writing a bunch of broadcasting code. However, this only works to
communicate between all clients that are connected to the same server. Two clients connected to the same stream on
different servers are effectively in different, disconnected worlds.

The above situation is often handled by using a pubsub service (like Redis). This feels somewhat equivalent to using a
database as a service in the API route situation, but this code might not be as straightforward to migrate.

API routes and API streams are not a you-must-use-one-or-the-other situation. Your project can use both! In general, try
to imagine the case where a new server might get spun up, and design your code to handle that situation gracefully. API
routes are generally safe to use, so use them often. However, if you have a situation where you need to communicate
events in real-time, especially situations where you want your client to be continuously directed what to do by the
server via events, API streams are a great choice.

> [!NOTE]
> You can also search online about REST vs WebSockets, as these are the technologies that API routes and API streams are
> implemented with. Any discussions about them should apply here as well.

# Advanced topics

## Setting your site's route prefix

Typically, sites live at the top level. This means if you have a root file `index.html` and your site is hosted at
the domain `https://mysite.com` then that HTML file can be accessed by visiting `https://mysite.com/index.html`.

However, in some cases your site may be hosted under a subfolder, such as `https://example.com/products/myproduct/`, in which case your site's root `index.html` file would live at `https://example.com/products/myproduct/index.html`.

Kobweb needs to know about this subfolder structure so that it takes them into account in its routing logic. This can be
specified in your project's `.kobweb/conf.yaml` file with the `routePrefix` value under the `site` section:

```yaml
site:
  title: "..."
  routePrefix: "..."
```

where the value of `routePrefix` is the part between the origin part of the URL and your site's root. For example, if
your site is rooted at `https://example.com/products/myproduct/`, then the value of `routePrefix` would be `products/myproduct`.

> [!NOTE]
> If you are planning to host your site on GitHub Pages using the default `github.io` domain, you will need to set an
> appropriate `routePrefix` value. For a concrete example of setting `routePrefix` for GitHub Pages,
> [check out this relevant section](https://bitspittle.dev/blog/2022/staticdeploy#github-pages) from my blog post about
> exporting static layout sites.

Outside of setting your `routePrefix` in the `conf.yaml` file, you can design your site without explicitly mentioning
it, as Kobweb's composables handle it for you. For example, `Link("docs/manuals/v123.pdf")` (or `Anchor` if you're not
using Silk) will automatically resolve to `https://example.com/products/myproduct/docs/manuals/v123.pdf`.

If you do need to access the route prefix in your own code, you can do so by referencing the `RoutePrefix.value`
companion property or by utilizing the `RoutePrefix.prepend(...)` companion method. This should rarely be required in
practice, however.

> [!NOTE]
> This note is included for anyone who wants to better understand the reason for this feature's design in Kobweb.
>
> Normally, websites are flexible enough to be hosted under any subfolder because they can use relative paths.
> For example, if you are on page `a/b/c` and you need to get to `e/f/g`, you would set your link to `../../../e/f/g`
> and not `/e/f/g`. In this setup, if you referenced an icon in your top-level `index.html` file, you would use
> `favicon.ico` (a relative path to a sibling file) and not `/favicon.ico`.
>
> However, Kobweb is built on top of a reactive framework (Compose HTML) normally meant for SPAs (Single Page
> Applications). In this setup, the same code is used to handle any URL in your site, at which point it intercepts the
> URL value and renders the associated content using some giant switch case in its routing logic.
>
> This means that Kobweb has a single, global `index.html` which is used for all pages on the site. (If you look at it,
> you'll mainly see a minimal DOM skeleton that acts as a container for your dynamically generated pages, plus a script
> link to the code of your site.)
>
> Now, imagine if this `index.html` linked to `favicon.ico`, as in the relative path case. If you were to
> visit `https://example.com/products/myproduct/a/b/c`, you would ultimately get served the global `index.html`
> file that would then look for the icon file at `https://example.com/products/myproduct/a/b/c/favicon.ico`, where
> it doesn't exist. In other words, Kobweb needs help to know that the icon file it is looking for is always at
> `https://example.com/products/myproduct/favicon.ico`, so that the `index.html` file link is valid in whatever
> context it is served.
>
> If you were creating your site the traditional way, you might have one hand-crafted HTML file per page, where each one
> would have its own unique link to `favicon.ico`. Perhaps the rooted `index.html` file would use `favicon.ico` and the
> one located under `a/b/c` would use `../../../favicon.ico`.
>
> But, in Kobweb, since we're using the same `index.html` file for every page, we use the absolute path `/favicon.ico`,
> or, in the case of a route prefix being set, `/${routePrefix}/favicon.ico`.

## Splitting Kobweb code across multiple modules

For simplicity, new projects can choose to put all their pages and widgets inside a single application module, e.g.
`site/`.

However, you can define components and/or pages in separate modules and apply the `com.varabyte.kobweb.library` plugin
on them (in contrast to your main module which applies the `com.varabyte.kobweb.application` plugin.)

In other words, you can split up and organize your project like this:

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
    ├── .kobweb/conf.yaml
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

## Generating site code at compile time

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
  commonMain.dependencies { /* ... */ }
  jsMain {
    kotlin.srcDir(generateCodeTask) // <----- Set your task here
    dependencies { /* ... */ }
  }
}
```

If you want to see this working in action, you
can [check out my blog site's build script here](https://github.com/bitspittle/bitspittle.dev/blob/f7208543046e25337e73b5ede07ff576623962b0/site/build.gradle.kts#L104),
where it is used to generate a listing page for all blog posts.

## Adding Kobweb to an existing project

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

## Exporting your site in a GitHub workflow

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
  change the version of the browser downloaded, will use a new cache bucket (allowing GitHub to eventually clean up the old
  one).
* ***Upload site***: This action uploads the exported site as an artifact. You can then download the artifact from the
  workflow summary page. Your own workflow will likely delete this action and do something else here, like upload to a
  web server (or some location accessible by your web server) or copy files over into a `gh_pages` repository. I've
  included this here (and set the retention days very low) just so you can verify that the workflow is working for your
  project.

For a simple site, the above workflow should take about 2 minutes to run.

## Arithmetic for `StyleVariable`s using `calc`

`StyleVariable`s work in a subtle way that is usually fine until it isn't -- which is often when you try to interact
with their values instead of just passing them around.

Specifically, this would compile but be a problem at runtime:

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// later...

// Border opacity should be more opaque than the rest of the widget
val borderOpacity = max(1.0, MyOpacityVar.value().toDouble() * 2)
```

To see what the problem is, let's first take a step back. The following code:

```kotlin
val MyOpacityVar by StyleVariable<Number>()

// later...
Modifier.opacity(MyOpacityVar.value())
```

generates the following CSS:

```css
opacity: var(--my-opacity);
```

However, `MyOpacityVar` acts like a `Number` in our code! How does something that effectively has a type of `Number`
generate text output like `var(--my-opacity)`?

This is accomplished through the use of Kotlin/JS's `unsafeCast`, where you can tell the compiler to treat a value as a
different type than it actually is. In this case, `MyOpacityVar.value()` returns some object which the Kotlin compiler
*treats* like a `Number` for compilation purposes, but it is really some class instance whose `toString()` evaluates to
`var(--my-opacity)`.

Therefore, `Modifier.opacity(MyOpacityVar.value())` works seemingly like magic! However, if you try to do some
arithmetic, like `MyOpacityVar.value().toDouble() * 0.5`, the compiler might be happy, but things will break silently at
runtime, when the JS engine is asked to do math on something that's not really a number.

In CSS, doing math with variables is accomplished by using `calc` blocks, so Kobweb offers its own `calc` method to
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

Working with variables representing length values don't require calc blocks because Compose HTML supports mathematical
operations on such numeric unit types:

```kotlin
val MyFontSizeVar by StyleVariable<CSSLengthNumericValue>()

MyFontSizeVar.value() + 1.cssRem
// Output: "calc(var(--my-font-size) + 1rem)"
```

However, a calc block could still be useful if you were starting with a raw number that you wanted to convert to a size:

```kotlin
val MyFontSizeScaleFactorVar by StyleVariable<Number>()

calc { MyFontSizeScaleFactorVar.value() * 16.px }
// Output: calc(var(--my-font-size-scale-factor) * 16px)
```

## Kobweb Server Plugins

Many users who create a full stack application generally expect to completely own both the client- and server-side code.

However, being an opinionated framework, Kobweb provides a custom Ktor server in order to deliver some of its features.
For example, it implements the logic for handling [server API routes▲](#define-api-routes) as well as some live reloading
functionality.

It would not be trivial to refactor this behavior into some library that users could import into their own backend
server. As a compromise, some server configuration is exposed by the `.kobweb/conf.yaml` file, and this has been the
main way users could affect the server's behavior.

That said, there will always be some use cases that Kobweb won't anticipate. So as an escape hatch, Kobweb allows users
who know what they're doing to write their own plugins to extend the server.

> [!NOTE]
> The Kobweb Server plugin feature is still fairly new. If you use it, please consider
> [filing issues](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=)
> for any missing features and [connecting with us▼](#connecting-with-us) to share any feedback you have about your
> experience.

Creating a Kobweb server plugin is relatively straightforward. You'll need to:

* Create a new module in your project that produces a JAR file that bundles an implementation of
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

Upon the next Kobweb server run (e.g. via `kobweb run`), if you check the logs, you should see something like this:

```text
[main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
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

## Using your own backend with Kobweb

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
directly, or you can use the convenience `http` property that Kobweb adds to the `window` object which exposes
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

## `CSSNumericValue` typealiases

Kobweb introduces a handful of typealiases for CSS unit values, basing them off of the `CSSNumericValue` class and
extending the set defined by Compose HTML:

```kotlin
typealias CSSAngleNumericValue = CSSNumericValue<out CSSUnitAngle>
typealias CSSLengthOrPercentageNumericValue = CSSNumericValue<out CSSUnitLengthOrPercentage>
typealias CSSLengthNumericValue = CSSNumericValue<out CSSUnitLength>
typealias CSSPercentageNumericValue = CSSNumericValue<out CSSUnitPercentage>
typealias CSSFlexNumericValue = CSSNumericValue<out CSSUnitFlex>
typealias CSSTimeNumericValue = CSSNumericValue<out CSSUnitTime>
```

This section explains why they were added and why you should almost always prefer using them.

### Background

#### CSSSizeValue

When you write CSS values like `10.px`, `5.cssRem`, `45.deg`, or even `30.s` into your code, you normally don't have to
think too much about their types. You just create them and pass them into the appropriate Kobweb / Compose HTML APIs.

Let's discuss what is actually happening when you do this. Compose HTML provides a `CSSSizeValue` class which represents
a number value and its unit.

```kotlin
val lengthValue = 10.px // CSSSizeValue<CSSUnit.px> (value = 10 and unit = px)
val angleValue = 45.deg // CSSSizeValue<CSSUnit.deg> (value = 45 and unit = deg)
```

This is a pretty elegant approach, but the types are verbose. This can be troublesome when writing code that needs to
work with them:

```kotlin
val lengths: List<CSSSizeValue<CSSUnit.px>>
fun drawArc(arc: CSSSizeValue<CSSUnit.deg>)
```

Note also that the above cases are overly restrictive, only supporting a single length and angle type, respectively. We
usually want to support all relevant types (e.g. `px`, `em`, `cssRem`, etc. for lengths; `deg`, `rad`, `grad`, and
`turn` for angles). We can do this with the following `out` syntax:

```kotlin
val lengths: List<CSSSizeValue<out CSSUnitLength>>
fun drawArc(arc: CSSSizeValue<out CSSUnitAngle>)
```

What a mouthful!

As a result, the Compose HTML team added typealiases for all these unit types, such as `CSSLengthValue`
and `CSSAngleValue`. Now, you can write the above code like:

```kotlin
val lengths: List<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)
```

Much better! Seems great. No problems, right? *Right?!*

#### CSSNumericValue

You can probably tell by my tone: Yes problems.

To explain, we first need to talk about `CSSNumericValue`.

It is common to transform values in CSS using many of its various mathematical functions. Perhaps you want to take the
sum of two different units (`10.px + 5.cssRem`) or call some other math function (`clamp(1.cssRem, 3.vw)`). These
operations return intermediate values that cannot be directly queried like a `CSSSizeValue` can.

This is handled by the `CSSNumericValue` class, also defined by Compose HTML (and which is actually a base class
of `CSSSizeValue`).

```kotlin
val lengthSum = 10.px + 2.cssRem // CSSNumericValue<CSSUnitLength>
val angleSum = 45.deg + 1.turn // CSSNumericValue<CSSAngleLength>
```

These numeric operations are of course useful to the browser, which can resolve them into absolute screen values, but
for us in user space, they are opaque calculations.

In practice, however, that's fine! The limited view of these values does not matter because we rarely need to query them
in our code. In almost all cases, we just take some numeric value, optionally tweak it by doing some more math on it,
and then pass it onto the browser.

Because it is opaque, `CSSNumericValue` is far more flexible and widely applicable than `CSSSizeValue` is. If you are
writing a function that takes a parameter, or declaring a `StyleVariable` tied to some length or time, you almost always
want to use `CSSNumericValue` and not `CSSSizeValue`.

### Prefer using Kobweb's `CSSNumericValue` typealiases

As mentioned above, the Compose HTML team created their unit-related typealiases against the `CSSSizeValue` class.

This decision makes it really easy to write code that works well when you test it with concrete size values but is
actually more restrictive than you expected.

Kobweb ensures its APIs all reference its `CSSNumericValue` typealiases:

```kotlin
// Legacy Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageValue): Modifier = styleModifier {
    lineHeight(value)
  }

// Modern Kobweb
fun Modifier.lineHeight(value: CSSLengthOrPercentageNumericValue): Modifier = styleModifier {
  lineHeight(value)
}
```

If you are using style variables in your code, or writing your own functions that take CSS units as arguments, you might
be referencing the Compose HTML types. Your code will still work fine, but you are strongly encouraged to migrate them
to Kobweb's newer set, in order to make your code more flexible about what it can accept:

```kotlin
// Not recommended
val MyFontSize by StyleVariable<CSSLengthValue>
fun drawArc(arc: CSSAngleValue)

// Recommended
val MyFontSize by StyleVariable<CSSLengthNumericValue>
fun drawArc(arc: CSSAngleNumericValue)
```

> [!NOTE]
> Perhaps in the future, the Compose HTML team might consider updating their typealiases to use the `CSSNumericValue`
> type and not the `CSSSizeValue` type. If that happens, we can revert our changes and delete this section. But until
> then, it's worth understanding why Kobweb introduces its own typealiases and why you are encouraged to use them
> instead of the Compose HTML versions.

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

# Miscellaneous topics

## Debugging your site

A Kobweb project always has a frontend and, if configured as a full stack site, a backend as well. Both require
different steps to debug them.

### Debugging the frontend

At the moment, attaching a debugger to Kotlin/JS code requires IntelliJ Ultimate. If you have it, you
can [follow these steps](https://kotlinlang.org/docs/js-debugging.html#debug-in-the-ide) in the official docs.

> [!IMPORTANT]
> Be sure the port in your URL matches the port you specified in your `.kobweb/conf.yaml` file. By default, this
> is 8080.

If you do not have access to IntelliJ Ultimate, then you'll have to rely on `println` debugging. While this is far from
great, live reloading plus Kotlin's type system generally help you incrementally build your site up without too many
issues.

> [!TIP]
> If you're a student, you can apply for a free IntelliJ Ultimate
> license [here](https://www.jetbrains.com/community/education/#students). If you maintain an open source project, you
> can apply [here](https://www.jetbrains.com/community/opensource/#support).

### Debugging the backend

Debugging the backend first requires configuring the Kobweb server to support remote debugging. This is easy to do by
modifying the `kobweb` block in your build script to enable remote debugging:

```kotiln
kobweb {
  app {
    server {
      remoteDebugging {
        enabled.set(true)
        port.set(5005)
      }
    }
  }
}
```

> [!NOTE]
> Specifying the port is optional. Otherwise, it is 5005, a common remote debugging default. If you ever need to debug
> multiple Kobweb servers at the same time, however, it can be useful to change it.

Once you've enabled remote debugging support, you can
then [follow the official documentation](https://www.jetbrains.com/help/idea/attaching-to-local-process.html#attach-to-remote)
to add a *remote JVM debug* configuration to your IDE.

> [!IMPORTANT]
> For remote debugging to work:
> * The *Debugger Mode* should be set to *Attach to remote JVM*.
> * You need to correctly specify the *Use module classpath* value. In general, use the `jvmMain` classpath associated
>   with your Kobweb application, e.g. `app.site.jvmMain`. If you've refactored your backend code out to another module,
>   you should be able to use that instead.

At this point, start up your Kobweb server using `kobweb run`.

> [!CAUTION]
> Remote debugging is only supported in dev mode. It will not be enabled for a server started with
> `kobweb run --env prod`.

With your Kobweb server running and your "remote debug" run configuration selected, press the debug button. If
everything is set up correctly, you should see a message in the IDE debugger console
like: `Connected to the target VM, address: 'localhost:5005', transport: 'socket'`

If instead, you see a red popup with a message
like `Unable to open debugger port (localhost:5005): java.net.ConnectException "Connection refused"`, please
double-check the values in your `conf.yaml` file, restart the server, and try again.

## Using a custom font

### Font hosting service

The easiest way to use a custom font is if it is already hosted for you. For example, Google Fonts provides a CDN that
you can use to load fonts directly.

> [!CAUTION]
> While this is the easiest approach, be sure you won't run into compliance issues! If you use Google Fonts on your
> site, you may technically be in violation of the GDPR in Europe, because an EU citizen's IP address is communicated to
> Google and logged. You may wish to find a Europe-safe host instead, or self-host, which you can read about
> in [the next section▼](#self-hosted-fonts).

The font service should give you HTML to add to your site's `<head>` tag. For example, Google Fonts suggests the
following when I select Roboto Regular 400:

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
```

This code should be converted into Kotlin and added to the `kobweb` block of your site's `build.gradle.kts` script:

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
        link(
          href = "https://fonts.googleapis.com/css2?family=Roboto&display=swap",
          rel = "stylesheet"
        )
      }
    }
  }
}
```

Once done, you can now reference this new font:

```kotlin
Column(Modifier.fontFamily("Roboto")) {
    Text("Hello world!")
}
```

### Self-hosted fonts

Users can flexibly declare a custom font by using
CSS's [`@font-face` rule](https://developer.mozilla.org/en-US/docs/Web/CSS/@font-face).

In Kobweb, you can normally declare CSS properties in Kotlin (within an `@InitSilk` block), but unfortunately, Firefox
doesn't allow you to define or modify `@font-face` entries
in code ([relevant Bugzilla issue](https://bugzilla.mozilla.org/show_bug.cgi?id=443978)). Therefore, for guaranteed
cross-platform compatibility, you should create a CSS file and reference it from your build script.

To keep the example concrete, let's say you've downloaded the open
source font [Lobster](https://fonts.google.com/specimen/Lobster) from Google Fonts (and its license as well, of course).

You need to put the font file inside your public resources directory, so it can be found by the user visiting your site.
I recommend the following file organization:

```
jsMain
└── resources
    └── public
        └── fonts
            ├── faces.css
            └── lobster
                ├── OFL.txt
                └── Lobster-Regular.ttf
```

where `faces.css` contains all your `@font-face` rule definitions (we just have a single one for now):

```css
@font-face {
  font-family: 'Lobster';
  src: url('/fonts/lobster/Lobster-Regular.ttf');
}
```

> [!NOTE]
> The above layout may be slightly overkill if you are sure you'll only ever have a single font, but it's flexible
> enough to support additional fonts if you decide to add more in the future, which is why we recommend it as a general
> advice here.

Now, you need to reference this CSS file from your `build.gradle.kts` script:

```kotlin
kobweb {
  app {
    index {
      head.add {
        link(rel = "stylesheet", href = "/fonts/faces.css")
      }
    }
  }
}
```

Finally, you can reference the font in your code:

```kotlin
Column(Modifier.fontFamily("Lobster")) {
    Text("Hello world!")
}
```

## Kobweb server logs

When you run `kobweb run`, the spun-up web server will, by default, log to the `.kobweb/server/logs` directory.

You can configure logging behavior by editing the `.kobweb/conf.yaml` file. Below we show setting all parameters to
their default values:

```yaml
server:
  logging:
    level: DEBUG # ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
    logRoot: ".kobweb/server/logs"
    clearLogsOnStart: true # Warning - if true, wipes ALL files in logRoot, so don't put other files in there!
    logFileBaseName: "kobweb-server" # e.g. "kobweb-server.log", "kobweb-server.2023-04-13.log"
    maxFileCount: null # null = unbound. One log file is created per day, so 30 = 1 month of logs
    totalSizeCap: 10MiB # null = unbound. Accepted units: B, K, M, G, KB, MB, GB, KiB, MiB, GiB
    compressHistory: true # If true, old log files are compressed with gzip
```

The above defaults were chosen to be reasonable for most users running their projects on their local machines in
developer mode. However, for production servers, you may want to set `clearLogsOnStart` to false, bump up the
`totalSizeCap` after reviewing the disk limitations of your web server host, and maybe set `maxFileCount` to a reasonable
limit.

Note that most config files assume "10MB" is 10 * 1024 * 1024 bytes, but here it will actually result in
10 * 1000 * 1000 bytes. You probably want to use "KiB", "MiB", or "GiB" when you configure this value.

## Configuring CORS

[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), or *Cross-Origin Resource Sharing*, is a security
feature built on the idea that a web page should not be able to make requests for resources from a server that is not
the same as the one that served the page *unless* it was served from a trusted domain.

To configure CORS for a Kobweb backend, Kobweb's `.kobweb/conf.yaml` file allows you to declare such trusted domains
using a `cors` block:

```yaml
server:
  cors:
    hosts:
      - name: "example.com"
        schemes:
          - "https"
```

> [!NOTE]
> Specifying the schemes is optional. If you don't specify them, Kobweb defaults to "http" and "https".

> [!NOTE]
> You can also specify subdomains, e.g.
> ```yaml
> - name: "example.com"
>   subdomains:
>     - "en"
>     - "de"
>     - "es"
> ```
> which would add CORS support for `en.example.com`, `de.example.com`, and `es.example.com`, as well as `example.com`
> itself.

Once configured, your Kobweb server will be able to respond to data requests from any of the specified hosts.

> [!TIP]
> If you find that your full-stack site, which was working locally during development, rejects requests in the
> production version, check your browser's console logs. If you see errors in there about a violated CORS policy, that
> means you didn't configure CORS correctly.

## Generating export traces

The Kobweb export feature is built on top of [Microsoft Playwright](https://playwright.dev/), a solution for making it
easy to download and run browsers programmatically.

One of the features provided by Playwright is the ability to generate traces, which are essentially detailed reports
you can use to understand what is happening as your site loads. Kobweb exposes this feature through the `export` block
in your Kobweb application's build script.

Enabling traces is easy:

```kotlin
// build.gradle.kts
plugins {
  // ... other plugins ...
  alias(libs.plugins.kobweb.application)
}

kobweb {
  app {
    export {
      enableTraces()
    }
  }
}
```

You can pass in parameters to configure the `enableTraces` method, but by default, it will generate trace files into
your `.kobweb/export-traces/` directory.

Once enabled, you can run `kobweb export`, then once exported, open any of the generated `*.trace.zip` files by
navigating to them using your OS's file explorer and drag-and-dropping them into
the [Playwright Trace Viewer](https://trace.playwright.dev/).

> [!TIP]
> You can learn more about how to use the Trace
> Viewer [using the official documentation](https://playwright.dev/docs/trace-viewer).

It's not expected many users will need to debug their site exports, but it's a great tool to have (especially combined
with the [server logs feature](#kobweb-server-logs)) to diagnose if one of your pages is taking longer to export than
expected.

## Extending the Compose HTML library

In the beginning, Kobweb was only intended to be a thin layer on top of Compose HTML, but the more we worked on it, the
more we ran into features that were simply not yet implemented in Compose HTML. We also wrote utility methods and
classes that were so generally useful, that it would have been a shame to bury them deep inside our framework.

As a result, we created a module called [`compose-html-ext`](frontend/compose-html-ext/README.md), where we put code
that we would be more than happy for the Compose HTML team to fork and migrate over to Compose HTML someday.

This now includes (not comprehensive):

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

If you want to use Compose HTML but *not* Kobweb, you can still use and benefit from `compose-html-ext` in your own
project. An example build script could look like this:

```kotlin
// build.gradle.kts
plugins {
  kotlin("multiplatform") version "..."
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
  maven("https://us-central1-maven.pkg.dev/varabyte-repos/public") // IMPORTANT!!!
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

# Can We Kobweb Yet

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

I think there's enough there now to let you do almost anything you'd want to do, as either Kobweb supports it or you can
escape hatch to underlying Compose HTML / Kotlin/JS approaches, but there might be some areas where it's still a bit
DIY. It would be great to get real-world experience to hear what issues users are actually running into.

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
    [this earlier section▲](#adding-kobweb-to-an-existing-project) if you want to try anyway.
* a company:
  * **Probably not?** I'm assuming most companies are so risk-averse they would not even use Compose HTML, which Kobweb
    is built on top of. If you *were* considering Compose HTML, however, Kobweb is worth a look.

On the fence but not sure? Connect with us, and I'd be happy to help you assess your situation.

## Testimonials

I'm pleased to mention that Kobweb has received feedback from some satisfied users. Here are a few:

* "This is a pretty bloody amazing technology you've created here. I have been dreading upgrading [my] website for ages because I didn't want to go back to html and css 🫤 now I can stay with Kotlin 😀"
* "Kobweb looks fantastic and I've been [trying] to use Kotlin in all parts of [my] hobby stuff and work, so I got real excited when I saw Kobweb, [even though] I hadn't been satisfied with a web framework in a long time. Incredible work."
* "I started using Kobweb last week and I have to say this [...] reinvented web development for me. [...] I used to hate html css. After getting my hands on kobweb I’m in love with it."
* "Finally got paid -- all thanks to kobweb 🎉💥"
* "I didn't wanna learn any JS framework so when I first learned about kobweb it felt like a no-brainer; having built 2 Android apps with compose already and a backend with ktor. One could argue Android developers are the best target audience since the additional knowledge needed to move an app to the web with Kobweb is minimal. I love it! 🤩"

# Connecting with us

* [Join my Discord!](https://discord.gg/5NZ2GKV5Cs)
* [GitHub Discussions](https://github.com/varabyte/kobweb/discussions) for this project
* The [Kobweb channel](https://kotlinlang.slack.com/archives/C04RTD72RQ8) on the Kotlin Slack
* You can send direct queries to [my email](mailto:bitspittle@gmail.com)

If you're comfortable with it, using Discord is recommended, because there's a growing community of users in there who
can offer help even when I'm not around.

# Filing issues and leaving feedback

It is still early days, and while we believe we've proven the feasibility of this approach at this point, there's still
plenty of work to do to get to a 1.0 launch! We are hungry for the community's feedback, so please don't hesitate to:

* [Open an issue](https://github.com/varabyte/kobweb/issues/new/choose)
* Contact us (using any of the ways mentioned above) telling us what features you want
* Ask us for guidance, especially as there are no tutorials yet (your questions can help us know what to write first!)

Thank you for your support and interest in Kobweb!
