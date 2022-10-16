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

    val browserDevelopmentRun by lazy { "${target.name}BrowserDevelopmentRun" }
    val browserProductionRun by lazy { "${target.name}BrowserProductionRun" }
    val browserRun by lazy { "${target.name}BrowserRun" }
    val run by lazy { "${target.name}Run" }

    override val compileKotlin by lazy { "compileKotlin${target.name.capitalize()}" }
    val processResources by lazy { "${target.name}ProcessResources" }

    val browserDevelopmentWebpack by lazy { "${target.name}BrowserDevelopmentWebpack" }
    val browserProductionWebpack by lazy { "${target.name}BrowserProductionWebpack" }

    val developmentExecutableCompileSync by lazy { "${target.name}DevelopmentExecutableCompileSync" }
    val productionExecutableCompileSync by lazy { "${target.name}ProductionExecutableCompileSync" }
}

val Project.jsTarget: JsTarget
    get() = JsTarget(buildTargets.withType<KotlinJsIrTarget>().single())


class JvmTarget(private val target: KotlinJvmTarget) : TargetPlatform<KotlinJvmTarget> {
    override val name: String = target.name

    override val compileKotlin by lazy { "compileKotlin${target.name.capitalize()}" }

    val jar by lazy { "${target.name}Jar" }
}

val Project.jvmTarget: JvmTarget?
    get() = buildTargets.withType<KotlinJvmTarget>().singleOrNull()?.run(::JvmTarget)