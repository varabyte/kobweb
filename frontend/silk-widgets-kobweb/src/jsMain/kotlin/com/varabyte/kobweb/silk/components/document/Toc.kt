package com.varabyte.kobweb.silk.components.document

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.refScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.Element
import org.w3c.dom.HTMLCollection
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.get

object TocVars {
    val BorderColor by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
}

val TocStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .listStyle(ListStyleType.None)
        .textAlign(TextAlign.Start)
        .padding(0.cssRem) // Clear default UL padding
}

val TocBorderedVariant by TocStyle.addVariantBase {
    Modifier
        .borderRadius(5.px)
        .border(1.px, LineStyle.Solid, TocVars.BorderColor.value())
        .padding(1.cssRem)
}

private fun HTMLCollection.walk(onEach: (Element) -> Boolean) {
    (0 until length)
        .mapNotNull { i: Int -> this[i] }
        .forEach { child ->
            if (onEach(child)) {
                child.children.walk(onEach)
            }
        }
}

private class TocEntry(val text: String, val id: String, val indent: Int)

/**
 * Generates a table of contents for the current page, by searching the page for header elements with IDs.
 *
 * It's important that each header element has an ID, as this is what the TOC will link to. This is a standard format
 * output by markdown, but you may need to add IDs manually if you're adding Composables directly:
 *
 * ```
 * Toc()
 * H2(Modifier.id("h1").toAttrs()) { Text("Header 1") }
 * H3(Modifier.id("h1s1").toAttrs()) { Text("Subheader 1.1") }
 * H3(Modifier.id("h1s2").toAttrs()) { Text("Subheader 1.2") }
 * H2(Modifier.id("h2").toAttrs()) { Text("Header 2") }
 * H2(Modifier.id("h3").toAttrs()) { Text("Header 3") }
 * H3(Modifier.id("h3s1").toAttrs()) { Text("Subheader 3.1") }
 * ```
 *
 * @param minHeaderLevel The minimum header level to start paying attention to; any lower level headers will be skipped
 *   over. This defaults to 2 and not 1 because `H1` is usually the title of the page and not included in the TOC.
 * @param maxHeaderLevel The maximum header level to pay attention to; any higher level headers will be skipped over.
 */
@Composable
fun Toc(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    minHeaderLevel: Int = 2,
    maxHeaderLevel: Int = 3,
    indent: CSSNumeric = 1.cssRem,
    ref: ElementRefScope<HTMLUListElement>? = null,
) {
    require(minHeaderLevel in 1..6) { "Toc minHeaderLevel must be in range 1..6, got $minHeaderLevel" }
    require(maxHeaderLevel in 1..6) { "Toc maxHeaderLevel must be in range 1..6, got $maxHeaderLevel" }
    require(maxHeaderLevel >= minHeaderLevel) { "Toc maxHeaderLevel must be >= minHeaderLevel, got $minHeaderLevel > $maxHeaderLevel" }

    val inRangeHeaderNodeNames = (minHeaderLevel..maxHeaderLevel).map { level -> "H$level" }

    Ul(TocStyle.toModifier(variant).then(modifier).toAttrs()) {
        val tocEntries = remember { mutableStateListOf<TocEntry>() }
        registerRefScope(
            refScope {
                add(ref)
                ref { element ->
                    tocEntries.clear()
                    document.body!!.children.walk { child ->
                        if (child is HTMLHeadingElement
                            && child.id.isNotBlank()
                            && child.nodeName in inRangeHeaderNodeNames
                        ) {
                            val headingText = child.textContent ?: return@walk false
                            val indentCount =
                                inRangeHeaderNodeNames.indexOf(child.nodeName).takeIf { it >= 0 } ?: return@walk false

                            tocEntries.add(TocEntry(headingText, child.id, indentCount))
                        }

                        when {
                            child === element -> false
                            else -> true
                        }
                    }
                }
            }
        )

        tocEntries.forEach { entry ->
            Li(Modifier.padding(left = entry.indent * indent).toAttrs()) {
                Link("#${entry.id}", entry.text)
            }
        }
    }

}
