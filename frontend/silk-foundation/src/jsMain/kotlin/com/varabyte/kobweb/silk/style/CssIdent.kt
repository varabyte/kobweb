package com.varabyte.kobweb.silk.style


// The Kotlin/JS version doesn't expose `supports`
private abstract external class CSS {
    companion object {
        fun supports(exp: String): Boolean
    }
}

/**
 * A simple inline class for wrapping String values that represent a [CSS Identifier](https://developer.mozilla.org/en-US/docs/Web/CSS/ident).
 *
 * By wrapping this concept in an outer class, we can add extension methods for the outer class instead of `String` if
 * we want to add additional behavior that should only apply to class names.
 */
value class CssIdent(val asStr: String): CharSequence {
    companion object {
        fun isValid(identifier: String): Boolean {
            return tryCreate(identifier) != null
        }

        fun tryCreate(identifier: String): CssIdent? {
            return try {
                CssIdent(identifier)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    init {
        require(asStr.isNotEmpty()) { "CSS identifier cannot be empty" }
        val errorMsg = "Invalid CSS identifier: \"$asStr\". See rules at https://developer.mozilla.org/en-US/docs/Web/CSS/ident#syntax"
        require(!asStr[0].isDigit()) { errorMsg }
        // The following trick lets us lean on the browser for complex CSS identifier validation - we pretend to create
        // a custom variable, and if the browser doesn't support it, it means the identifier is invalid.
        // Note that variables are allowed to start with digits, which is why we explicitly check for that case above.
        require(CSS.supports("--$asStr:0")) { errorMsg }
    }

    override val length get() = asStr.length
    override fun get(index: Int) = asStr[index]
    override fun subSequence(startIndex: Int, endIndex: Int) = asStr.subSequence(startIndex, endIndex)
    override fun toString(): String = asStr

    /**
     * Convenience method for creating a new [CssIdent] based on this current one.
     *
     * For example, to add a suffix to a CSS identifier:
     *
     * ```
     * val myClass = CssIdent("my-class")
     * val myClassDark = myClass.renamed { "${this}_dark" }
     * ```
     */
    fun renamed(action: String.() -> String)= CssIdent(action(asStr))
}