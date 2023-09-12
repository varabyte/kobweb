package com.varabyte.kobweb.gradle.core

import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.util.namedOrNull
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_BACKEND
import com.varabyte.kobweb.ksp.KOBWEB_METADATA_FRONTEND
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

fun Project.kspFrontendFile(jsTarget: JsTarget): Provider<RegularFile> {
    return tasks.named(jsTarget.kspKotlin).map { kspTask ->
        RegularFile {
            kspTask.outputs.files.asFileTree.matching { include(KOBWEB_METADATA_FRONTEND) }.singleFile
        }
    }
}

fun Project.kspBackendFile(jvmTarget: JvmTarget): Provider<FileTree>? {
    return tasks.namedOrNull(jvmTarget.kspKotlin)?.map { kspTask ->
        kspTask.outputs.files.asFileTree.matching { include(KOBWEB_METADATA_BACKEND) }
    }
}
