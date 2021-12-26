package com.varabyte.kobweb.cli.common

import com.varabyte.kotter.foundation.anim.Anim
import java.time.Duration

object Anims {
    val ELLIPSIS = Anim.Template(listOf("", ".", "..", "..."), Duration.ofMillis(250))
    val SPINNER = Anim.Template(listOf("⠋", "⠙", "⠸", "⠴", "⠦", "⠇"), Duration.ofMillis(150))
}