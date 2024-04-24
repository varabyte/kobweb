package com.varabyte.kobweb.gradle.core.util

import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.stream.createHTML
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import javax.inject.Inject

abstract class IndexHead {
    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    protected abstract val head: ListProperty<String>

    /**
     * Add a block of HTML elements to the `<head>` of the app's generated `index.html` file.
     *
     * The [block] is lazily evaluated.
     */
    fun add(block: HEAD.() -> Unit) {
        // Wrap computation with a provider in case it references any lazy properties
        head.add(providerFactory.provider { serializeHeadContents(block) })
    }

    /**
     * Set the HTML elements to be added to the `<head>` of the app's generated `index.html` file, overriding any
     * default and previously set values.
     *
     * The [block] is lazily evaluated.
     */
    fun set(block: HEAD.() -> Unit) {
        head.set(providerFactory.provider { listOf(serializeHeadContents(block)) })
        head.disallowChanges()
    }

    /**
     * Returns the value of the underlying list property as a single string containing all elements.
     *
     * This should only be called at task execution time.
     */
    fun get(): String = head.get().joinToString("")

    companion object {
        // Generate the html nodes without the containing <head> tag
        // See: https://github.com/Kotlin/kotlinx.html/issues/228
        private inline fun <T, C : TagConsumer<T>> C.headFragment(crossinline block: HEAD.() -> Unit): T {
            HEAD(emptyMap(), this).block()
            return this.finalize()
        }

        // Use `xhtmlCompatible = true` to include a closing slash as currently kotlinx.html needs them when adding raw text.
        // See: https://github.com/Kotlin/kotlinx.html/issues/247
        fun serializeHeadContents(block: HEAD.() -> Unit): String =
            createHTML(prettyPrint = false, xhtmlCompatible = true).headFragment(block)
    }
}
