import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

group = "playground.worker"
version = "1.0-SNAPSHOT"

val workerFileName = "worker.js"

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = workerFileName
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlinx.serialization.json)
        }
    }
}

val exposeWorkerScript by configurations.registering {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val outputFile = tasks.named("jsBrowserProductionWebpack").map { task ->
    task.outputs.files.first { it.name == "productionExecutable" }.resolve(workerFileName)
}
artifacts {
    add(exposeWorkerScript.name, outputFile) // dependency from jsBrowserProductionWebpack task is inferred
}

hackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode()

// For context, see: https://youtrack.jetbrains.com/issue/KT-55820/jsBrowserDevelopmentWebpack-in-continuous-mode-doesnt-keep-outputs-up-to-date
// It seems like the webpack tasks are broken when run in continuous mode (it has a special branch of logic for handling
// `isContinuous` mode and I guess it just needs more time to bake).
// Unfortunately, `kobweb run` lives and dies on its live reloading behavior. So in order to allow it to support
// webpack, we need to get a little dirty here, using reflection to basically force the webpack task to always take the
// non-continuous logic branch.
// Basically, we're setting this value to always be false:
// https://github.com/JetBrains/kotlin/blob/4af0f110c7053d753c92fd9caafb4be138fdafba/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/targets/js/webpack/KotlinWebpack.kt#L276
fun Project.hackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode() {
    tasks.withType<KotlinWebpack>().configureEach {
        // Gradle generates subclasses via bytecode generation magic. Here, we need to grab the superclass to find
        // the private field we want.
        this::class.java.superclass.declaredFields
            // Note: Isn't ever null for now but checking protects us against future changes to KotlinWebpack
            .firstOrNull { it.name == "isContinuous" }
            ?.let { isContinuousField ->
                isContinuousField.isAccessible = true
                isContinuousField.setBoolean(this, false)
            }
    }
}
