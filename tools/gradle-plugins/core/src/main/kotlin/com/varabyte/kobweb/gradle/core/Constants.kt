package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.kmp.jsTarget
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

const val GENERATED_ROOT = "generated/kobweb"

const val KOBWEB_METADATA_SUBFOLDER = "META-INF/kobweb"
const val KOBWEB_METADATA_FRONTEND = "$KOBWEB_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_METADATA_BACKEND = "$KOBWEB_METADATA_SUBFOLDER/backend.json"

// TODO: ksp/js or jsp/{name}
val Project.kspFrontendFile: Provider<RegularFile>
    get() = layout.buildDirectory.file("generated/ksp/js/${jsTarget.name}Main/resources/frontend.json")

val Project.kspBackendFile: Provider<RegularFile>?
    get() {
        val fileProvider = jvmTarget
            ?.let { layout.buildDirectory.file("generated/ksp/jvm/${it.name}Main/resources/backend.json") }

        return fileProvider
//        return provider { fileProvider?.get()?.takeIf { it.asFile.exists() } } // TODO: does this conform to lazy best practices
    }
//    get() = provider { jvmTarget?.let { layout.buildDirectory.file("generated/ksp/jvm/${it.name}Main/backend.json")  }//jvmTarget?.let { layout.buildDirectory.file("generated/ksp/jvm/${it.name}Main/backend.json") }
