@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.gradle.core.extensions.IconsBlock
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

/** An app's `kobweb { app { icons { ... } } }` block. See [IconsBlock]. */
abstract class AppIconsBlock @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    layout: ProjectLayout,
) : IconsBlock<AppIconsBlock.AppIconSetBlock>(AppIconSetBlock::class.java, objects, providers, layout) {
    abstract class AppIconSetBlock : IconSetBlock() {
        /**
         * Whether to warn when a library generated bindings for this icon set at a version that the stylesheet this
         * site loads might not fully support, since some of that library's icons may render blank.
         *
         * You are welcome to turn the warning off once you've checked that those icons render.
         */
        abstract val warnOnVersionMismatch: Property<Boolean>

        init {
            warnOnVersionMismatch.convention(true)
        }
    }
}

val AppBlock.icons: AppIconsBlock
    get() = extensions.getByType<AppIconsBlock>()

internal fun AppBlock.createIconsBlock(): AppIconsBlock {
    return extensions.create<AppIconsBlock>("icons")
}
