package com.varabyte.kobweb.core

import androidx.compose.runtime.Composable

/**
 * Wraps a composable which is used to render an interactive, responsive html page.
 */
interface Page {
    @Composable
    fun render()
}