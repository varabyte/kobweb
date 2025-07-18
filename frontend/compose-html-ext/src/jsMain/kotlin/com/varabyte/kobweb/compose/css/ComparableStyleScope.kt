package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*

// We don't reuse JB's StylePropertyDeclaration so that [value] is a String and thus equality is well-defined.
/** Represents a CSS property and the value it is set to, e.g. `("width", "10px")`. */
data class CssPropertyDeclaration(val name: String, val value: String)

// We need our own implementation of StyleScope, so we can both test equality and pull values out of it later
class ComparableStyleScope : StyleScope {
    private val _properties = mutableListOf<CssPropertyDeclaration>()
    val properties: List<CssPropertyDeclaration> = _properties
    private val _variables = mutableListOf<CssPropertyDeclaration>()
    val variables: List<CssPropertyDeclaration> = _variables

    override fun property(propertyName: String, value: StylePropertyValue) {
        _properties.add(CssPropertyDeclaration(propertyName, value.toString()))
    }

    override fun variable(variableName: String, value: StylePropertyValue) {
        _variables.add(CssPropertyDeclaration(variableName, value.toString()))
    }

    override fun equals(other: Any?): Boolean {
        return (other is ComparableStyleScope
            && properties == other.properties
            && variables == other.variables
            )
    }

    override fun hashCode(): Int {
        var result = properties.hashCode()
        result = 31 * result + variables.hashCode()
        return result
    }
}

fun ComparableStyleScope.isNotEmpty(): Boolean {
    return properties.isNotEmpty() || variables.isNotEmpty()
}
