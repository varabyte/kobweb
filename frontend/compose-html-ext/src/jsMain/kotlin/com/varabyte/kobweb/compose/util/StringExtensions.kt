package com.varabyte.kobweb.compose.util

import com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary

/**
 * Convert a String for a name that is using TitleCamelCase into kebab-case.
 *
 * For example, "ExampleText" to "example-text"
 *
 * Note that there's special handling for acronyms, so "ExampleABC" will be converted to "example-abc", and "ABCExample"
 * will be converted to "abc-example" (not "example-a-b-c" and "a-b-c-example").
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.titleCamelCaseToKebabCase` instead (that is, `compose` → `browser`).")
fun String.titleCamelCaseToKebabCase() = titleCamelCaseToKebabCase()

/**
 * Convert a String for a name that is using kebab-case into TitleCamelCase.
 *
 * For example, "example-text" to "ExampleText"
 *
 * This is often but NOT ALWAYS the inverse of [titleCamelCaseToKebabCase], if there were acronyms in the original
 * text. For example, "ABCExample" will get converted to "abc-example", which, when inversed, will become "AbcExample".
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase` instead (that is, `compose` → `browser`).")
fun String.kebabCaseToTitleCamelCase() = kebabCaseToTitleCamelCase()


/**
 * Quote a CSS string (unless it is already quoted).
 *
 * CSS text content should always be surrounded by quotes, but this is a pretty subtle requirement that's easy to miss
 * and causes silent failures.
 *
 * This method will investigate a string that is intended to be used as CSS text content and wrap it with quotes (unless
 * it is already properly wrapped).
 *
 * For example:
 * * `test` -> `"test"`
 * * `"test"` -> `"test"` (unchanged)
 * * `"Is the test today?" he asked` -> `"\"Is the test today?\" he asked"`
 */
@Suppress("DeprecatedCallableAddReplaceWith") // Migrating deprecated extension methods is not a good experience
@Deprecated("We are migrating non-Compose utilities to a new artifact. Please change your imports to use `com.varabyte.kobweb.browser.util.wrapQuotesIfNecessary` instead (that is, `compose` → `browser`).")
fun String.wrapQuotesIfNecessary() = wrapQuotesIfNecessary()
