This small library exposes a simple API for declaring a server plugin that can be loaded by a Kobweb server at startup.

While the Kobweb server strives to expose useful configuration support through the `.kobweb/conf.yaml` file, you can use
this approach to extend Ktor in ways that are not supported.

To create and register a plugin yourself:

1. Add a `compileOnly` dependency on `com.varabyte.kobweb:server-plugin` in your project's build script.
2. Add implementation dependencies as needed on Ktor artifacts. You should check with Kobweb's README to see which
   version of Ktor it builds against.
3. Create a class that implements `KobwebServerPlugin`.
    ```kotlin
    package com.example
    class MyKobwebServerPlugin : KobwebServerPlugin {
        override fun Application.configure() {
            log("Placeholder log to represent configuring the Kobweb server's Ktor Application")
        }
    }
    ```
4. Add a `META-INF/services/com.varabyte.kobweb.server.api.KobwebServerPlugin` file in your project's resources folder,
  and add the fully qualified name of your class to it:
    ```
    com.example.MyKobwebServerPlugin
    ```
5. Build your project (e.g. `./gradlew jar`)
6. Copy the resulting jar file (found in `build/libs/`) into the `.kobweb/server/plugins` folder found in your Kobweb
   application.

**Be sure to familiarize yourself with the configuration already handled by the Kobweb server by reviewing
[the code found here](https://github.com/varabyte/kobweb/tree/main/backend/server/src/main/kotlin/com/varabyte/kobweb/server/plugins).**

---

If the flexibility provided by this plugin system isn't enough to accomplish what you need, or if you think Kobweb
should have supported your use-case out of the box, please consider [filing a feature request](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&template=feature_request.md&title=).