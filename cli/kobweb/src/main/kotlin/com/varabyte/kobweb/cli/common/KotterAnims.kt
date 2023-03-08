package com.varabyte.kobweb.cli.common

import com.varabyte.kotter.foundation.anim.TextAnim
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Anims {
    val ELLIPSIS = TextAnim.Template(listOf("", ".", "..", "..."), 250.milliseconds)
    val SPINNER = TextAnim.Template(listOf("⠋", "⠙", "⠸", "⠴", "⠦", "⠇"), 150.milliseconds)
}