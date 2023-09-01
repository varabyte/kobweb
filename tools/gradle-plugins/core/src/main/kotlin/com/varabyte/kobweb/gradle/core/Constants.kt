package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.util.namedOrNull
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

const val GENERATED_ROOT = "generated/kobweb"

const val KOBWEB_METADATA_SUBFOLDER = "META-INF/kobweb"
const val KOBWEB_METADATA_FRONTEND = "$KOBWEB_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_METADATA_BACKEND = "$KOBWEB_METADATA_SUBFOLDER/backend.json"

// TODO: ksp/js or ksp/{name}
val Project.kspFrontendFile: Provider<RegularFile>
    get() {
        return tasks.named("kspKotlinJs").map { kspTask ->
            RegularFile { kspTask.outputs.files.single { it.name == "frontend.json" } }
        }
    }

val Project.kspBackendFile: Provider<RegularFile>?
    get() {
        return tasks.namedOrNull("kspKotlinJvm")?.map { kspTask ->
            RegularFile { kspTask.outputs.files.single { it.name == "backend.json" } }
        }
    }
