# Miscellaneous topics

## Table of contents
- [Miscellaneous topics](#miscellaneous-topics)
  - [Table of contents](#table-of-contents)
    - [Debugging your site](#debugging-your-site)
    - [Debugging the frontend](#debugging-the-frontend)
    - [Debugging the backend](#debugging-the-backend)
    - [Using a custom font](#using-a-custom-font)
      - [Font hosting service](#font-hosting-service)
      - [Self-hosted fonts](#self-hosted-fonts)

### Debugging your site

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

> [!NOTE]
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

> [!IMPORTANT]
> Remote debugging is only supported in dev mode. It will not be enabled for a server started with
> `kobweb run --env prod`.

With your Kobweb server running and your "remote debug" run configuration selected, press the debug button. If
everything is set up correctly, you should see a message in the IDE debugger console
like: `Connected to the target VM, address: 'localhost:5005', transport: 'socket'`

If instead you see a red popup with a message
like `Unable to open debugger port (localhost:5005): java.net.ConnectException "Connection refused"`, please
double-check the values in your `conf.yaml` file, restart the server, and try again.

### Using a custom font

#### Font hosting service

The easiest way to use a custom font is if it is already hosted for you. For example, Google Fonts provides a CDN that
you can use to load fonts from directly.

> [!NOTE]
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

#### Self-hosted fonts

Users can flexibly declare a custom font by using
CSS's [`@font-face` rule](https://developer.mozilla.org/en-US/docs/Web/CSS/@font-face).

In Kobweb, you can normally declare CSS properties in Kotlin (within an `@InitSilk` block), but unfortunately Firefox
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
> enough to support additional fonts if you decide to add more in the future, which is why we recommend it as general
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