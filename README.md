# K🕸️bweb

Kobweb is an opinionated Kotlin framework for creating websites and web apps, built on top of
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) and inspired by [Next.js](https://nextjs.org)
and [Chakra UI](https://chakra-ui.com).

[![version: 0.15.0](https://img.shields.io/badge/kobweb-0.15.0-blue)](COMPATIBILITY.md)
[![version: 0.9.13](https://img.shields.io/badge/kobweb_cli-0.9.13-blue)](https://github.com/varabyte/kobweb-cli)
<br>
[![kotlin: 1.9.20](https://img.shields.io/badge/kotlin-1.9.20-blue?logo=kotlin)](COMPATIBILITY.md)
[![compose: 1.5.10](https://img.shields.io/badge/compose-1.5.10-blue?logo=jetpackcompose)](COMPATIBILITY.md)
[![ktor: 2.3.0](https://img.shields.io/badge/ktor-2.3.0-blue)](https://ktor.io/)
<br>
[![Varabyte Discord](https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/5NZ2GKV5Cs)
[![Mastodon Follow](https://img.shields.io/mastodon/follow/109382855401210782?domain=https%3A%2F%2Ffosstodon.org&style=social)](https://fosstodon.org/@bitspittle)

## Table of contents
- [K🕸️bweb](#k️bweb)
  - [Table of contents](#table-of-contents)
  - [About](#about)
  - [Installation](#installation)
  - [Create your Kobweb site](#create-your-kobweb-site)
  - [Run your Kobweb site](#run-your-kobweb-site)
    - [Using IntelliJ](#using-intellij)
      - [Terminal tool window](#terminal-tool-window)
      - [Gradle commands](#gradle-commands)
  - [Running examples](#running-examples)
    - [Gradle version catalogs](#gradle-version-catalogs)
      - [Upgrading Kobweb in your project](#upgrading-kobweb-in-your-project)
  - [Topics](#topics)
    - [Basics topics](#basics-topics)
    - [Intermediate topics](#intermediate-topics)
    - [Advanced topics](#advanced-topics)
    - [Miscellaneous topics](#miscellaneous-topics)
  - [Kobweb server logs](#kobweb-server-logs)
  - [Can We Kobweb Yet?](#can-we-kobweb-yet)
  - [Testimonials](#testimonials)
  - [Connecting with us](#connecting-with-us)
  - [Filing issues and leaving feedback](#filing-issues-and-leaving-feedback)
  - [Showcase](#showcase)

## About

Kobweb is easy to use and has built-in functionalities to help you build fast, testable and scalable applications

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

## Installation

Before you can develop Kobweb applications, you need the Kobweb CLI,
which is a set of tools in the command line available in one command
which makes the task easier to manage, 
develop, export and more

The installation section has been
moved to a separated [page](./docs/installation.md)

The installation page also covers how to update the Kobweb binary (CLI)

## Create your Kobweb site

First of all, navigate into where
you want to create your application <br>
Then run the command
```bash
kobweb create app
```
You'll be asked a few questions needed for setting up your project.

Then you can open the project with your favorite IDE

For more [info](./docs/create-your-kobweb-site.md)

## Run your Kobweb site

```bash
$ cd /path/to/projects/your-project/site
$ kobweb run
```

This command spins up a webserver at http://localhost:8080. If you want to configure the port, you can do so by editing
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

> [!NOTE]
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

While Kobweb is still pre-1.0, it has been usable for a while now. It provides escape hatches to lower level APIs, so
you can accomplish anything even if Kobweb doesn't support it yet. Please consider starring the project to indicate
interest, so we know we're creating something the community wants.
[How ready is it?▼](#can-we-kobweb-yet)

Our goal is to provide:

* an intuitive structure for organizing your Kotlin website or web app
* automatic handling of routing between pages
* a collection of useful _batteries included_ widgets built on top of Compose HTML
* an environment built from the ground up around live reloading
* static site exports for improved SEO and potentially cheaper server setups
* support for responsive (i.e. mobile and desktop) design
* shared, rich types between client and server
* out-of-the-box Markdown support
* a way to easily define server API routes and persistent API streams
* an open source foundation that the community can extend
* and much, much more!

Here's a demo where we create a Compose HTML project from scratch with Markdown support and live reloading, in under
10 seconds:

https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4

> [!NOTE]
> One of Kobweb's users, Stevdza-San, has created YouTube videos that demonstrate how to build projects using Kobweb.
>
> * [Building a static layout site](https://www.youtube.com/watch?v=F5B-CxJTKlg)
>   * This is a great starting place, and it's perfect for simple portfolio sites or SEO-friendly landing pages for your
>     software.
> * [Building a full stack multiplatform site](https://www.youtube.com/watch?v=zcrY0qayWF4)
>   * This demonstrates how to write both frontend and backend logic. It also demonstrates how you can write a separate
>     Android frontend that can also work with your server. (This video is still useful to watch even if you never intend
>     to have any other frontend besides web).
> * It's easy to start with a static layout site and migrate to a full stack site later. (You can read more about
>   [Static vs. Fullstack sites▼](#static-layout-vs-full-stack-sites) below.)

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

> [!NOTE]
> It can be confusing, but Kobweb has two versions -- the version for the library itself (the one that is applicable in
> this situation), and the one for the command line tool.


## Topics

### Basics topics

To get the [basics](./docs/topics/basics.md) of Kobweb.

### Intermediate topics

Got the basics already??

Take a rest because you will
need it for [this](./docs/topics/intermediate.md)

### Advanced topics

So are you ready to get to the hot topics?? <br>
Then start with [this](./docs/topics/advanced.md)

### Miscellaneous topics

We are almost done. click on [this](./docs/topics/miscellaneous.md) 

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
`totalSizeCap` after reviewing disk limitations of your web server host, and maybe set `maxFileCount` to a reasonable
limit.

Note that most config files assume "10MB" is 10 * 1024 * 1024 bytes, but here it will actually result in
10 * 1000 * 1000 bytes. You probably want to use "KiB", "MiB", or "GiB" when you configure this value.

## Can We Kobweb Yet?

Current state: **Foundations are in place! You may encounter API gaps.**

You may wish to refer to our [Kobweb 1.0 roadmap document](https://docs.google.com/document/d/1n2Jd02yzuxaatpT7gOhEuijzfaSu9UIaQTV2t4EPcPk/preview).

Kobweb is becoming quite functional. We are already using it to build https://kobweb.varabyte.com and
https://bitspittle.dev. Several users have created working portfolio sites already, and I'm aware of at least two cases
where Kobweb was used in a project for a client.

At this point:

* It is easy to set up a new project and get things running quickly.
* The live reloading flow is pretty nice, and you'll miss it when you switch to projects that don't have it.
* It supports generating pages from Markdown that can reference your Composable code.
* While it's not quite server-side rendering, you can export static pages which will get hydrated on load.
* A huge range of CSS properties are supported, along with support for style variables and animations.
* You can use the `Modifier` builder for a significant number of css properties.
* Silk components are color mode aware and support responsive behavior.
* There are quite a few widgets available, and it's easy to create your own.

However, there's always more to do.

* I'm trying to add support for every stabilized CSS property, but some are still missing, especially less
  common ones. (You can use a fallback for such cases in the meanwhile).
* There are still a handful of widgets planned to be added.
* A lot of detailed documentation is planned to go into the Kobweb site (linked just above) but it isn't done yet.

I think there's enough there now to let you do almost anything you'd want to do, as either Kobweb supports it or you can
escape hatch to underlying Compose HTML / Kotlin/JS approaches, but there might be some areas where it's still a bit
DIY. It would be great to get real world experience to hear what issues users are actually running into.

So, should you use Kobweb at this point? If you are...

* playing around with Compose HTML for the first time and want to get up and running quickly on a toy project:
    * **YES!!!** Please see the [connecting with us▼](#connecting-with-us) section
      below, we'd definitely love to hear from you. It's still a good time if you'd want to have a voice in the
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

* "This is a pretty bloody amazing technology you've created here. I have been dreading upgrading [my] website for ages because I didn't want to go back to html and css 🫤 now i can stay with Kotlin 😀"
* "Kobweb looks fantastic, and I've been [trying] to use kotlin in all parts of [my] hobby stuff and work, so I got real excited when I saw Kobweb, [even though] I hadn't been satisfied with a web framework in a long time. Incredible work."
* "I started using Kobweb from last week, and I have to say this [...] reinvented web development for me. [...] I used to hate html css. After getting hands on kobweb I’m in love with it."
* "Finally, got paid—all thanks to kobweb 🎉💥"
* "I didn't wanna learn any JS framework, so when I first learned about kobweb it felt like a no-brainer; having built 2 Android apps with compose already and a backend with ktor. One could argue Android developers are the best target audience, since the additional knowledge needed to move an app to web with kobweb is minimal. I love it! 🤩"

## Connecting with us

* [Join my Discord!](https://discord.gg/5NZ2GKV5Cs)
* Follow me on Mastodon: [@bitspittle@fosstodon.org](https://fosstodon.org/@bitspittle)
* You can send direct queries to [my email](mailto:bitspittle@gmail.com)

If you're comfortable with it, using Discord is recommended, because there's a growing community of users in there who
can offer help even when I'm not around.

## Filing issues and leaving feedback

It is still early days, and while we believe we've proven the feasibility of this approach at this point, there's still
plenty of work to do to get to a 1.0 launch! We are hungry for the community's feedback, so please don't hesitate to:

* [Open an issue](https://github.com/varabyte/kobweb/issues/new/choose)
* Contact us (using any of the ways mentioned above) telling us what features you want
* Ask us for guidance, especially as there are no tutorials yet (your questions can help us know what to write first!)

Thank you for your support and interest in Kobweb!

## Showcase

[//]: # (Tell me if you don't want to idea)

Here are some projects that use kobweb

- [Empty](https://www.google.com)

Feel free to send your project, 
so we add it in the list