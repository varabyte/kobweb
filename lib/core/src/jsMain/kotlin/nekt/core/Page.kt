package nekt.core

import androidx.compose.runtime.Composable

/**
 * Wraps a composable which is used to render an interactive, responsive html page.
 *
 * @param slug The slug for this page. If null, one will be created using reflection
 * @param isIndex If true, this page will be rendered for the page's slug even if not
 *   explicitly typed by the user. For example, "/home" and "/", or "posts/all" and "posts/"
 *   It will be a runtime error if multiple index pages are found for the same slug part.
 */
abstract class Page(slug: Slug? = null, val isIndex: Boolean = false) {
    // TODO: Handle nested slugs via reflection, e.g. "pages.posts.SomePostPage" -> "posts/somepost"
    val slug = slug ?: Slug(this::class.js.name.removeSuffix("Page").lowercase())

    @Composable
    abstract fun render()

    open fun getTheme(): Theme = App.getInstance().getTheme()
}