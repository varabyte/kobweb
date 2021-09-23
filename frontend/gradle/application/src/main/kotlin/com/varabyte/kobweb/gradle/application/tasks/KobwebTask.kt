package com.varabyte.kobweb.gradle.application.tasks

import org.gradle.api.DefaultTask

abstract class KobwebTask(desc: String) : DefaultTask() {
    init {
        group = "kobweb"
        description = desc
    }
}