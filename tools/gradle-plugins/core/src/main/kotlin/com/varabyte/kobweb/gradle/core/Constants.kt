package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.util.namedOrNull
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

val Project.kspFrontendFile: Provider<RegularFile>
    get() = tasks.named("kspKotlinJs").map { kspTask ->
        RegularFile {
            kspTask.outputs.files.asFileTree.matching { include(KOBWEB_METADATA_FRONTEND) }.singleFile
        }
    }

val Project.kspBackendFile: Provider<FileTree>?
    get() = tasks.namedOrNull("kspKotlinJvm")?.map { kspTask ->
        kspTask.outputs.files.asFileTree.matching { include(KOBWEB_METADATA_BACKEND) }
    }
