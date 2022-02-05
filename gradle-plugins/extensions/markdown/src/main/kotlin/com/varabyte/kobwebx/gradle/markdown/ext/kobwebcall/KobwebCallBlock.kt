package com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall

import org.commonmark.node.CustomBlock

/**
 * The block object that owns the nodes which will be used to extract the text for the Kobweb call from.
 *
 * For example, `{{{ .components.widgets.Example }}}` will create a block with a single [KobwebCall] node as its child.
 *
 * Call blocks can nest content which will get added as indented children to the final code. In other words, something
 * like this:
 *
 * ```markdown
 * {{{ .components.widgets.Warning
 * This API is deprecated and will be removed before v2.0 is published.
 * }}}
 * ```
 *
 * would produce Kotlin code like:
 *
 * ```
 * com.mysite.components.widgets.Warning {
 *   Text("This API is deprecated and will be removed before v2.0 is published.")
 * }
 * ```
 */
class KobwebCallBlock : CustomBlock()