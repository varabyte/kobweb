@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.core.extensions

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

/**
 * An enumeration of strategies to take when Kotlin informs us that a project's yarn.lock file has changed.
 *
 * In Kotlin 1.8, Kotlin/JS now warns users if a project's yarn.lock file has changed. This is something that
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
     * Have the build fail if it detects that a yarn.lock update is requested.
     *
     * This should give the user a chance to manually inspect their yarn.lock file / update it with a command like
     * `./gradlew kotlinUpgradeYarnLock`.
     *
     * This strategy will require quitting Kobweb immediately to address it, which may not be clear to new users, which
     * is why it's not chosen as the default. However, if someone knows what they're doing, setting the strategy to fail
     * is technically the safest option.
     *
     * @param rejectCreatingNewLock If set to true, this means reject not only yarn.lock being changed, but for yarn.lock
     *   being created in the first place. Usually this level of strictness isn't required, but project authors can
     *   include it if they want to verify that new users are getting a yarn.lock file that they already committed into
     *   source control.
     */
    class Fail(val rejectCreatingNewLock: Boolean = false) : YarnLockChangedStrategy()

    /**
     * Have the build leave the existing yarn lock as is.
     *
     * This should give the user a chance to manually inspect their yarn.lock file / update it with a command like
     * `./gradlew kotlinUpgradeYarnLock`.
     *
     * This is expected to be a very rarely used strategy. It is provided for users who know what they are doing -- for
     * example, they've manually updated their yarn.lock file on their own, they've removed it from the .gitignore file,
     * they've checked it into source control, and they want to allow their Kobweb site to build despite newer
     * dependencies being available.
     */
    object Ignore : YarnLockChangedStrategy()

    /**
     * Aggressively regenerate the yarn.lock file anytime it changes.
     *
     * This setting may be dangerous long term but is probably fine for new Compose HTML projects that don't
     * themselves add additional NPM dependencies (since if that broke, then anyone creating a new Compose HTML
     * project would also break).
     *
     * This is therefor chosen as a default strategy, since it's the least painful for new users, and is equivalent to
     * what behavior was like before Kotlin 1.8 (as I understand), but we may revisit this decision later.
     */
    object Regenerate : YarnLockChangedStrategy()
}

abstract class YarnBlock {
    /**
     * The strategy to employ when Kotlin notifies us that the project's yarn.lock file has changed.
     *
     * See [YarnLockChangedStrategy] for more details.
     */
    abstract val lockChangedStrategy: Property<YarnLockChangedStrategy>

    init {
        lockChangedStrategy.convention(YarnLockChangedStrategy.Regenerate)
    }
}

val KobwebBlock.yarn: YarnBlock
    get() = ((this as ExtensionAware).extensions["yarn"] as YarnBlock)

internal fun KobwebBlock.createYarnBlock() {
    (this as ExtensionAware).extensions.create<YarnBlock>("yarn")
}
