package com.varabyte.kobweb.gradle.core.kmp

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

interface TargetPlatform<T : KotlinTarget> {
    val name: String

    val capitalizedName: String get() = name.replaceFirstChar { it.uppercase() }

    val mainSourceSet: String get() = "${name}Main"
    val srcSuffix: String get() = "/src/${mainSourceSet}/kotlin"
    val resourceSuffix: String get() = "/src/${mainSourceSet}/resources"

    val compileKotlin: String get() = "compileKotlin$capitalizedName"
    val compileClasspath: String get() = "${name}CompileClasspath"
    val runtimeClasspath: String get() = "${name}RuntimeClasspath"

    val jar get() = "${name}Jar"
    val processResources get() = "${name}ProcessResources"

    val kspKotlin get() = "kspKotlin$capitalizedName"
}

class JsTarget(kotlinTarget: KotlinJsIrTarget) : TargetPlatform<KotlinJsIrTarget> {
    override val name: String = kotlinTarget.name

    val browserDevelopmentRun get() = "${name}BrowserDevelopmentRun"
    val browserProductionRun get() = "${name}BrowserProductionRun"

    val browserDevelopmentWebpack get() = "${name}BrowserDevelopmentWebpack"
    val browserProductionWebpack get() = "${name}BrowserProductionWebpack"
    val browserDistribution get() = "${name}BrowserDistribution"

    val developmentExecutableCompileSync get() = "${name}DevelopmentExecutableCompileSync"
    val productionExecutableCompileSync get() = "${name}ProductionExecutableCompileSync"

    val compileDevelopmentExecutableKotlin get() = "compileDevelopmentExecutableKotlin${capitalizedName}"
    val compileProductionExecutableKotlin get() = "compileProductionExecutableKotlin${capitalizedName}"

    val sourcesJar get() = "${name}SourcesJar"
}

val Project.jsTarget: JsTarget
    get() = JsTarget(buildTargets.withType<KotlinJsIrTarget>().single())


class JvmTarget(target: KotlinJvmTarget) : TargetPlatform<KotlinJvmTarget> {
    override val name: String = target.name
}

val Project.jvmTarget: JvmTarget?
    get() = buildTargets.withType<KotlinJvmTarget>().singleOrNull()?.run(::JvmTarget)
