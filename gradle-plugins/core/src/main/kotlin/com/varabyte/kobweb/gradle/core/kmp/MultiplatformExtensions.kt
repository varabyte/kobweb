package com.varabyte.kobweb.gradle.core.kmp

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

val Project.kotlin get() = extensions.getByName("kotlin") as KotlinMultiplatformExtension

fun Project.kotlin(configure: Action<KotlinMultiplatformExtension>): Unit =
    (this as ExtensionAware).extensions.configure("kotlin", configure)

fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
    (this as ExtensionAware).extensions.configure("sourceSets", configure)

val Project.buildTargets: NamedDomainObjectCollection<KotlinTarget>
    get() = kotlin.targets