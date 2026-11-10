package com.varabyte.kobweb.icons.codegen.stylesheet

/** The stylesheet a page must load for an icon set's bindings to render. */
interface IconStylesheet {
    /** The URL of this stylesheet for bindings generated at [version]. */
    fun urlFor(version: String): String

    /**
     * Picks which of these [versions] to load the stylesheet for, when several modules generate bindings for the icon
     * set at different versions and the page can only load one.
     */
    fun versionFor(versions: Collection<String>): String

    /** Whether the stylesheet for [stylesheetVersion] renders every icon in bindings generated at [bindingsVersion]. */
    fun serves(stylesheetVersion: String, bindingsVersion: String): Boolean
}
