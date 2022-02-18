This is a [Kobweb](https://github.com/varabyte/kobweb) project instantiated from the `examples/opengl` template.

The purpose of this project is to demonstrate the `Canvas` composable to drive 3D, OpenGL content.

This sample was built starting from the tutorials at
https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Tutorial/

In addition to converting the tutorial's JavaScript to Kotlin, the other two interesting bits was the creation of
bindings into a JavaScript library it uses for dealing with matrices called
[glMatrix](https://github.com/toji/gl-matrix).

To support this, I both created the file `src/jsMain/kotlin/opengl/bindings/glmatrix.kt` and also added a link to the
library via an html script tag in this project's generated `index.html` file. This is done in the `build.gradle.kts`
file:

```kotlin
/*...*/

kobweb.index.head.add {
    script {
        src = "https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"
    }
}

/*...*/
```

which generates html that looks like:

```html
<html lang="en">
  <head>
    <!-- ... -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gl-matrix/3.4.2/gl-matrix-min.js"></script>
  </head>
  <!-- ... -->
</html>
```

---

To run the sample, simply enter the following command in the terminal:

```bash
kobweb run
```

and open [http://localhost:8080](http://localhost:8080) with your browser to see the result.