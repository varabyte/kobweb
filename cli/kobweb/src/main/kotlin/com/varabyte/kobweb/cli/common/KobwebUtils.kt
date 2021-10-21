package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.konsole.runtime.KonsoleApp

val KonsoleApp.kobwebProject: KobwebProject?
    get() = try {
        KobwebProject()
    } catch (ex: KobwebException) {
        informError("This command must be called in the root of a Kobweb project.")
        null
    }


val KonsoleApp.kobwebFolder get() = kobwebProject?.kobwebFolder