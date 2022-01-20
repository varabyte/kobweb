---
root: .components.layouts.PageLayout("MARKDOWN")
---

## Markdown Example

This site is generated from markdown.

Create rich, dynamic web apps with ease, leveraging [Kotlin](https://kotlinlang.org/) and [Web Compose](https://compose-web.ui.pages.jetbrains.team/).

Markdown also supports

```
Multi-line
code blocks
```

and `inline` code as well.

You can use <span id="md-inline-demo">inlined html</span> tags. You can inspect this page to see that "inlined html" is
wrapped in a span.

However, block tags (like `<pre>`, `<body>` etc.) are not supported. Instead, you can use `{{{ code }}}` to call into
Kotlin code. In fact, the following link is actually provided by Kotlin code:

{{{ .components.widgets.GoHomeLink }}}