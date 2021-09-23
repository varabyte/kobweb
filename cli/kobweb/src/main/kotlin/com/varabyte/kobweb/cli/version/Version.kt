package com.varabyte.kobweb.cli.version

import com.varabyte.konsole.foundation.konsoleApp
import com.varabyte.konsole.foundation.text.text
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.foundation.text.yellow

fun runVersion() = konsoleApp {
    konsole {
        textLine("kobweb ${System.getProperty("kobweb.version")}")
    }.run()
}