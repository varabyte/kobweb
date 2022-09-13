package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// We need our own implementation of StyleScope, so we can both test equality and pull values out of it later
class ComparableStyleScope : StyleScope {
    val properties = mutableMapOf<String, String>()
    val variables = mutableMapOf<String, String>()

    override fun property(propertyName: String, value: StylePropertyValue) {
        properties[propertyName] = value.toString()
    }

    override fun variable(variableName: String, value: StylePropertyValue) {
        variables[variableName] = value.toString()
    }

    override fun equals(other: Any?): Boolean {
        return (other is ComparableStyleScope
            && properties == other.properties
            && variables == other.variables
            )
    }

    override fun hashCode(): Int {
        return (properties.hashCode() + variables.hashCode())
    }
}

fun ComparableStyleScope.isNotEmpty(): Boolean {
    return properties.isNotEmpty() || variables.isNotEmpty()
}
