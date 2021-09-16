package com.varabyte.kobweb.plugins.kobweb

import org.gradle.api.Plugin
import org.gradle.api.Project

class KobwebPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // TODO: Set up an extension so users can configure kobweb settings if necessary (or should we just search for
        //  a kobweb.conf.yaml file?
    }
}