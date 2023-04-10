package com.varabyte.kobweb.cli.common.version

import com.varabyte.kobweb.cli.common.template.KobwebTemplate
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.Session
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered

val kobwebCliVersion: SemVer.Parsed by lazy {
    SemVer.parse(System.getProperty("kobweb.version").substringBefore('-')) as SemVer.Parsed
}

/**
 * Returns true if the given template is supported by the current version of the Kobweb CLI.
 *
 * This assumes that the "minimumVersion" value in the template metadata was properly set. If it can't be parsed,
 * we silently hide it instead of crashing.
 */
val KobwebTemplate.versionIsSupported: Boolean get() {
    return (SemVer.parse(metadata.minimumVersion) as? SemVer.Parsed)?.let { minVersion ->
        minVersion <= kobwebCliVersion
    } ?: false
}

fun Session.reportUpdateAvailable(oldVersion: SemVer.Parsed, newVersion: SemVer.Parsed) {
    section {
        textLine()
        yellow {
            bordered(borderCharacters = BorderCharacters.CURVED, paddingLeftRight = 2, paddingTopBottom = 1) {
                white()
                text("Update available: ")
                black(isBright = true) {
                    text(oldVersion.toString())
                }
                text(" → ")
                green {
                    text(newVersion.toString())
                }
                textLine(); textLine()
                text("Please review the README at ")
                cyan(isBright = false) { text("https://github.com/varabyte/kobweb") }
                textLine(" for")
                textLine("instructions, and/or use your chosen package manager to upgrade to the")
                textLine("latest version.")
            }
        }
    }.run()
}