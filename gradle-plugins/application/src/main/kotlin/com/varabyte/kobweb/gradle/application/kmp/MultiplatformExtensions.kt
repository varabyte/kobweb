@file:Suppress("UNCHECKED_CAST")

package com.varabyte.kobweb.gradle.application.kmp

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

val Project.kotlin get() = extensions.getByName("kotlin") as KotlinMultiplatformExtension

fun Project.kotlin(configure: Action<KotlinMultiplatformExtension>): Unit =
    (this as ExtensionAware).extensions.configure("kotlin", configure)

fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
    (this as ExtensionAware).extensions.configure("sourceSets", configure)

val Project.buildTargets: NamedDomainObjectCollection<KotlinTarget>
    get() = kotlin.targets