package com.varabyte.kobweb.cli.stop

import com.varabyte.kobweb.cli.common.KobwebGradle
import com.varabyte.kobweb.cli.common.assertKobwebProject
import com.varabyte.kobweb.cli.common.consumeProcessOutput

fun handleStop() {
    assertKobwebProject()
    KobwebGradle.stopServer().also { it.consumeProcessOutput(); it.waitFor() }
}