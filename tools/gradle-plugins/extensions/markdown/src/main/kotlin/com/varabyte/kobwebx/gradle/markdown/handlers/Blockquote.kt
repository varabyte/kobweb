package com.varabyte.kobwebx.gradle.markdown.handlers

import com.varabyte.kobweb.project.common.PackageUtils
import org.commonmark.node.BlockQuote
import org.commonmark.node.Text

val SilkCalloutTypes = mapOf(
    "CAUTION" to "$SILK.display.CalloutType.CAUTION",
    "IMPORTANT" to "$SILK.display.CalloutType.IMPORTANT",
    "NOTE" to "$SILK.display.CalloutType.NOTE",
    "QUESTION" to "$SILK.display.CalloutType.QUESTION",
    "QUOTE" to "$SILK.display.CalloutType.QUOTE",
    "TIP" to "$SILK.display.CalloutType.TIP",
    "WARNING" to "$SILK.display.CalloutType.WARNING",
)

/**
 * Creates a handler for the blockquote type which delegates to the Silk `Callout` widget is using silk.
 *
 * IMPORTANT: It is an error to use this in a project that does not depend on Silk.
 *
 * When set, Silk can parse blockquotes with a special syntax to generate callouts. For example:
 *
 * ```
 * > [!NOTE]
 * > This is a note.
 * ```
 *
 * will generate a note callout.
 *
 * The default list of callout keywords are, by default, provided by [SilkCalloutTypes] map, but users can extend
 * this set themselves or provide their own by setting the [types] parameter:
 *
 * ```
 * markdown {
 *    handlers.blockquote = SilkCalloutBlockquoteHandler(
 *      types = SilkCalloutTypes + mapOf("CUSTOM" to ".components.widgets.callouts.CustomCalloutType")
 *    )
 * }
 * ```
 *
 * By default, the label for a callout is the type itself (e.g. "Note", "Tip", etc.). However, the label can be
 * overridden by users on a case-by-case basis by specifying the label inside the callout syntax. For
 * example: `[!NOTE "My Custom Label"]`.
 *
 * But you can change the default label globally, specifying it in the [labels] parameter. For example, if you want
 * to set the "QUOTE" type to have an empty label by default (which looks clean), you can set the [labels]
 * parameter to `mapOf("QUOTE" to "")`. NOTE: If you specify a key in [labels] that isn't also registered in
 * [types], it will essentially be ignored.
 *
 * Finally, you can specify an alternate callout variant to use (perhaps `OutlinedCalloutVariant` or something from
 * your own project) by setting the [variant] parameter.
 */
fun SilkCalloutBlockquoteHandler(
    types: Map<String, String> = SilkCalloutTypes,
    labels: Map<String, String> = emptyMap(),
    variant: String? = null,
): NodeScope.(BlockQuote) -> String {
    return { blockQuote ->
        val silkCallout = blockQuote.firstChild.firstChild?.let { firstChild ->
            firstChild as Text
            val regex = """\[!([^ ]+)( "(.*)")?]""".toRegex()
            val typeMatch = regex.find(firstChild.literal) ?: return@let null
            firstChild.literal = firstChild.literal.substringAfter(typeMatch.value)

            val typeId = typeMatch.groupValues[1]
            val isLabelSet = typeMatch.groupValues[2].isNotBlank()
            val label = typeMatch.groupValues[3].takeIf { isLabelSet } ?: labels[typeId]

            val calloutTypeFqn = types[typeId]?.let {
                PackageUtils.resolvePackageShortcut(data.getValue(MarkdownHandlers.DataKeys.ProjectGroup), it)
            }

            @Suppress("NAME_SHADOWING") val variant = variant ?: "$SILK.display.CalloutDefaults.Variant"

            if (calloutTypeFqn != null) {
                """$SILK.display.Callout(type = $calloutTypeFqn, label = ${label?.let { "\"$it\"" }}, variant = $variant)"""
            } else {
                """$SILK.display.Callout(type = $SILK.display.CalloutType.UNKNOWN, label = "Invalid callout type [!$typeId]", variant = $variant)"""
            }
        }
        silkCallout ?: "$KOBWEB_DOM.GenericTag(\"blockquote\")"
    }
}
