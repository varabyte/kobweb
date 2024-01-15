package com.varabyte.kobweb.gradle.application.artifacts

import com.varabyte.kobweb.gradle.application.KOBWEB_SERVER_PLUGIN_CONFIGURATION_NAME
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

val Project.kobwebServerPlugin: NamedDomainObjectProvider<Configuration>
    get() = configurations.named(
        KOBWEB_SERVER_PLUGIN_CONFIGURATION_NAME
    )
