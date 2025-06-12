package com.varabyte.kobweb.gradle.publish

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Action
import org.gradle.api.Project

internal fun Project.mavenPublishing(configure: Action<MavenPublishBaseExtension>) {
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("mavenPublishing", configure)
}
