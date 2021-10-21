package com.varabyte.kobweb.cli.version

import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.textLine

fun handleVersion() = konsoleApp {
    konsole {
        textLine("kobweb ${System.getProperty("kobweb.version")}")
    }.run()
}