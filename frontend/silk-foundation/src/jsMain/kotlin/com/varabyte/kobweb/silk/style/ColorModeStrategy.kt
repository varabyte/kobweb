package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.style.ColorModeStrategy.BOTH
import com.varabyte.kobweb.silk.style.ColorModeStrategy.Companion.current
import com.varabyte.kobweb.silk.style.ColorModeStrategy.SCOPE
import com.varabyte.kobweb.silk.style.ColorModeStrategy.SUFFIX
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams


/**
 * An enum indicating how Silk will register color mode aware styles.
 *
 * A site will only use one strategy for its entire lifetime, which is determined by the environment it is running in:
 * - If the browser does not support the CSS `@scope` rule, the [SUFFIX] strategy is used.
 * - If the site is currently being exported, the [BOTH] strategy is used.
 * - Otherwise, the [SCOPE] strategy is used.
 *
 * See [current] for the current strategy being used by the site.
 */
enum class ColorModeStrategy {
    /**
     * Color mode aware styles are registered using the CSS `@scope` rule.
     *
     * When this strategy is used, using `ExampleStyle.toModifier()` will apply a single `example` class to the target
     * element. If the style is color mode aware, the appropriate style will automatically be applied based on the
     * nearest ancestor with a [ColorMode.cssClass] class.
     *
     * This is the preferred strategy, as it enables controlling style behavior entirely via CSS by toggling a class.
     * This allows changing the color mode of a page without loading and running the entire JS bundle, which is useful
     * for preventing a color mode flash when the color mode at export time does not match site's initial color mode.
     */
    SCOPE,

    /**
     * Color mode aware styles are registered using `_light` and `_dark` suffixes.
     *
     * When this strategy is used, using `ExampleStyle.toModifier()` will apply the `example` class to the target
     * element, as well as either `example_light` or `example_dark`, depending on the current color mode.
     *
     * This is the fallback strategy for browsers that do not support the CSS `@scope` rule.
     */
    SUFFIX,

    /**
     * Color mode aware styles are registered using both the CSS `@scope` rule and `_light`/`_dark` suffixes.
     *
     * This mode is used only when exporting a site, as it ensures the export contains CSS that browsers with and
     * without support for the `@scope` rule can interpret before the JS loads, preventing a flash of unstyled content.
     */
    BOTH;

    companion object {
        // During export, this parameter is set to `BOTH` (See KobwebExportTask)
        private val urlStrategy = URLSearchParams(window.location.search).get("_kobwebColorModeStrategy")

        // It's safe to initialize this once since the mode doesn't change during the lifetime of the app
        /** The [ColorModeStrategy] that is used by Silk for the lifetime of the app. */
        val current: ColorModeStrategy = when {
            !CSSScopeSupport -> SUFFIX
            else -> entries.firstOrNull { it.name == urlStrategy } ?: SCOPE
        }
    }
}

/**
 * Indicates whether color mode aware styles should be registered using the CSS `@scope` rule.
 *
 * Note that [useScope] and [useSuffix] are NOT mutually exclusive.
 */
val ColorModeStrategy.useScope get() = this != SUFFIX

/**
 * Indicates whether color mode aware styles should be registered using `_light` and `_dark` suffixes.
 *
 * Note that [useScope] and [useSuffix] are NOT mutually exclusive.
 */
val ColorModeStrategy.useSuffix get() = this != SCOPE
