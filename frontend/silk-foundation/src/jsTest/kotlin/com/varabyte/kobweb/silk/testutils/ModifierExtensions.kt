package com.varabyte.kobweb.silk.testutils

import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

/** Return an [AttrsScope] whose contents can be queried. */
fun Modifier.toTestAttrs() = ComparableAttrsScope<Element>().apply(this.toAttrs())
