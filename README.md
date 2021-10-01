![version: 0.3.0](https://img.shields.io/badge/kobweb-v0.3.0-yellow)
<a href="https://discord.gg/bCdxPr7aTV">
  <img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>

# K🕸️bweb

Kobweb is an opinionated Kotlin framework for building websites and web apps, inspired by Next.js and Chakra UI. 

**It is currently in technology preview**. 

Its goal is to provide:

* an intuitive structure for organizing your website or web app
* automatic handling of routing between pages
* a collection of useful _batteries included_ widgets built on top of Web Compose
* an environment built from the ground up around live reloading
* an open source foundation that the community can extend
* and much, much more!

Here's a demo where we create a Web Compose website from scratch with Markdown support and live reloading, in under a
minute:

https://user-images.githubusercontent.com/43705986/135570277-2d67033a-f647-4b04-aac0-88f8992145ef.mp4

# Trying it out yourself

## Build the Kobweb binary

Once the code stabilizes a bit, we will host an artifact for downloading, but it's easy enough to build your own for
now.

```bash
cd /path/to/src/root
git clone https://github.com/varabyte/kobweb.git
cd kobweb
./gradlew :cli:kobweb:installDist
```

I recommend putting Kobweb in your path:

```bash
PATH=$PATH:/path/to/src/root/kobweb/cli/kobweb/build/install/kobweb/bin
kobweb version
```

## Create your Kobweb Site

```bash
cd /path/to/projects/root
kobweb create site
```

You'll be asked a bunch of questions required for setting up the project. When finished, you'll have a basic project
with three pages - a home page, an about page, and a markdown page - and some components (which are collections of
reusable, composable pieces). Your own directory structure should look something like:

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
        │               │  └── sections
        │               │     └── NavHeader.kt
        │               ├── MyApp.kt
        │               └── pages
        │                   ├── AboutPage.kt
        │                   └── Index.kt
        └── resources
            └── markdown
                └── Markdown.md

```

Note that there's no index.html or routing logic anywhere! We generate that for you automatically when you run Kobweb.
Which brings us to the next section...

## Run your Kobweb site

```bash
cd /path/to/projects/root/your-project
kobweb run
```

This spins up a webserver at http://localhost:8080. If you want to configure the port, you can do so by editing your
project's `.kobweb/conf.yaml` file.

You can open your project in IntelliJ and start editing it. While Kobweb is running, it will detect changes and deploy
updates to your site automatically.

## Built on Web Compose

To learn more about Web Compose, please visit [the official tutorials](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Getting_Started).

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

# Connecting with us

If you'd like to be kept in the loop on updates to this project, there are a few ways:

* [Join our Discord!](https://discord.gg/bCdxPr7aTV)
* Follow me on Twitter: [@bitspittle](https://twitter.com/bitspittle)
* [Send me an email](mailto:bitspittle@gmail.com)
  * **Note: I am just an individual person, but I promise not to harvest, distribute, or in any way use any emails I
receive except to 1) respond to any questions asked or 2) ping when the status of this project changes.**

Thank you for your interest!

# Filing issues and leaving feedback

It is still early days and while we believe we've proven the feasibility of this approach at this point, there's still
plenty of work to do to get to a 1.0 launch! We are hungry for the community's feedback, so please don't hesitate to:

* [Open an issue](https://github.com/varabyte/kobweb/issues)
* Contact us (using any of the ways mentioned above) telling us what features you want
* Ask us for guidance, especially as there are no tutorials yet (your questions can help us know what to write first!)