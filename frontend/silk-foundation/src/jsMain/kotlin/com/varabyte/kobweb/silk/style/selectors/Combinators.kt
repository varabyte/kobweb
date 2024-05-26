package com.varabyte.kobweb.silk.style.selectors

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.StyleScope

fun StyleScope.children(vararg elements: String, createModifier: () -> Modifier) =
    cssRule(" > :is(${elements.joinToString()})", createModifier)

fun StyleScope.descendants(vararg elements: String, createModifier: () -> Modifier) =
    cssRule(" :is(${elements.joinToString()})", createModifier)

fun StyleScope.nextSiblings(vararg elements: String, createModifier: () -> Modifier) =
    cssRule(" + :is(${elements.joinToString()})", createModifier)

fun StyleScope.subsequentSiblings(vararg elements: String, createModifier: () -> Modifier) =
    cssRule(" ~ :is(${elements.joinToString()})", createModifier)
