package com.varabyte.kobweb.cli.common.version

import com.varabyte.kobweb.cli.common.template.KobwebTemplate

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