package com.varabyte.kobweb.project.common

import kotlinx.serialization.json.Json

private val NonStrictJsonInstance by lazy {
    Json {
        // Ignore unknown keys, because otherwise we'll have a bad time with backwards compatibility (that is, metadata
        // written by a newer version of Kobweb, e.g. inside a library built with it, would fail to parse in a project
        // using an older version, EVEN IF the field we added didn't really matter that much.
        ignoreUnknownKeys = true
        // Hide defaults as it makes for potentially cleaner, leaner saved files.
        encodeDefaults = false
    }
}

/** A default Json configuration used by Kobweb, like `Yaml.nonStrictDefault` for yaml files. */
val Json.Default.nonStrictDefault get() = NonStrictJsonInstance
