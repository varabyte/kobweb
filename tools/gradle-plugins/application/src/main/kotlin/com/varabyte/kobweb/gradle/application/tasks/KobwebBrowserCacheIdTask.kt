package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.Browser
import com.varabyte.kobweb.gradle.application.util.PlaywrightCache
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * A task for generating an ID that can be used for naming a cache bucket on a CI.
 *
 * CIs can call this like so: `./gradlew -q kobwebBrowserCacheId`
 *
 * You can use this value, for example, when [creating a cache key](https://github.com/actions/cache#creating-a-cache-key).
 */
abstract class KobwebBrowserCacheIdTask : KobwebTask("Export the Kobweb project into a static site") {
    @get:Input
    abstract val browser: Property<Browser>

    @TaskAction
    fun execute() {
        println("${browser.get().playwrightName}-${PlaywrightCache().version}")
    }
}
