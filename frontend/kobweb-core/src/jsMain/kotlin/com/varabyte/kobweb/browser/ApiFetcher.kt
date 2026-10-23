package com.varabyte.kobweb.browser

import com.varabyte.kobweb.api.api as newApi
import org.w3c.dom.Window

@Deprecated(
    "`ApiFetcher` has moved to the `com.varabyte.kobweb.api` package.",
    ReplaceWith("ApiFetcher", "com.varabyte.kobweb.api.ApiFetcher")
)
typealias ApiFetcher = com.varabyte.kobweb.api.ApiFetcher

@Suppress("DeprecatedCallableAddReplaceWith") // ReplaceWith doesn't work well for extension properties
@Deprecated("Replace `import com.varabyte.kobweb.browser.api` with `import com.varabyte.kobweb.api.api`.")
val Window.api: com.varabyte.kobweb.api.ApiFetcher get() = this.newApi
