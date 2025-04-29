package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.Content
import com.varabyte.kobweb.compose.css.content
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.appearance(appearance: Appearance) = styleModifier {
    appearance(appearance)
}

fun Modifier.classNames(vararg classes: String) = attrsModifier {
    classes(*classes)
}

fun Modifier.classNames(classes: List<String>) = classNames(*classes.toTypedArray())

fun Modifier.content(content: Content) = styleModifier {
    content(content)
}

fun Modifier.content(vararg contents: Content.Listable) = styleModifier {
    content(Content.list(*contents))
}

fun Modifier.content(contents: List<Content.Listable>) = content(*contents.toTypedArray())

fun Modifier.content(altText: String, vararg contents: Content.Listable) = styleModifier {
    content(Content.list(altText, *contents))
}

fun Modifier.content(altText: String, contents: List<Content.Listable>) = content(altText, *contents.toTypedArray())

fun Modifier.content(value: String) = styleModifier {
    content(value)
}

fun Modifier.hidden() = attrsModifier {
    hidden()
}

fun Modifier.id(value: String) = attrsModifier {
    id(value)
}

fun Modifier.title(value: String) = attrsModifier {
    title(value)
}
