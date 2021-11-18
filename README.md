![version: 0.6.3](https://img.shields.io/badge/kobweb-v0.6.3-yellow)
<a href="https://discord.gg/5NZ2GKV5Cs">
  <img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>
[![Follow @bitspittle](https://img.shields.io/twitter/follow/bitspittle.svg?style=social)](https://twitter.com/intent/follow?screen_name=bitspittle)

# K🕸️bweb

Kobweb is an opinionated Kotlin framework for creating websites and web apps, built on top of
[Web Compose](https://compose-web.ui.pages.jetbrains.team) and inspired by [Next.js](https://nextjs.org)
and [Chakra UI](https://chakra-ui.com).

```kotlin
@Page
@Composable
fun HomePage() {
  Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Row(Modifier.align(Alignment.End)) {
      var colorMode by rememberColorMode()
      Button(
        onClick = { colorMode = colorMode.opposite() },
        Modifier.clip(Circle())
      ) {
        Box(Modifier.padding(4.px)) {
          // Includes support for Font Awesome icons
          if (colorMode.isLight()) FaSun() else FaMoon()
        }
      }
    }
    H1 {
      Text("Welcome to Kobweb!")
    }
    Row {
      Text("Create rich, dynamic web apps with ease, leveraging ")
      Link("https://kotlinlang.org/", "Kotlin")
      Text(" and ")
      Link("https://compose-web.ui.pages.jetbrains.team/", "Web Compose")
    }
  }
}
```

<p align="center">
<img src="https://github.com/varabyte/media/raw/main/kobweb/screencasts/kobweb-welcome.gif" />
</p>

---

**Kobweb is currently in technology preview**. Please consider starring the project to indicate interest, so we know we're
creating something the community wants. [How ready is it? ▼](https://github.com/varabyte/kobweb#can-we-kobweb-yet)

Our goal is to provide:

* an intuitive structure for organizing your Kotlin website or web app
* automatic handling of routing between pages
* a collection of useful _batteries included_ widgets built on top of Web Compose
* an environment built from the ground up around live reloading
* static site exports for improved SEO
* shared, rich types between client and server
* out-of-the-box Markdown support
* an open source foundation that the community can extend
* and much, much more!

Here's a demo where we create a Web Compose website from scratch with Markdown support and live reloading, in under 10
seconds:

https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4

# Trying it out yourself

The first step is to get the Kobweb binary. You can download it or build it, so we'll include instructions for both
approaches.

## Download the Kobweb binary

Our binary artifact is hosted on github. To download latest:

```bash
$ cd /path/to/applications/kobweb
$ wget https://github.com/varabyte/kobweb/releases/download/v0.6.3/kobweb-0.6.3.zip
$ unzip kobweb-0.6.3.zip
```

and I recommend adding it to your path, either directly:

```bash
$ PATH=$PATH:/path/to/applications/kobweb/kobweb-0.6.3/bin
$ kobweb version # to check it's working
```

or via symbolic link:

```base
$ cd /path/to/bin # some folder you've created that's in your PATH
$ ln -s /path/to/applications/kobweb/kobweb-0.6.3/bin/kobweb kobweb
```

## Build the Kobweb binary

Although we host Kobweb artifacts on github, it's easy enough to build your own.

**Note:** Building Kobweb requires JDK11 or newer. If you don't already have this set up, the easiest way is to
[download a JDK](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html), unzip it somewhere,
and update your `JAVA_HOME` variable to point at it.

```bash
JAVA_HOME=/path/to/jdks/corretto-11.0.12
# ... or whatever version or path you chose
```

With `JAVA_HOME` set up, building is just a single Gradle command:

```bash
$ cd /path/to/src/root
$ git clone --recurse-submodules https://github.com/varabyte/kobweb
$ cd kobweb
$ ./gradlew :cli:kobweb:installDist
```

Finally, update your PATH:

```bash
$ PATH=$PATH:/path/to/src/root/kobweb/cli/kobweb/build/install/kobweb/bin
$ kobweb version # to check it's working
```

## Create your Kobweb site

```bash
$ cd /path/to/projects/
$ kobweb create site
```

You'll be asked a few questions required for setting up your project. When prompted for a folder name, it will create
the folder _within_ your current directory, so you don't have to worry about it overwriting any files.

When finished, you'll have a basic project with three pages - a home page, an about page, and a markdown page - and some
components (which are collections of reusable, composable pieces). Your own directory structure should look something
like:

```
my-project
└── src
    └── jsMain
        ├── kotlin
        │   └── org
        │       └── example
        │           └── myproject
        │               ├── components
        │               │  ├── layouts
        │               │  │  └── PageLayout.kt
        │               │  ├── sections
        │               │  │  └── NavHeader.kt
        │               │  └── widgets
        │               │     └── GoHomeLink.kt
        │               ├── MyApp.kt
        │               └── pages
        │                   ├── About.kt
        │                   └── Index.kt
        └── resources
            └── markdown
                └── Markdown.md

```

Note that there's no index.html or routing logic anywhere! We generate that for you automatically when you run Kobweb.
Which brings us to the next section...

## Run your Kobweb site

```bash
$ cd /path/to/projects/root/your-project
$ kobweb run
```

This command spins up a webserver at http://localhost:8080. If you want to configure the port, you can do so by editing
your project's `.kobweb/conf.yaml` file.

You can open your project in IntelliJ and start editing it. While Kobweb is running, it will detect changes, recompile,
and deploy updates to your site automatically.

## Examples

Kobweb will provide a growing collection of samples for you to learn from. To see what's available, run:

```bash
$ kobweb list

You can create the following Kobweb projects by typing `kobweb create ...`

• examples/todo: An example TODO app, showcasing client / server interactions
• site: A template for a minimal site that demonstrates the basic features of Kobweb
```

For example, `kobweb create examples/todo` will instantiate a TODO app locally.

# Basics

Kobweb, at its core, is a handful of classes responsible for trimming away much of the boilerplate around building a
Web Compose app, such as routing and setting up default CSS styles. It exposes a handful of annotations and utility
methods which your app can use to communicate intent with the framework. These annotations work in conjunction with our
Gradle plugin (`com.varabyte.kobweb.application`) that handles code and resource generation for you.

Kobweb is also a CLI binary of the same name which provides commands to handle the parts of building a Web Compose app
that are less glamorous. We want to get that stuff out of the way, so you can enjoy focusing on the more interesting
work!

## Create a page

Creating a page is easy! It's just a normal `@Composable` method. To upgrade your composable to a page, all you need to
do is:

1. Define your composable in a file somewhere under the `pages` package in your `jsMain` source directory.
1. Annotate it with `@Page`

Just from that, Kobweb will create a site entry for you automatically.

For example, if I create the following file:

```kotlin
// jsMain/kotlin/com/example/mysite/pages/admin/Settings.kt

@Page
@Composable
fun SettingsPage() {
    /* ... */
}
```

this will create a page that I can then visit by going to `mysite.com/admin/settings`.

By default, the path comes from the file name, although there will be ways to override this behavior on a
case-by-case basis (* *coming soon*).

The file name `Index.kt` is special. If a page is defined inside such a file, it will be treated as the default page
under that URL. For example, a page defined in `.../pages/admin/Index.kt` will be visited if the user visits
`mysite.com/admin`.

## Silk

Silk is a UI layer included with Kobweb and built upon Web Compose. (To learn more about Web Compose, please visit
[the official tutorials](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Getting_Started)).

While Web Compose requires you to understand underlying html / css concepts, Silk attempts to abstract a lot of that away,
providing an API more akin to what you might experience developing a Compose app on Android or Desktop. Less
"div, span, flexbox, attrs, styles, classes" and more "Rows, Columns, Boxes, and Modifiers".

We consider Silk a pretty important part of the Kobweb experience, but it's worth pointing out that it's designed as an
optional component. You can absolutely use Kobweb without Silk. You can also interleave Silk and Web Compose without
issue (as Silk, itself, is just composing Web Compose methods).

### What about Multiplatform Widgets?

Jetbrains is working on an experimental project called "multiplatform widgets" which is supposed to bring the Desktop /
Android API to the web. And it may seem like the Kobweb + Silk approach is competing with it.

However, I've found there is a fundamental distance between Desktop / Android Compose and Web Compose. Specifically,
Desktop / Android targets render to their own surface, while Web modifies a parallel html / css DOM tree and leaves it
to do the final rendering.

This has major implications on how similar the two APIs can get. For example, in Desktop / Android, the order you apply
modifiers matters, while in Web, this action simply sets html style properties under the hood, where order does not
matter.

One approach would be to own the entire rendering pipeline, ditching html / css entirely and targeting a full page
canvas or something. However, this limits the ability for robots to crawl and index your site, which is a major
drawback. It also means that debugging in a browser would be a rough experience, as the browser's developer tools would
be limited in the insights it could provide for your site. It would also prevent a developer from making use of the rich
ecosystem of Javascript libraries out there that modify the DOM tree themselves.

For now, I am making a bet that the best way forward is to embrace the web, sticking to html / css, but providing a
rich UI library of widgets that hopefully makes it relatively rare for the developer to worry about it. For example,
flexbox is a very powerful component, but you'll find it's much easier to compose Rows and Columns together than trying
to remember if you should be justifying your items or aligning your content, even if Rows and Columns are just creating
the correct html / css for you behind the scenes anyways.

## Components: Layouts, Sections, and Widgets

Outside of pages, it is common to create reusable composable parts. While Kobweb doesn't enforce any particular rule
here, we recommend a convention which, if followed, may make it easier to allow new readers of your codebase to get
around.

First, as a sibling to pages, create a folder called **components**. Within it, add:

* **layouts** - High-level composables that provide entire page layouts. Most (all?) of your `@Page` pages will start
  by calling a page layout function first. You may only have a single layout for your entire site.
* **sections** - Medium-level composables that represent compound areas inside your pages, organizing a collection of
  many children composables. If you have multiple layouts, it's likely sections would be shared across them. For
  example, nav headers and footers are great candidates for this subfolder.
* **widgets** - Low-level composables. Focused UI pieces that you may want to re-use all around your site. For example,
  a stylized visitor counter would be a good candidate for this subfolder.

## Define API routes

You can define and annotate methods which will generate server endpoints you can interact with. To add one:

1. Define your method (optionally `suspend`able) in a file somewhere under the `api` package your `jvmMain` source
directory.
1. The method should take exactly one argument, an `ApiContext`. 
1. Annotate it with `@Api`

For example, here's a simple method that echoes back an argument passed into it:

```kotlin
// jvmMain/kotlin/com/example/mysite/api/Echo.kt

@Api
fun echo(ctx: ApiContext) {
    // ctx.req is for the incoming request, ctx.res for responding back to the client

    // Queries are parsed from the URL, e.g. here "/echo?message=..."
    val msg = ctx.req.query["message"] ?: ""
    ctx.res.setBodyText(msg)
  
    // You could also do something like: `ctx.res.body = ctx.req.body`
    // but using query parameters makes for an easier demo
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
      println("Echoed: " + window.api.get("echo?message=hello")!!.decodeToString())
    }
  })
}
```

All the HTTP methods are supported (`post`, `put`, etc.). Of course, you can also use `window.fetch(...)` directly.

## Markdown

If you create a markdown file under the `jsMain/resources/markdown` folder, a corresponding page will be created for
you at build time, using the filename as its path.

For example, if I create the following file:

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.kt

# Kobweb Tutorial

...
```

this will create a page that I can then visit by going to `mysite.com/docs/tutorial/kobweb`

The power of Kotlin + Web Compose is composable interactive components though, not static text! That's why Kobweb
Markdown support enables extensions to allow this out of the box.

### Front Matter

Front Matter is metadata that you can specify at the beginning of your document, like so:

```markdown
---
title: Tutorial
author: bitspittle
---
```

and these key / value pairs can be referenced in your Kotlin `@Composable` code (* *coming soon*).

However, there's a special value which, if set, will be used to render a root `@Composable` that wraps the code your
markdown file would otherwise create. This is useful for specifying a layout for example:

```markdown
---
root: .components.layout.DocsLayout
---

# Kobweb Tutorial
```

This will generate code like the following:

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

### Kobweb Call

A markdown extension built just for Kobweb, you can surround a path to a `@Composable` method with double-curly braces
to call it:

```markdown
# Kobweb Tutorial

...

{{ .components.widgets.VisitorCounter }}
```

which will generate code for you like the following:

```kotlin
import com.mysite.components.widgets.VisitorCounter

@Composable
@Page
fun KobwebPage() {
    /* ... */
    VisitorCounter()
}
```

In this way, you can write pages that are mostly static text punctuated with beautiful, interactive components. This
could be a great approach for people who want to write and host their own blogs, for example.

# Can We Kobweb Yet

Current state: **Functional but early**

Kobweb has some pretty big pieces working already. It is easy to set up a new project and get things running quickly.
The live reloading flow is pretty nice, and you'll miss it when you switch to projects that don't have it. It supports
generating pages from Markdown that can reference your Composable code. And while it's not quite server-side rendering,
you can export static pages which will get hydrated on load.

However, there's still a lot to do. The API surface is a bit lean in some areas right now, especially around Silk UI
components, plus filling in holes in the APIs that interact with Web Compose. There are probably quite a few sharp
corners. And while the code is decently documented, higher level documentation is missing. Windows support needs love.

So, should you use Kobweb at this point? If you are...

* a Kotlin tinkerer who is excited to play around with new tech and isn't afraid of creating toy projects atop APIs
  which may shift underfoot:
  * **YES!!!** Please see the [connecting with us ▼](https://github.com/varabyte/kobweb#connecting-with-us) section
  below, we'd definitely love to hear from you. Now's a great time if you'd want to have a voice in the direction of
  this project.
* a Kotlin developer who wants to write a small web app or create a new blog from scratch:
  * ***Maybe***, but now is probably a bit too early.
* someone who already has an existing project in progress and wants to integrate Kobweb into it:
  * **No**
* a company:
  * **NOOOOOO** (someday, we hope, but not yet)

# Templates

Kobweb provides its templates in a separate git repository, which is referenced within this project as a submodule for
convenience. To pull down everything, run:

```bash
/path/to/src/root
$ git clone --recurse-submodules https://github.com/varabyte/kobweb

# or, if you've already previously cloned kobweb...
/path/to/src/root/kobweb
$ git submodule update --init
```

# Known Issues

* `kobweb run` sometimes gets stuck when Gradle (running behind it) gets stuck.
  * Quit kobweb, run `./gradlew --stop`, and then try again
  * Run `./gradlew kobwebGen` or `./gradlew kobwebStart` with various Gradle debug options to see what's going on under
  the hood (e.g. `./gradlew kobwebStart --stacktrace`)

Solutions didn't work? Or you're encountering issues not listed here? Please consider
[leaving feedback ▼](https://github.com/varabyte/kobweb#filing-issues-and-leaving-feedback)!

# Connecting with us

* [Join our Discord!](https://discord.gg/5NZ2GKV5Cs)
* Follow me on Twitter: [@bitspittle](https://twitter.com/bitspittle)
* You can send direct queries to [my email](mailto:bitspittle@gmail.com)

# Filing issues and leaving feedback

It is still early days, and while we believe we've proven the feasibility of this approach at this point, there's still
plenty of work to do to get to a 1.0 launch! We are hungry for the community's feedback, so please don't hesitate to:

* [Open an issue](https://github.com/varabyte/kobweb/issues/new/choose)
* Contact us (using any of the ways mentioned above) telling us what features you want
* Ask us for guidance, especially as there are no tutorials yet (your questions can help us know what to write first!)

Thank you for your support and interest in Kobweb!
