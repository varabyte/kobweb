@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

/**
 * A Gradle property that controls the default value of [YarnBlock.active].
 *
 * This is provided as a simple way to disable multiple Kobweb projects with one setting, which can be useful for
 * codebases that would prefer handling yarn configuration for themselves.
 */
internal const val KOBWEB_HANDLE_YARN_PLUGIN_CONFIGURATION = "kobweb.handleYarnPluginConfiguration"

/**
 * An enumeration of strategies to take when Kotlin informs us that a project's `yarn.lock` file has changed.
 *
 * In Kotlin 1.8, Kotlin/JS now warns users if a project's `yarn.lock` file has changed. This is something that
 * can be managed manually, for example by using Gradle to rebuild a lock file, and by checking it in.
 *
 * However, for most Kobweb users, this is not something they'd have to worry about by default, when just
 * creating a simple site without any of their own NPM dependencies. So Kobweb tries to hide this decisions
 * from users unless they want to opt into it.
 *
 * Please see the docs for the individual strategies for more information.
 *
 * See also: https://kotlinlang.org/docs/js-project-setup.html#reporting-that-yarn-lock-has-been-updated
 */
sealed class YarnLockChangedStrategy {
    companion object {
        val Fail = Fail()
    }

    /**
     * Have the build fail if it detects that a `yarn.lock` update is requested.
     *
     * This should give the user a chance to manually inspect their `yarn.lock` file / update it with a command like
     * `./gradlew kotlinUpgradeYarnLock`.
     *
     * This strategy will require quitting Kobweb immediately to address it, which may not be clear to new users, which
     * is why it's not chosen as the default. However, if someone knows what they're doing, setting the strategy to fail
     * is technically the safest option.
     *
     * @param rejectCreatingNewLock If set to true, this means reject not only `yarn.lock` being changed, but for
     *   `yarn.lock` being created in the first place. Usually this level of strictness isn't required, but project
     *   authors can include it if they want to verify that new users are getting a `yarn.lock` file that they already
     *   committed into source control.
     */
    class Fail(val rejectCreatingNewLock: Boolean = false) : YarnLockChangedStrategy()

    /**
     * Have the build leave the existing yarn lock as is.
     *
     * This should give the user a chance to manually inspect their `yarn.lock` file / update it with a command like
     * `./gradlew kotlinUpgradeYarnLock`.
     *
     * This is expected to be a very rarely used strategy. It is provided for users who know what they are doing -- for
     * example, they've manually updated their `yarn.lock` file on their own, they've removed it from the `.gitignore`
     * file, they've checked it into source control, and they want to allow their Kobweb site to build despite newer
     * dependencies being available.
     */
    object Ignore : YarnLockChangedStrategy()

    /**
     * Aggressively regenerate the `yarn.lock` file anytime it changes.
     *
     * This setting may be dangerous long term but is probably fine for new Compose HTML projects that don't
     * themselves add additional NPM dependencies. After all, if such initial dependencies were bad, then anyone
     * creating a new Compose HTML project from scratch would necessarily break.
     *
     * This is therefore chosen as a default strategy, since it provides the least painful experience for new users.
     */
    object Regenerate : YarnLockChangedStrategy()
}

abstract class YarnBlock @Inject constructor(providers: ProviderFactory) {
    /**
     * If set to true, the Kobweb core plugin will handle configuring the yarn plugin using values from this block for
     * this project.
     *
     * If set to false, the core plugin will skip configuring the yarn plugin entirely. This is provided as an option
     * for people who may want to handle configuring yarn themselves.
     *
     * This value defaults to true or to the value of the Gradle property `kobweb.handleYarnPluginConfiguration` if it
     * is set.
     */
    abstract val active: Property<Boolean>

    /**
     * The strategy to use when Kotlin notifies us that the project's `yarn.lock` file has changed.
     *
     * See [YarnLockChangedStrategy] for more details.
     */
    abstract val lockChangedStrategy: Property<YarnLockChangedStrategy>

    init {
        active.convention(
            providers
                .gradleProperty(KOBWEB_HANDLE_YARN_PLUGIN_CONFIGURATION)
                .map<Boolean> { it.toBooleanStrictOrNull() }
                .orElse(true)
        )

        lockChangedStrategy.convention(YarnLockChangedStrategy.Regenerate)
    }
}

val KobwebBlock.yarn: YarnBlock
    get() = extensions.getByType<YarnBlock>()

internal fun KobwebBlock.createYarnBlock(providers: ProviderFactory): YarnBlock {
    return extensions.create<YarnBlock>("yarn", providers)
}
