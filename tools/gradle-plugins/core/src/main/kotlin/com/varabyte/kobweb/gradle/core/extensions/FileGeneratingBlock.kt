package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.provider.Property

// TODO: docs
interface FileGeneratingBlock {
    val genDir: Property<String>
}
