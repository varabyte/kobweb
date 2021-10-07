package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.konsole.runtime.KonsoleApp

val KonsoleApp.kobwebFolder: KobwebFolder? get() = KobwebFolder.inWorkingDirectory()
    ?: run {
        informError("This command must be called in the root of a Kobweb project.")
        null
    }