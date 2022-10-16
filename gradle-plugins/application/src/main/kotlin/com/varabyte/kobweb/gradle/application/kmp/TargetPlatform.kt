package com.varabyte.kobweb.gradle.application.kmp

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

interface TargetPlatform<T : KotlinTarget> {
    val name: String

    val mainSourceSet: String get() = "${name}Main"
    val srcSuffix: String get() = "/src/${mainSourceSet}/kotlin"
    val resourceSuffix: String get() = "/src/${mainSourceSet}/resources"

    val compileKotlin: String
}

class JsTarget(private val target: KotlinJsIrTarget) : TargetPlatform<KotlinJsIrTarget> {
    override val name: String = target.name

    val browserDevelopmentRun get() = "${target.name}BrowserDevelopmentRun"
    val browserProductionRun get() = "${target.name}BrowserProductionRun"
    val browserRun get() = "${target.name}BrowserRun"
    val run get() = "${target.name}Run"

    override val compileKotlin get() = "compileKotlin${target.name.capitalize()}"
    val processResources get() = "${target.name}ProcessResources"

    val browserDevelopmentWebpack get() = "${target.name}BrowserDevelopmentWebpack"
    val browserProductionWebpack get() = "${target.name}BrowserProductionWebpack"

    val developmentExecutableCompileSync get() = "${target.name}DevelopmentExecutableCompileSync"
    val productionExecutableCompileSync get() = "${target.name}ProductionExecutableCompileSync"
}

val Project.jsTarget: JsTarget
    get() = JsTarget(buildTargets.withType<KotlinJsIrTarget>().single())


class JvmTarget(private val target: KotlinJvmTarget) : TargetPlatform<KotlinJvmTarget> {
    override val name: String = target.name

    override val compileKotlin get() = "compileKotlin${target.name.capitalize()}"

    val jar get() = "${target.name}Jar"
}

val Project.jvmTarget: JvmTarget?
    get() = buildTargets.withType<KotlinJvmTarget>().singleOrNull()?.run(::JvmTarget)