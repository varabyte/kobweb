package com.varabyte.kobweb.test.compose

import androidx.compose.runtime.*

fun callComposable(content: @Composable () -> Unit) = runComposeTest {
    composition(content)
}
