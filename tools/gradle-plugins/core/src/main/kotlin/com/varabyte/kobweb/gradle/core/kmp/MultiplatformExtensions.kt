package com.varabyte.kobweb.gradle.core.kmp

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

val Project.kotlin get() = extensions.getByName("kotlin") as KotlinMultiplatformExtension

val Project.buildTargets: NamedDomainObjectCollection<KotlinTarget>
    get() = kotlin.targets
