package com.varabyte.kobweb.cli.common

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.KobwebProject
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.foundation.text.yellow
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotter.runtime.render.RenderScope

fun assertKobwebProject(): KobwebProject {
    return try {
        KobwebProject()
    } catch (ex: KobwebException) {
        throw KobwebException("This command must be called in the root of a Kobweb project.")
    }
}

fun Session.findKobwebProject(): KobwebProject? {
    return try {
        assertKobwebProject()
    } catch (ex: KobwebException) {
        informError(ex.message!!)
        null
    }
}

fun Session.showStaticSiteLayoutWarning() {
    section {
        // TODO(#123): Link to URL doc link when available.
        yellow { textLine("Static site layout chosen. Some Kobweb features like server routes are unavailable in this configuration.") }
        textLine()
    }.run()
}

fun RenderScope.showDownloadDelayWarning() {
    yellow { textLine("Output may seem to pause for a while if Kobweb needs to download new dependencies.") }
    textLine()
}
