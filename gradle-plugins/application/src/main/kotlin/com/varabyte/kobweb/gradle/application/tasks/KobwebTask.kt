package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.project.KobwebProject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

abstract class KobwebTask(desc: String) : DefaultTask() {
    @get:Internal
    val kobwebProject = KobwebProject(project.layout.projectDirectory.asFile.toPath())

    init {
        group = "kobweb"
        description = desc
    }
}