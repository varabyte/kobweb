package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property

// TODO: docs
interface FileGeneratingBlock : ExtensionAware {
    val genDir: Property<String>
}
