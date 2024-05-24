package playground.utilities

import org.w3c.dom.Element
import org.w3c.dom.HTMLCollection
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.get

fun HTMLCollection.walk(onEach: (Element) -> Unit) {
    (0 until length)
        .mapNotNull { i: Int -> this[i] }
        .forEach { child ->
            onEach(child)
            child.children.walk(onEach)
        }
}

fun NodeList.walk(onEach: (Node) -> Unit) {
    (0 until length)
        .mapNotNull { i: Int -> this[i] }
        .forEach { node ->
            onEach(node)
            node.childNodes.walk(onEach)
        }
}
