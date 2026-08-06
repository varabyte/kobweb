package com.varabyte.kobweb.browser.dom.css

// The Kotlin/JS version doesn't expose `supports`
private abstract external class CSS {
    companion object {
        fun supports(exp: String): Boolean
        fun supports(propertyName: String, value: String): Boolean
    }
}

/**
 * A simple inline class for wrapping String values that represent a [CSS Identifier](https://developer.mozilla.org/en-US/docs/Web/CSS/ident).
 *
 * By wrapping this concept in an outer class, we can add extension methods for it instead of `String`. We also perform
 * some checks in the constructor to ensure the passed in identifier is valid.
 */
value class CssIdent(val asStr: String): CharSequence by asStr {
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
        val errorMsg = "Invalid CSS identifier: \"$asStr\". See https://developer.mozilla.org/en-US/docs/Web/CSS/ident#syntax"
        require(asStr.isNotEmpty()) { errorMsg }
        require(!asStr[0].isDigit()) { errorMsg }
        // The following trick lets us lean on the browser for complex CSS identifier validation - we pretend to create
        // a custom variable, and if the browser doesn't support it, it means the identifier is invalid.
        // Note that variables are allowed to start with digits, which is why we explicitly check for that case above.
        require(CSS.supports("--$asStr:0")) { errorMsg }
    }

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

/**
 * A helping wrapper for CSS property names that let us define functionality on top of them.
 */
value class CssPropertyName(private val ident: CssIdent) {
    constructor(name: String) : this(CssIdent(name))
    val asStr get() = ident.asStr

    /**
     * Returns the first property value supported by this browser for this property name.
     *
     * Property values should be listed in priority order -- the earlier names in the list should ideally be matched
     * before giving up and moving to the later names.
     *
     * If no property values are supported, the first item in the list will be returned, as even if the browser can't
     * handle it, at least the user will be able to use dev tools to see it.
     *
     * The purpose of this method is to help with CSS properties that might require fallback values on different browser
     * vendors.
     *
     * For example, `"width: stretch"` is not, at the time of writing this comment, supported by stable Safari yet;
     * there, they still use `"width: -webkit-fill-available"`. So you would call:
     * ```
     * CssProperty("width").firstSupportedValue("stretch", "-webkit-fill-available")
     * ```
     * which would return `"stretch"` on, say, Chrome, and `"-webkit-fill-available"` on Safari.
     *
     * It is an error to call this method with one or fewer values. In that case, you shouldn't be using it!
     */
    fun firstSupportedValue(vararg values: String): String {
        require(values.size >= 2)
        return values.firstOrNull { value -> CSS.supports(ident.asStr, value) } ?: values.first()
    }

    override fun toString() = asStr
}