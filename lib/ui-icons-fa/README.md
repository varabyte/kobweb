Support for integration of font awesome icons in your project.

To support this module, you also need to add

```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />
```

to the `<head>` block in your document template.

Note that this directory contains a file called `fa-icon-list`, which is parsed and used to generate code used in this
project. This list was populated using instructions from [Stack Overflow](https://stackoverflow.com/a/33794368/1299302).
For example, visit [the Font Awesome Solid icons cheatsheet](https://fontawesome.com/v5/cheatsheet/free/solid) and,
in the developer console, run

```js
var names = new Set();
var icons = document.getElementsByClassName('icon');
for (const icon of icons) {
  const name = icon.getElementsByTagName('dd')[0].innerText;
  names.add(name);
}
console.log(JSON.stringify(Array.from(names)));
```

Copy the results, paste it into the text file, and then repeat for regular and brand icons.
