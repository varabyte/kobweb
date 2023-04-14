package com.varabyte.kobweb.compose.dom

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.NodeFilter

/** An interface for finding some target element, relative to some given initial element. */
interface ElementTarget {
    operator fun invoke(startingFrom: HTMLElement): HTMLElement?

    companion object {
        /**
         * A target which returns the current element's parent.
         */
        val Parent get() = object : ElementTarget {
            override fun invoke(startingFrom: HTMLElement) = startingFrom.parentElement as? HTMLElement
        }

        /**
         * A target which returns the element's previous sibling.
         */
        val PreviousSibling get() = object : ElementTarget {
            override fun invoke(startingFrom: HTMLElement) = startingFrom.previousElementSibling as? HTMLElement
        }

        /**
         * A direct element target for when you happen to already have access to the target [HTMLElement].
         *
         * In this case, the starting element that triggers this search is ignored.
         */
        fun of(element: HTMLElement) = object : ElementTarget {
            override fun invoke(startingFrom: HTMLElement) = element
        }

        /**
         * Search an element's ancestors, returning the first match found, if any.
         *
         * For example, perhaps you're nested several levels below some outer container that's tagged by a class:
         *
         * ```
         * ElementTarget.findAncestor { it.classList.contains("outer-container") }
         * ```
         */
        fun findAncestor(matching: (HTMLElement) -> Boolean) = object : ElementTarget {
            override fun invoke(startingFrom: HTMLElement): HTMLElement? {
                var currElement: HTMLElement? = startingFrom
                do {
                    currElement = currElement?.parentElement as? HTMLElement
                } while (currElement != null && !matching(currElement))

                return currElement
            }
        }

        /**
         * Search all descendants of some root element for the first element matching the passed in condition.
         *
         * In this case, the starting element that triggers this search is ignored.
         *
         * This search runs in a breadth-first search manner. If not specified, the search starts from the document's `body`
         * element.
         *
         * For example, perhaps you want to find a close button that's part of a popup dialog that you're inside. The close
         * button also lives inside the dialog, but you are not a direct ancestor.
         *
         * ```
         * ElementTarget.findDescendant(root = dialogElement) { it.classList.contains("close-button") }
         * ```
         *
         * @param root The root to start searching descendants for. This should never be set to null, but its API accepts
         *   a nullable parameter so that it can default to `document.body`, which itself is nullable (even though a body
         *   will almost always exist). If set to null, the descendant search will always fail.
         */
        fun findDescendant(root: HTMLElement? = document.body, matching: (HTMLElement) -> Boolean) =
            object : ElementTarget {
                override fun invoke(startingFrom: HTMLElement): HTMLElement? {
                    if (root == null) return null
                    return document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT) { element ->
                        if (element is HTMLElement && matching(element)) NodeFilter.FILTER_ACCEPT else NodeFilter.FILTER_SKIP
                    }.nextNode() as? HTMLElement
                }
            }

        /**
         * Search globally for an element matching a target ID.
         *
         * In this case, the starting element that triggers this search is ignored.
         *
         * For example:
         *
         * ```
         * ElementTarget.withId("user-info")
         * ```
         *
         * There shouldn't be multiple elements in your DOM tree with the same ID, but if there are, then this will have the
         * same behavior as `document.getElementById(...)`
         */
        fun withId(elementId: String) = object : ElementTarget {
            override fun invoke(startingFrom: HTMLElement): HTMLElement? =
                document.getElementById(elementId) as? HTMLElement
        }
    }
}
