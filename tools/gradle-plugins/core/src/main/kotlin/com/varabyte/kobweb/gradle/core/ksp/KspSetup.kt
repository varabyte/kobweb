package com.varabyte.kobweb.gradle.core.ksp

import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import com.varabyte.kobweb.gradle.core.extensions.kobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.JsTarget
import com.varabyte.kobweb.gradle.core.kmp.JvmTarget
import com.varabyte.kobweb.gradle.core.kmp.TargetPlatform
import com.varabyte.kobweb.ksp.KSP_API_PACKAGE_KEY
import com.varabyte.kobweb.ksp.KSP_APP_DATA_KEY
import com.varabyte.kobweb.ksp.KSP_PAGES_PACKAGE_KEY
import com.varabyte.kobweb.project.common.PackageUtils
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun Project.applyKspPlugin() = pluginManager.apply(KspGradleSubplugin::class.java)

fun Project.setupKspJs(target: JsTarget, includeAppData: Boolean) {
    addKspDependency(target)

    project.tasks.matching { it.name == target.kspKotlin }.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        //  however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_PAGES_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.pagesPackage.get()),
        )
        if (includeAppData) {
            kspExtension.arg(KSP_APP_DATA_KEY, "true")
        }
    }
}

fun Project.setupKspJvm(target: JvmTarget) {
    addKspDependency(target)

    project.tasks.matching { it.name == target.kspKotlin }.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        //  however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_API_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.apiPackage.get()),
        )
    }
}

private val Project.kspExtension: KspExtension
    get() = extensions.getByType<KspExtension>()

private fun Project.addKspDependency(target: TargetPlatform<*>) {
    val configurationName = "ksp${target.capitalizedName}"

    configurations.matching { it.name == configurationName }.configureEach {
        dependencies {
            add(this@configureEach.name, kobwebBlock.kspProcessorDependency.get())
        }
    }
}
