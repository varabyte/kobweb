Class for extending a Kobweb project that wants to use the new Chrome AI API.

### Usage

#### Text Example

```kotlin
@Composable
fun TextAiExample() {
    var session: TextSession? = null
    var text by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        session = ai.createTextSession().await()
        text = session?.prompt("Hello, world!")?.await()
    }
    Text(text)
}
```

#### Streaming Example

```kotlin
@Composable
fun StreamingAiExample() {
    var session: TextSession? = null
    var text by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        session = ai.createTextSession().await()
        val stream = session.promptStreaming("Hello, what's your name?").getReader()
        while (true) {
            when (val result = readableStream.read().await()) {
                is ReadableStreamReadDoneResult -> break
                is ReadableStreamReadValueResult -> {
                    text = result.value
                }
            }
        }
    }
    Text(text)
}
```