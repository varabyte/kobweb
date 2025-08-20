---
title: Unified Markdown Processing Test
date: 'July 8, 2025'
description: Testing unified markdown processing with remark-gfm and rehype-raw plugins
tags: [ 'kobweb', 'commonmark', 'gfm', 'test', 'md', 'kotlin' ]
posted: true
---

# Unified Markdown Processing Test

This content demonstrates markdown features on the **Kobweb Framework**, which uses
a [CommonMark](https://commonmark.org/) parser
with [GitHub Flavored Markdown (GFM) extensions](https://github.github.com/gfm/).

## GitHub Flavored Markdown Features

### Strikethrough

~~This text should be struck through~~ or ~one tilde~

### Tables
| Feature          | Status                             | Notes                         |
|------------------|------------------------------------|-------------------------------|
| Strikethrough    | * [x]                              | Crosses out text              |
| Tables           |  [x]                               | Organizes data in a grid      |
| Task Lists       |- [x]                               | Creates checklists |
| HTML in Markdown | - [x]  <mark>highlighted text</mark> | Allows embedding raw HTML tags |
| Code Blocks      | - [x]                               | Allows embedding code blocks  |
| Links            | - [x]                               | Allows embedding links        |
| Emphasis         | - [x]                               | Allows embedding emphasis     |
| Column A | Column B                           | Column C | Column D |
|----------| :---------                         |:--------:|---------:|
| Left     | Left                               |  Center  |    Right |
| Data 1   | Data 2                             |  Data 3  |   Data 4 |
| Row 2    | Row 2                              |  Row 2   |    Row 2 |

### Task Lists

- [x] Completed task
- [ ] Pending task
- [x] Another completed task
- [ ] Another pending task
- [x] Venus
- <input type="checkbox" disabled checked /> works

### HTML Elements in Markdown

This should work with <u>underlined text</u> and <mark>highlighted text</mark>.

<div style="background-color: #f0f0f0; padding: 10px; border-left: 4px solid #007acc;">
<strong>Note:</strong> This is HTML content inside markdown that should be preserved with rehype-raw.
</div>

### Code Blocks

```javascript
// This is a JavaScript code block
function hello() {
    console.log("Hello from unified markdown processing!");
}
```

### Links and Autolinks

Regular link: [Kobweb Documentation](https://kobweb.varabyte.com)

Autolink: https://kobweb.varabyte.com and www.example.com.

### Emphasis and Strong

*Italic text* and **bold text** and ***bold italic text***

## Footnote

A note can have a simple footnote.[^1] Here are some more examples:

- A footnote with a link.[^link]
- A footnote with multiple paragraphs.[^multi]
- A footnote with a long text.[^long]

[^1]: This is the first simple footnote.

[^link]: This footnote contains a link to [Kobweb](https://kobweb.varabyte.com).

[^multi]: This footnote has multiple paragraphs.
[foo]: https://example.com/foo

This is the second paragraph of the footnote.

[^long]: Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem
aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim
ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione
voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit,
sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.

---

✅ If you can see all the above features working correctly, then the CommonMark-java parser with its GFM extensions is functioning properly!