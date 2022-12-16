Support for integration of material design icons in your project.

Note that, when this module is depended on, Kobweb adds the following entry to the `<head>` block in your document
template:

```html
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Two+Tone&family=Material+Icons+Round&family=Material+Icons+Sharp" />
```

which adds to the size of your page, something you should be mindful of if you don't plan to use any of these icons.

---

Note that this directory contains a file called `md-icon-list.txt`, which is parsed and used to generate code used in
this project.

To populate it, I visited the
[Material Design Icons GitHub repository](https://github.com/google/material-design-icons/tree/master/font), copied the
contents of each of the codepoint files, converted them into comma-separated lists, and deleted the ligatures.

I then manually reorganized the results (into default, outlined, rounded, sharp and two-toned subsets) and migrated the
temporary text file into the custom text format seen in the `md-icon-list.txt` file.