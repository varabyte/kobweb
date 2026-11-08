@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/** Who can use the icon bindings generated from an [IconsBlock]. */
enum class IconVisibility {
    /**
     * Modules that depend on this one can use its icon bindings too.
     *
     * By default, icon bindings are internal, so each module (every library and the app) can generate the icons it
     * needs without naming conflicts. Use this instead when a module exists to share icons, e.g. a single icons
     * library for an entire project.
     */
    PUBLIC,

    /** Only this module can use the bindings. */
    INTERNAL,
}

/**
 * A block for generating Kotlin bindings for icon sets, such as Font Awesome or Lucide, at versions you choose.
 *
 * ```
 * kobweb {
 *     icons {
 *         lucide { version.set("1.70.0") }
 *     }
 * }
 * ```
 *
 * After adding an icon set or changing its version, run the `kobwebFetchIcons` task. It downloads the set's data into
 * this module's `kobweb-store/icons` folder, which should be checked in. Builds only read from that folder, never from
 * the network. To pick up newer releases for a version like "latest", run the task again.
 */
abstract class IconsBlock @Inject constructor(private val objects: ObjectFactory) {
    abstract class IconSetBlock {
        /**
         * The upstream version to generate bindings for: an exact version, a prefix like "1.+" for the newest matching
         * release, or "latest" (the default).
         *
         * `kobwebFetchIcons` resolves this to an exact version and records it, so builds keep using that version until
         * the next fetch.
         */
        abstract val version: Property<String>

        init {
            version.convention("latest")
        }
    }

    /**
     * The package that bindings go under, with each icon set in a subpackage named after it, e.g.
     * "com.example.components.icons.lucide".
     *
     * An initial '.' means this should be prefixed by the project group, e.g. ".components.icons" ->
     * "com.example.components.icons"
     */
    abstract val rootPackage: Property<String>

    abstract val visibility: Property<IconVisibility>

    private val _iconSets = mutableMapOf<String, IconSetBlock>()

    /** The configured icon sets, keyed by their id (e.g. "fa"), which is also the name of their subpackage. */
    internal val iconSets: Map<String, IconSetBlock> get() = _iconSets

    private fun iconSet(id: String, configure: Action<IconSetBlock>) {
        configure.execute(_iconSets.getOrPut(id) { objects.newInstance<IconSetBlock>() })
    }

    /** Font Awesome (https://fontawesome.com), whose versions are release tags, e.g. "6.5.2". */
    fun fa(configure: Action<IconSetBlock> = Action {}) = iconSet("fa", configure)

    /** Lucide (https://lucide.dev), whose versions are release tags, e.g. "1.70.0". */
    fun lucide(configure: Action<IconSetBlock> = Action {}) = iconSet("lucide", configure)

    /**
     * Google's Material Icons (https://developers.google.com/fonts/docs/material_icons), whose versions are commit SHAs
     * in https://github.com/google/material-design-icons.
     *
     * This is the older of Google's two Material icon sets. Its successor is Material Symbols ([ms]), which Google
     * publishes from the same repository.
     */
    fun mdi(configure: Action<IconSetBlock> = Action {}) = iconSet("mdi", configure)

    /**
     * Google's Material Symbols (https://developers.google.com/fonts/docs/material_symbols), whose versions are commit
     * SHAs in https://github.com/google/material-design-icons.
     *
     * This is the newer of Google's two Material icon sets, the successor to Material Icons ([mdi]). Google publishes
     * both from the same repository, despite its name.
     */
    fun ms(configure: Action<IconSetBlock> = Action {}) = iconSet("ms", configure)

    init {
        rootPackage.convention(".components.icons")
        visibility.convention(IconVisibility.INTERNAL)
    }
}

val KobwebBlock.icons: IconsBlock
    get() = extensions.getByType<IconsBlock>()

internal fun KobwebBlock.createIconsBlock(): IconsBlock {
    return extensions.create<IconsBlock>("icons")
}
