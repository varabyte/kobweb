package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Window

@Suppress("NOTHING_TO_INLINE")
inline fun Window.getSelection(): Selection? = asDynamic().getSelection().unsafeCast<Selection?>()
