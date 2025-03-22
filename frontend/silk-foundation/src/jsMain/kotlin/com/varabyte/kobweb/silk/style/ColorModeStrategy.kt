package com.varabyte.kobweb.silk.style

import com.varabyte.kobweb.silk.style.ColorModeStrategy.BOTH
import com.varabyte.kobweb.silk.style.ColorModeStrategy.Companion.current
import com.varabyte.kobweb.silk.style.ColorModeStrategy.SCOPE
import com.varabyte.kobweb.silk.style.ColorModeStrategy.SUFFIX
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

/**
 * Whether the current browser supports the CSS `@scope` at-rule.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/@scope">@scope</a>
 */
private val CSSScopeSupport: Boolean = js("typeof CSSScopeRule != 'undefined'")

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

/**
 * Returns a list of CSS layers that will be used by the site, based on the given strategy.
 *
 * If the strategy is [SCOPE] or [SUFFIX], the original layers are returned.
 * When the strategy is [BOTH], each original layer is preceded by a corresponding "compatibility" layer. These
 * compatibility layers contain suffixed styles that also exist as `@scope`-based styles, meaning all styles in these
 * layers can be ignored if the browser supports `@scope`.
 */
internal fun ColorModeStrategy.getLayersWithCompat(layers: Set<String>): List<String> = when (this) {
    SCOPE, SUFFIX -> layers.toList()
    BOTH -> layers.flatMap { listOfNotNull(suffixedStyleLayer(it), it) }
}

/**
 * Returns the CSS layer that should be used for suffix-based styles.
 *
 * For [SCOPE] and [SUFFIX] mode, this is simply the original layer name, but in [BOTH] mode, these styles should be
 * registered into a separate layer.
 */
internal fun ColorModeStrategy.suffixedStyleLayer(layer: String?) = when (this) {
    SCOPE, SUFFIX -> layer
    BOTH -> layer?.let { "$it-compat" }
}
