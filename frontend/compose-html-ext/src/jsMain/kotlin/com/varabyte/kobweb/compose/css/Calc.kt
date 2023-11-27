package com.varabyte.kobweb.compose.css

import com.varabyte.kobweb.compose.css.functions.CalcScope
import com.varabyte.kobweb.compose.css.functions.CalcScopeInstance
import org.jetbrains.compose.web.css.*
import kotlin.experimental.ExperimentalTypeInference

// While operations with CSS numerics are already supported, the existing implementation does not properly handle using
// StyleVariables (CSS Variables) in calculations. So, we implement explicit calc() functions for handling these cases.

/**
 * Expresses a CSS calculation for a [CSSNumericValue], particularly useful when it involves a [StyleVariable].
 *
 * For example:
 * ```
 * val PaddingVar by StyleVariable(1.cssRem)
 * Modifier.padding(calc { (BasePaddingVar.value() + 2.px) * 2 })
 * ```
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@Deprecated("Use `com.varabyte.kobweb.compose.css.functions.calc` instead.")
fun <T : CSSNumericValue<*>> calc(action: CalcScope.() -> T): T = with(CalcScopeInstance, action)

@Deprecated("Use `com.varabyte.kobweb.compose.css.functions.calc` instead.")
fun <V : Number, T : CalcScope.CalcNum<V>> calc(action: CalcScope.() -> T): V =
    with(CalcScopeInstance, action).unsafeCast<V>()
