package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.tasks.TaskCollection
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

// <IMPORTANT!>
// This method is public so that it can be called from other Kobweb plugins. However, a normal user should probably not
// be calling this method unless they know what they're doing.
// </IMPORTANT!>
// This method should be called like this:
//    project.tasks.withType<KotlinWebpack>().configureHackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode()
// and it disables the continuous mode logic in the webpack task.
// For context, see: https://youtrack.jetbrains.com/issue/KT-55820/jsBrowserDevelopmentWebpack-in-continuous-mode-doesnt-keep-outputs-up-to-date
// It seems like the webpack tasks are broken when run in continuous mode (it has a special branch of logic for handling
// `isContinuous` mode and I guess it just needs more time to bake).
// Unfortunately, `kobweb run` lives and dies on its live reloading behavior. So in order to allow it to support
// webpack, we need to get a little dirty here, using reflection to basically force the webpack task to always take the
// non-continuous logic branch.
// Basically, we're setting this value to always be false:
// https://github.com/JetBrains/kotlin/blob/4af0f110c7053d753c92fd9caafb4be138fdafba/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/targets/js/webpack/KotlinWebpack.kt#L276
fun TaskCollection<KotlinWebpack>.configureHackWorkaroundSinceWebpackTaskIsBrokenInContinuousMode() {
    this.configureEach {
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
