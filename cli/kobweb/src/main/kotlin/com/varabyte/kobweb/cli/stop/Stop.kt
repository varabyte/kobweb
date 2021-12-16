package com.varabyte.kobweb.cli.stop

import com.varabyte.kobweb.cli.common.KobwebGradle
import com.varabyte.kobweb.cli.common.assertKobwebProject
import com.varabyte.kobweb.cli.common.consumeProcessOutput
import com.varabyte.kobweb.server.api.ServerEnvironment

fun handleStop() {
    assertKobwebProject()
    // TODO(#79): Add an interactive mode and pass env in
    KobwebGradle(ServerEnvironment.PROD).stopServer().also { it.consumeProcessOutput(); it.waitFor() }
}