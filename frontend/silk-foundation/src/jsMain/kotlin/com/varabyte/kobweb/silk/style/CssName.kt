package com.varabyte.kobweb.silk.style

/**
 * An annotation that can be applied to a [CssStyle] or [CssStyleVariant] property to override its default name.
 *
 * In the following example, the generated CSS class name would have been "info-bubble" by default but will be "info"
 * instead:
 *
 * ```
 * @CssName("info")
 * val InfoBubbleStyle = CssStyle { ... }
 * ```
 *
 * It is not expected many users will ever use this annotation, but it can be useful as a way to shorten long names or
 * deal with name collisions with a different style generated in another package.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class CssName(val name: String)

/**
 * An annotation that can be applied to a [CssStyle] or [CssStyleVariant] property to add a prefix to the generated CSS
 * class name.
 *
 * As an example, imagine we are developing a library called "kobweb-material-design". We might then choose a
 * prefix (say, "kmd") which, when applied to our style, would significantly reduce the chance of a name collision
 * with a user's style declared in their own project.
 *
 * The following code will generate a CSS class named "kmd-button":
 *
 * ```
 * @CssPrefix("kmd")
 * val ButtonStyle = CssStyle { ... }
 * ```
 *
 * This also makes it easy for users debugging their site to see at a glance which styles are coming from the library.
 *
 * **NOTE**: Library authors should prefer setting a global prefix in their build script instead of using this
 * annotation:
 *
 * ```
 * kobweb { library { cssPrefix.set("kmd") } }
 * ```
 *
 * Using this annotation will override the global prefix on the targeted style. Additionally, [prefix] can be set to
 * an empty string to remove the prefix entirely.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class CssPrefix(val prefix: String)
