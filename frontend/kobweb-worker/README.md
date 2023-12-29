Utility classes which help the user define type-safe web-worker APIs.

These classes work hand in hand with the Kobweb Worker Gradle plugin, which will look for a single implementation of the
`WorkerStrategy` class somewhere in the user's codebase.

Using vanilla web workers, the implementation for an echoing web worker would look something like this:

```kotlin
// Worker module

external val self: DedicatedWorkerGlobalScope

fun main() {
    self.onmessage = { m: MessageEvent ->
        self.postMessage("Echoed: ${m.data}")
    }
}

// Site module

@Composable
fun SomePage() {
    val worker = remember { Worker("worker.js") }
    worker.onmessage = { m: MessageEvent -> println("${m.data}") }
    worker.postMessage("Hello, world!")
}
```

Using Kobweb Workers, the implementation is similar but type-safe:

```kotlin
// Worker module

internal class EchoWorkerStrategy : WorkerStrategy<String, String> {
    override fun onInput(input: String) {
        postOutput("Echoed: $input")
    }
}

// Site module

@Composable
fun SomePage() {
    val worker = remember {
        EchoWorker() { message -> println(message) }
    }
    worker.postMessage("Hello, world!")
}
```

See also: [Kobweb Worker Gradle Plugin](../../tools/gradle-plugins/worker/README.md)
