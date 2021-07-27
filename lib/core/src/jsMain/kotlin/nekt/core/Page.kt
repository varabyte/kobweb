package nekt.core

import androidx.compose.runtime.Composable

/**
 * Wraps a composable which is used to render an interactive, responsive html page.
 */
abstract class Page {
    @Composable
    abstract fun render()
}