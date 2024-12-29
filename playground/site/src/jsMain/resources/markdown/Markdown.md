---
root: .components.layouts.PageLayout("MARKDOWN")
imports:
  - .components.widgets.*
---

## Markdown Example

This site is generated from Markdown.

Create rich, dynamic web apps with ease, leveraging [Kotlin](https://kotlinlang.org/) and [Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html).

Markdown of course supports **bold**, _italic_, and _**bold italic**_ text.

It also supports

```
Multi-line
code blocks
```

and tables:

| Tables | Are    | Cool   |
|--------|--------|--------|
| cell 1 | cell 2 | cell 3 |

and `inline` code as well.

* And
* list
* items

and images:

![Kobweb logo](/logo.png "Unused title")

---

You can use blockquotes:

> [!QUOTE]
> The trouble with quotes on the internet is you never know if they are genuine.
>
> -- Abraham Lincoln

You can link to other Markdown documents with their route overrides resolved correctly (except for dynamic overrides):

[documents/INDEX.md](documents/INDEX.md)<br>
[KotlinLanguage.md](KotlinLanguage.md) (`routeOverride: languages/kotlin`)<br>
[documents/Bananas.md](documents/Bananas.md) (`routeOverride: /fruits/`)<br>
[files/external.md](/files/external.md) (Linking to a Markdown file outside of the processed markdown files)<br>

You can use <span id="md-inline-demo">inlined html</span> tags. You can inspect this page to see that "inlined html" is
wrapped in a span.

This is [an example][id] reference-style link.

Here is an example of [a link using `code` formatting](https://example.com).

You can also use block tags, like `<a>` and `<pre>`. Here, we use html blocks to create a Discord badge:

<a href="https://discord.gg/5NZ2GKV5Cs">
<img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>

```
<a href="https://discord.gg/5NZ2GKV5Cs">
<img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>
```

Alternately, you can use `{{{ code }}}` to call into Kotlin code, which itself can make Compose HTML calls.

For example, here is a widget that turns bullet points into folder icons:

{{{ Folders

* src
  * main
    * kotlin
      * main.kt
    * resources
      * public
        * favicon.ico

}}}

And though it may not appear it, the following link is actually provided by Kotlin code:

{{{ GoHomeLink }}}

[id]: http://example.com/
