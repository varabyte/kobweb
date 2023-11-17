# Intermediate topics

Here you will find interesting topics

## Table of Contents
- [Intermediate topics](#intermediate-topics)
  - [Table of Contents](#table-of-contents)
    - [Components: Layouts, Sections, and Widgets](#components-layouts-sections-and-widgets)
    - [Specifying your application root](#specifying-your-application-root)
    - [Static layout vs. Full stack sites](#static-layout-vs-full-stack-sites)
      - [Exporting and running](#exporting-and-running)
      - [Deploying](#deploying)
    - [Communicating with the server](#communicating-with-the-server)
      - [Declare a full stack project](#declare-a-full-stack-project)
      - [Define API routes](#define-api-routes)
      - [Define API streams](#define-api-streams)
        - [API stream conveniences](#api-stream-conveniences)
      - [API routes vs. API streams](#api-routes-vs-api-streams)
    - [Markdown](#markdown)
      - [Front Matter](#front-matter)
        - [Root](#root)
        - [Route Override](#route-override)
      - [Kobweb Call](#kobweb-call)
        - [Block syntax](#block-syntax)
        - [Inline syntax](#inline-syntax)
      - [Imports](#imports)
        - [Global Imports](#global-imports)
        - [Local Imports](#local-imports)

### Components: Layouts, Sections, and Widgets

Outside of pages, it is common to create reusable, composable parts. While Kobweb doesn't enforce any particular rule
here, we recommend a convention which, if followed, may make it easier to allow new readers of your codebase to get
around.

First, as a sibling to pages, create a folder called **components**. Within it, add:

* **layouts** - High-level composable that provide entire page layouts.
  Most (all?) of your `@Page` pages will start by
  calling a page layout function first.
  It's possible that you will only need a single layout for your entire site.
* **sections** - Medium-level composable that represent compound areas inside your pages, organizing a collection of
  many children composable.
  If you have multiple layouts, it's likely sections would be shared across them.
  For
  example, nav headers and footers are great candidates for this subfolder.
* **widgets** - Low-level composable.
  Focused UI pieces that you may want to re-use all around your site.
  For example,
  a stylized visitor counter would be a good candidate for this subfolder.

### Specifying your application root

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

It is likely you'll want to configure this further for your own application. Perhaps you have additional styles you'd
like to globally define (e.g. the default font used by your site). Perhaps you have some initialization logic that you'd
like to run before any page gets run (like logic for updating saved settings into local storage).

In this case, you can create your own root composable and annotate it with `@App`. If present, Kobweb will use that
instead of its own default. You should, of course, delegate to `KobwebApp` (or `SilkApp` if using Silk), as the
initialization logic from those methods should still be run.

Here's an example application composable override that I use in one of my own projects:

```kotlin
@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
      val colorMode = ColorMode.current
        LaunchedEffect(colorMode) { // Relaunched every time the color mode changes
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        // A full screen Silk surface. Sets the background based on Silk's palette and animates color changes.
        Surface(Modifier.minHeight(100.vh)) {
            content()
        }
    }
}
```

You can define *at most* a single `@App` on your site, or else the Kobweb Application plugin will complain at build
time.

### Static layout vs. Full stack sites

There are two flavors of Kobweb sites: *static* and *full stack*.

A *static* site (or, more completely, a *static layout* site) is one where you export a bunch of frontend files (e.g.
`html`, `js`, and public resources) into a single, organized folder that gets served in a direct way by
a [static website hosting provider](https://en.wikipedia.org/wiki/Web_hosting_service#Static_page_hosting). In other
words, the name *static* does not refer to the behavior of your site but rather that of your hosting provider solution.

A *full stack* site is one where you write both the logic that runs on the frontend (i.e. on the user's machine) as well
as the logic that runs on the backend (i.e. on a server somewhere). This custom server must serve requested files (much
like a static web hosting service does) plus it should also define endpoints providing unique functionality tailored to
your site's needs.

> [!IMPORTANT]
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

#### Exporting and running

Both site flavors require an export. To export your site with a static layout, use the `kobweb export --layout static`
command, while for full stack the command is `kobweb export --layout kobweb` (or just `kobweb export` since `kobweb` is
the default layout as it originally was the only way).

Once exported, you can test your site by running it locally before uploading. You run a static site with
`kobweb run --env prod --layout static` and a full stack site with `kobweb run --env prod --layout kobweb` (or just
`kobweb run --env prod`).

#### Deploying

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

### Communicating with the server

Let's say you've decided on creating a full stack website using Kobweb. This section walks you through setting it up as
well as introducing the various APIs for communicating to the backend from the frontend.

#### Declare a full stack project

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

#### Define API routes

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

#### Define API streams

Kobweb servers also support persistent connections via streams. Streams are essentially named channels that maintain
continuous contact between the client and the server, allowing either to send messages to the other at any time. This is
especially useful if you want your server to be able to communicate updates to your client without needing to poll.

Additionally, multiple clients can connect to the same stream. In this case, the server can choose to not only send a
message back to your client, but also to broadcast messages to all users (or a filtered subset of users) on the same
stream. You could use this, for example, to implement a chat server with rooms.

Like API routes, API streams must be defined under the `api` package in your `jvmMain` source directory. By default, the
name of the stream will be derived from the file name and path that it's declared in.

Unlike API routes, API streams are defined as properties, not methods. This is because API streams need to be a bit more
flexible than routes, since streams consist of multiple distinct events: client connection, client messages, and
client disconnection.

Streams do not have to be annotated. The Kobweb Application plugin can automatically detect them.

For example, here's a simple stream that echoes back any argument passed into it:

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

To communicate with an API stream from your site, you need to create a stream connection there:

```kotlin
@Page
@Composable
fun ApiStreamDemoPage() {
  val echoStream = remember { ApiStream("echo") }
  LaunchedEffect(Unit) {
    echoStream.connect(object : ApiStreamListener {
      override fun onConnected(ctx: ConnectedContext) {  }
      override fun onTextReceived(ctx: TextReceivedContext) { console.log("Echoed: ${ctx.text}") }
      override fun onDisconnected(ctx: DisconnectedContext) {  }
    })
  }

  Button(onClick = {
      echoStream.send("hello!")
  }) { Text("Click me") }
}
```

After running your project, you can click on the button and check the console logs. If everything is working properly,
you should see "Echoed: hello!" for each time you pressed the button.

> [!NOTE]
> The `examples/chat` template project uses API streams to implement a very simple chat application, so you can
> reference that project for a more realistic example.

##### API stream conveniences

The above example demonstrated API streams in their most verbose form. However, depending on your use-case, you can
elide a fair bit of boilerplate.

First of all, the connect and disconnect handlers are optional, so you can omit them if you don't need them. Let's
simplify the echo example:

```kotlin
// Backend
val echo = object : ApiStream {
  override suspend fun onTextReceived(ctx: TextReceivedContext) { ctx.stream.send(ctx.text) }
}

// Frontend
val echoStream = remember { ApiStream("echo") }
LaunchedEffect(Unit) {
  echoStream.connect(object : ApiStreamListener {
    override fun onTextReceived(ctx: TextReceivedContext) { console.log("Echoed: ${ctx.text}") }
  })
}
```

Additionally, if you only care about the text event, there are convenience overrides for that:

```kotlin
// Backend
val echo = ApiStream { ctx -> ctx.stream.send(ctx.text) }

// Frontend
val echoStream = remember { ApiStream("echo") }
LaunchedEffect(Unit) {
  echoStream.connect { ctx -> console.log("Echoed: ${ctx.text}") }
}
```

And *finally*, you can simplify the frontend code even further by using the `rememberApiStream` composable:

```kotlin
// Frontend
val echoStream = rememberApiStream("echo") { text -> console.log("Echoed: $text") }

// ApiStreamListener version also available:
// val echoStream = rememberApiStream("echo", object : ApiStreamListener { ... })
```

In practice, your API streams will probably be a bit more involved than the echo example, but it's nice to know that you
can handle some cases only needing a one-liner on the server and another on the client to create a persistent
client-server connection!

#### API routes vs. API streams

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

### Markdown

If you create a markdown file under the `jsMain/resources/markdown` folder, a corresponding page will be created for you
at build time, using the filename as its path.

For example, if I create the following file:

```markdown
// jsMain/resources/markdown/docs/tutorial/Kobweb.kt

# Kobweb Tutorial

...
```

this will create a page that I can then visit by going to `mysite.com/docs/tutorial/kobweb`

#### Front Matter

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

##### Root

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

##### Route Override

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
fun AStarDemoPage() { /* ... */ }
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

#### Kobweb Call

The power of Kotlin + Compose HTML is interactive components, not static text! Therefore, Kobweb Markdown support
enables special syntax that can be used to insert Kotlin code.

##### Block syntax

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

##### Inline syntax

Occasionally, you may want to insert a smaller widget into the flow of a single sentence. For this case, use the
`${...}` inline syntax:

```markdown
Press ${.components.widgets.ColorButton} to toggle the site's current color.
```

> [!IMPORTANT]
> Spaces are not allowed within the curly braces! If you have them there, Markdown skips over the whole thing and leaves
> it as text.

#### Imports

You may wish to add imports to the code generated from your markdown. Kobweb Markdown supports registering both
*global* imports (imports that will be added to every generated file) and *local* imports (those that will only apply
to a single target file).

##### Global Imports

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

##### Local Imports

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