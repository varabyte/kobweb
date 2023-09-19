package com.varabyte.kobweb.silk.ui

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier

/**
 * A version of `Modifier.thenIf` that works within `@Composable` methods, which is particularly useful in Silk as
 * being color aware means a lot of modifier-generating methods themselves are `@Composable`.
 */
@Composable
fun Modifier.thenIf(condition: Boolean, lazyProduce: @Composable () -> Modifier): Modifier {
    return this.then(if (condition) lazyProduce() else Modifier)
}

/**
 * A version of `Modifier.thenUnless` that works within `@Composable` methods, which is particularly useful in Silk as
 * being color aware means a lot of modifier-generating methods themselves are `@Composable`.
 */
@Composable
fun Modifier.thenUnless(condition: Boolean, lazyProduce: @Composable () -> Modifier): Modifier {
    return this.thenIf(!condition, lazyProduce)
}
