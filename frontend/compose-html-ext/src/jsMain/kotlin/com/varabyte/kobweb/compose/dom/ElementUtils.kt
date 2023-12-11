package com.varabyte.kobweb.compose.dom

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

private val ALPHANUMERICS = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/** Generates an element ID guaranteed unique for the current [Document]. */
fun Document.generateUniqueId(len: Int = 6, prefix: String = ""): String {
    require(len > 0) { "ID length must be greater than 0" }

    var label: String
    do {
        label = prefix + buildString { repeat(len) { append(ALPHANUMERICS.random()) } }
    } while (!label[0].isLetter() && getElementById(label) != null)
    return label
}

/**
 * Returns a sequence of all ancestors of this element, starting with the element itself.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
val HTMLElement.ancestors: Sequence<HTMLElement>
    get() {
        return sequence {
            var current: Element? = this@ancestors
            while (current != null) {
                if (current is HTMLElement) yield(current)
                current = current.parentElement
            }
        }
    }

/**
 * Returns a sequence of all descendants of this element, starting with the element itself, in a breadth-first manner.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
val HTMLElement.descendantsBfs: Sequence<HTMLElement>
    get() {
        return sequence {
            val queue = mutableListOf<HTMLElement>()
            queue.add(this@descendantsBfs)
            while (queue.isNotEmpty()) {
                val node = queue.removeAt(0)
                yield(node)
                for (i in 0 until node.children.length) {
                    val child = node.children[i] as? HTMLElement ?: continue
                    queue.add(child)
                }
            }
        }
    }

/**
 * Returns a sequence of all descendants of this element, starting with the element itself, in a depth-first manner.
 *
 * If you want to exclude the element itself from the sequence, simply call `drop(1)` on the returned sequence.
 */
val HTMLElement.descendantsDfs: Sequence<HTMLElement>
    get() {
        return sequence {
            val stack = mutableListOf<HTMLElement>()
            stack.add(this@descendantsDfs)
            while (stack.isNotEmpty()) {
                val node = stack.removeAt(0)
                yield(node)
                for (i in 0 until node.children.length) {
                    val child = node.children[i] as? HTMLElement ?: continue
                    stack.add(i, child)
                }
            }
        }
    }
