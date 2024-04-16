Support for integration of font awesome icons in your project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
```

which adds to the size of your page, something you should be mindful of if you don't plan to use any of these (awesome!)
icons.

---

Note that this directory contains a file called `fa-icon-list.txt`, which is parsed and used to generate code used in
this project.

To populate it, I visited the
[Font Awesome free icon search](https://fontawesome.com/search?o=a&m=free&f=classic%2Cbrands), inspected the site using
dev tools, and visited each of the 12 pages, copying the DOM subtree under the `<div id="icons-results">` element into a
temporary text file.

I then manually reorganized the results (into solid, regular, and brand subsets) and migrated the XML format into the
custom text format seen in the `fa-icon-list.txt` file (using regex search and replace).

I also removed the *brand* entry of the `font-awesome` icon, since there were already solid and regular versions of it,
and having an icon that spans all three categories confused my codegen logic.
