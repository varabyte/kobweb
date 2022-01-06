package com.varabyte.kobweb.cli.common

import com.varabyte.kotter.foundation.anim.TextAnim
import java.time.Duration

object Anims {
    val ELLIPSIS = TextAnim.Template(listOf("", ".", "..", "..."), Duration.ofMillis(250))
    val SPINNER = TextAnim.Template(listOf("⠋", "⠙", "⠸", "⠴", "⠦", "⠇"), Duration.ofMillis(150))
}