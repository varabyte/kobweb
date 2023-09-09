package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.util.namedOrNull
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

const val GENERATED_ROOT = "generated/kobweb"


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
