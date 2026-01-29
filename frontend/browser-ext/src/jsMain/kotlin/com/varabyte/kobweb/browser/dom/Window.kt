package com.varabyte.kobweb.browser.dom

import org.w3c.dom.Window

fun Window.getSelection(): Selection? = asDynamic().getSelection() as Selection?
