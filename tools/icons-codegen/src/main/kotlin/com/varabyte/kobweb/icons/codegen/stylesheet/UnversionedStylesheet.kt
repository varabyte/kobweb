package com.varabyte.kobweb.icons.codegen.stylesheet

/** A stylesheet whose URL doesn't change with the version, so any version serves any bindings. */
class UnversionedStylesheet(private val url: String) : IconStylesheet {
    override fun urlFor(version: String) = url
    override fun versionFor(versions: Collection<String>) = versions.first()
    override fun serves(stylesheetVersion: String, bindingsVersion: String) = true
}
