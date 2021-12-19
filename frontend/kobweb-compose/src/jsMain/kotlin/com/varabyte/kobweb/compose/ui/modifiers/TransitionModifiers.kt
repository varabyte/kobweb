package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.TransitionDuration
import com.varabyte.kobweb.compose.css.TransitionProperty
import com.varabyte.kobweb.compose.css.transitionDuration
import com.varabyte.kobweb.compose.css.transitionProperty
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitTime

fun Modifier.transition(value: String) = styleModifier {
    property("transition", value)
}

fun Modifier.transitionProperty(vararg properties: String) = styleModifier {
    transitionProperty(*properties)
}

fun Modifier.transitionProperty(property: TransitionProperty) = styleModifier {
    transitionProperty(property)
}

fun Modifier.transitionDuration(vararg durations: CSSSizeValue<out CSSUnitTime>) = styleModifier {
    transitionDuration(*durations)
}

fun Modifier.transitionDuration(duration: TransitionDuration) = styleModifier {
    transitionDuration(duration)
}