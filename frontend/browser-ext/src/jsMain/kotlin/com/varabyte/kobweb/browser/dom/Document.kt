package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Document

@Suppress("NOTHING_TO_INLINE")
inline fun Document.getSelection(): Selection? = asDynamic().getSelection().unsafeCast<Selection?>()
