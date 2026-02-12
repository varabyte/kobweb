This release is a major update that overhauls the backend API, improves markdown handling, and raises the versions of **kotlin** to 2.3.10, **compose** (html) to 1.10.0, and **compose** (runtime) to 1.10.2 (now sourced from **androidx**).

```toml
[versions]
kobweb = "0.24.0"
kotlin = "2.3.10"
```

> [!IMPORTANT]
> Planning to upgrade? [Review instructions in the README](https://github.com/varabyte/kobweb#upgrading-kobweb-in-your-project).

⚠️ The Compose runtime dependency has been migrated from JetBrains Compose to [androidx Compose runtime](https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-190.html#multiplatform-targets-in-androidx-compose-runtime-runtime), which now supports the JS target starting with version 1.9.0. This should be transparent to most users.

## Changes

### Frontend

* Added missing bindings to support `window.getSelection` and `document.getSelection`
* Added new SVG icons
* Added `holdsIn` contracts to `Modifier.thenIf/Unless` and `CssStyleVariant.thenIf/Unless`
  * This allows the Kotlin compiler to smart-cast values within the `then` lambda block.
* Fixed `Modifier.displayBetween` which was broken since a prior rename
* Improved validation for dynamic route segments
  * Optional route segments (e.g. `{param?}`) are now only allowed in the final position of a route
  * An error is shown if an optional segment is unnecessary (i.e. the non-optional route already exists)
  * Conflicting dynamic route segments now produce an error at compile time
* Enabled the [unused return value checker](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0412-unused-return-value-checker.md) across the codebase
  * If you enable this Kotlin feature, you'll get warnings when you accidentally ignore return values from Kobweb APIs that should be used.

### Backend

* **Breaking:** Overhauled `Request.Body` and `Response.Body` APIs
  * `req.body` is now a rich `Request.Body` class (was `ByteArray?`). Use `req.body?.bytes()` or `req.body?.text()` to access the content.
  * `res.body` is now a rich `Response.Body` class (was `ByteArray?`). Construct a body using factory methods, e.g. `Response.Body.text(...)`.
  * Introduced `ByteSource`, a lightweight abstraction that supports streaming and lazy body access.
* Added support for multipart request bodies
  * Access via `req.body?.multipart()`. Individual parts expose their name, content type, and body.
  * Users can configure the form-field size limit per route via `kobweb.conf.yaml`.
* Added "bytes" versions of frontend HTTP helper methods (e.g. `window.api.getBytes(...)`)
  * Useful for fetching binary data such as images or files from API endpoints.
* Added `userData` properties on `Request` and `Response`
  * This allows passing custom data between API interceptors and API handlers.
* Fixed a crash where the Kobweb server could fetch the wrong JAR manifest file
* Fixed a KSP crash when using `@Api("")` on an API stream handler

### Silk

* Improved the error message shown when an illegal `attrs` modifier is used, now displaying the full CSS selector context

### Markdown

* Overhauled HTML handling in markdown to produce safer, more correct output
  * HTML attributes in markdown are now parsed and applied using `attrs` lambda blocks instead of string manipulation
  * Fixed multiple escaping issues (backslashes, dollar signs, newlines, html blocks, and inline html)
  * Safely handle HTML attributes that use tick delimiters containing quotes

### Gradle

* Fixed a `null` error that could occur in the Gradle task listener
* Set an explicit minimum supported Gradle version
* Internal build improvements (migrated away from `kotlin-dsl` plugin, added Gradle assignment plugin)

### Misc

* Removed deprecated APIs that had been deprecated for over 6 months
* Migrated the Compose runtime dependency to [androidx](https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-190.html#multiplatform-targets-in-androidx-compose-runtime-runtime)
* Updated many dependencies to their latest versions (Kotlin 2.3.10, KSP, ktor, Compose, and more)

## Thanks!

* @christomah0 contributed new SVG icons to the project.
* @DennisTsar continued to make significant contributions across the board, including the Compose runtime migration, markdown escaping fixes, the unused return value checker, and numerous code quality improvements.

---

**Full Changelog**: https://github.com/varabyte/kobweb/compare/v0.23.3...v0.24.0
