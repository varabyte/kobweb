package com.varabyte.kobweb.gradle.core.ksp

import com.google.devtools.ksp.gradle.KspExtension
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.ksp.KSP_API_PACKAGE_KEY
import com.varabyte.kobweb.ksp.KSP_APP_DATA_KEY
import com.varabyte.kobweb.ksp.KSP_PAGES_PACKAGE_KEY
import com.varabyte.kobweb.project.common.PackageUtils
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun setupKsp(project: Project, kobwebBlock: KobwebBlock, includeAppData: Boolean) {
    project.pluginManager.apply("com.google.devtools.ksp")

    val kspExtension = project.extensions.getByType<KspExtension>()

    val kspCommonMainKotlinMetadata =
        project.tasks.matching { it.name == "kspCommonMainKotlinMetadata" } // doesn't exist if js only

    project.configurations.matching { it.name == "kspJs" || it.name == "kspJvm" }.configureEach {
        project.dependencies {
            add(this@configureEach.name, kobwebBlock.kspProcessorDependency.get())
        }
    }

    project.tasks.matching { it.name == "kspKotlinJs" }.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        // however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_PAGES_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.pagesPackage.get()),
        )
        if (includeAppData) {
            kspExtension.arg(KSP_APP_DATA_KEY, "true")
        }
        dependsOn(kspCommonMainKotlinMetadata)
    }

    project.tasks.matching { it.name == "kspKotlinJvm" }.configureEach {
        // TODO: we currently set this kep using task.project.group since the group can be different from different modules
        // however, task.project is not recommended to be used, what are our alternatives?
        kspExtension.arg(
            KSP_API_PACKAGE_KEY,
            PackageUtils.resolvePackageShortcut(this.project.group.toString(), kobwebBlock.apiPackage.get()),
        )
        dependsOn(kspCommonMainKotlinMetadata)
    }
}
