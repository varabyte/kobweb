package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.export
import com.varabyte.kobweb.gradle.application.util.PlaywrightCache
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * A task for generating an ID that can be used for naming a cache bucket on a CI.
 *
 * CIs can call this like so: `./gradlew -q kobwebBrowserCacheId`
 *
 * You can use this value, for example, when [creating a cache key](https://github.com/actions/cache#creating-a-cache-key).
 */
abstract class KobwebBrowserCacheIdTask @Inject constructor(kobwebBlock: KobwebBlock) : KobwebModuleTask(kobwebBlock, "Export the Kobweb project into a static site") {
    @TaskAction
    fun execute() {
        println("${kobwebBlock.export.browser.get().playwrightName}-${PlaywrightCache().version}")
    }
}