package com.varabyte.kobweb.compose.ui.modifiers

/**
 * A simple enum which can be used for resolving ambiguous cases with some Modifier setters.
 *
 * For example, `width` can be the name of a style property OR an element attribute.
 */
enum class WebModifierType {
    ATTRS,
    STYLE
}