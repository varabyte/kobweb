package com.varabyte.kobweb.cli.common

import com.varabyte.konsole.foundation.anim.KonsoleAnim
import java.time.Duration

object Anims {
    val ELLIPSIS = KonsoleAnim.Template(listOf("", ".", "..", "..."), Duration.ofMillis(250))
    val SPINNER = KonsoleAnim.Template(listOf("⠋", "⠙", "⠸", "⠴", "⠦", "⠇"), Duration.ofMillis(150))
}