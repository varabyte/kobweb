package com.varabyte.kobweb.gradle.core.kmp

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

abstract class TargetPlatform<T : KotlinTarget> {
    abstract val name: String

    val mainSourceSet: String get() = "${name}Main"
    val srcSuffix: String get() = "/src/${mainSourceSet}/kotlin"
    val resourceSuffix: String get() = "/src/${mainSourceSet}/resources"

    val compileKotlin: String get() = "compileKotlin${name.capitalize()}"
    val compileClasspath: String get() = "${name}CompileClasspath"

    val jar get() = "${name}Jar"
    val processResources get() = "${name}ProcessResources"
}

class JsTarget(val kotlinTarget: KotlinJsIrTarget) : TargetPlatform<KotlinJsIrTarget>() {
    override val name: String = kotlinTarget.name

    val browserDevelopmentRun get() = "${kotlinTarget.name}BrowserDevelopmentRun"
    val browserProductionRun get() = "${kotlinTarget.name}BrowserProductionRun"
    val browserRun get() = "${kotlinTarget.name}BrowserRun"
    val run get() = "${kotlinTarget.name}Run"

    val browserProductionWebpack get() = "${kotlinTarget.name}BrowserProductionWebpack"

    val developmentExecutableCompileSync get() = "${kotlinTarget.name}DevelopmentExecutableCompileSync"
    val productionExecutableCompileSync get() = "${kotlinTarget.name}ProductionExecutableCompileSync"
}

val Project.jsTarget: JsTarget
    get() = JsTarget(buildTargets.withType<KotlinJsIrTarget>().single())


class JvmTarget(target: KotlinJvmTarget) : TargetPlatform<KotlinJvmTarget>() {
    override val name: String = target.name
}

val Project.jvmTarget: JvmTarget?
    get() = buildTargets.withType<KotlinJvmTarget>().singleOrNull()?.run(::JvmTarget)