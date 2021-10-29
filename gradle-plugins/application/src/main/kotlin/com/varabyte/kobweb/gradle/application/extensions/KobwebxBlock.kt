@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

/**
 * A bare class which should be used as a home for Kobwebx extensions when their own plugin is applied.
 *
 * For example, after you apply the kobwebx.markdown plugin, you can do this:
 *
 * ```
 * kobwebx {
 *   markdown {
 *     ...
 *   }
 * }
 */
abstract class KobwebxBlock