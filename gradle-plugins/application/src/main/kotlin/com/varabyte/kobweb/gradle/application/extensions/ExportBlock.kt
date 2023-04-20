@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.gradle.application.Browser
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.get

/**
 * A sub-block for defining properties related to configuring the `kobweb export` step.
 */
abstract class ExportBlock {
    /**
     * Which browser to use for the export step.
     *
     * Besides potentially affecting the snapshotted output and export times, this can also affect the download size.
     *
     * Chromium is chosen as a default due to its ubiquity, but Firefox may also be a good choice as its download size
     * is significantly smaller than Chromium's.
     */
    abstract val browser: Property<Browser>

    init {
        browser.convention(Browser.Chromium)
    }
}

val KobwebBlock.export: ExportBlock
    get() = ((this as ExtensionAware).extensions["export"] as ExportBlock)

internal fun KobwebBlock.createExportBlock() {
    (this as ExtensionAware).extensions.create("export", ExportBlock::class.java)
}
