package com.varabyte.kobweb.gradle.library.extensions

import com.varabyte.kobweb.gradle.core.extensions.IconsBlock
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

/** A library's `kobweb { library { icons { ... } } }` block. See [IconsBlock]. */
abstract class LibraryIconsBlock @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    layout: ProjectLayout,
) : IconsBlock<IconsBlock.IconSetBlock>(IconSetBlock::class.java, objects, providers, layout) {
    /**
     * Whether modules that depend on this one can use its icon bindings too. False by default.
     *
     * By default, icon bindings are internal, so each module (every library and the app) can generate the icons it
     * needs without naming conflicts. Set this to true when a module exists to share icons, e.g. a single icons
     * library for an entire project.
     */
    abstract val public: Property<Boolean>

    init {
        public.convention(false)
    }
}

val LibraryBlock.icons: LibraryIconsBlock
    get() = extensions.getByType<LibraryIconsBlock>()

internal fun LibraryBlock.createIconsBlock(): LibraryIconsBlock {
    return extensions.create<LibraryIconsBlock>("icons")
}
