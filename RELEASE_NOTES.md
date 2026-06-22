# Release Notes

## Features & Improvements
* Add support for Material Symbols
* Add Lucide Icons
* Support specifying an exact browser via path to use at export time
* Expose `org.slf4j` from Kobweb servers to user code so they can use it for logging
* Add support to set server system properties from Gradle
* `kobweb run --layout=static` will now reject dynamic routes even in dev mode
* Add `AnimationScope` modifier
* Add modifier for `object-position` property
* Add Modifier support for `imageRendering` and `imageOrientation` CSS styles
* Add support for `text-wrap` and `tab-size` CSS properties
* Add support for `Transform.None` (and global keywords)
* Update `Fetch` and `HttpFetcher` to allow calling them in a web worker script
* Add support for dispatchers on top of worker scopes
* Updated `ApiFetcherExtensions` and idiomatic Kotlin fetch calls to return `Response` objects instead of raw byte arrays
* Add support for multiple headers with the same key
* Change input enter event handling to use `evt.key` instead of `evt.code`
* Register the isolated class loader as parallel capable
* Dispatch calls to user code endpoints on the IO dispatcher
* Update `RequestBody` to force users to use `bodyOf` helper methods

## Bug Fixes & Stability
* Fix the server crashing if static routes aren't found
* Update jar manifest searching logic to ensure the server doesn't crash when run in an IDE
