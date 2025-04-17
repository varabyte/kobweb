package com.varabyte.kobwebx.frontmatter

@DslMarker
private annotation class FrontMatterElementBuilder

sealed class FrontMatterElement {
    companion object {
        val EmptyMap get() = ValueMap(emptyMap())
        val EmptyList get() = ValueList(emptyList())

        @Suppress("FunctionName") // Factory method
        fun Builder(populate: ValueMap.Builder.() -> Unit): ValueMap {
            val builder = ValueMap.Builder()
            builder.populate()
            return builder.build()
        }
    }

    /**
     * Use a convenient query syntax to extract a child element from this frontmatter element.
     *
     * Imagine you have the following frontmatter declaration:
     *
     * ```yaml
     * title: Title
     * data:
     *   assets:
     *     images:
     *       - cat.png
     *       - dog.png
     *     font: Roboto
     * ```
     *
     * Once parsed into a [FrontMatterElement], you can query it like so:
     *
     * ```
     * val fm: FrontMatterElement = ...
     * fm.query("title") // returns FrontMatterElement.Scalar
     * fm.query("data.assets.images") // returns FrontMatterElement.ValueList
     * fm.query("data.assets.images.1") // returns FrontMatterElement.Scalar
     * fm.query("data.assets.font") // returns FrontMatterElement.Scalar
     * fm.query("data.assets") // returns FrontMatterElement.ValueMap
     * fm.query("key-not-found") // returns null
     * ```
     *
     * You may prefer using the [get] operator in most cases, however, as that provides convenient access directly to
     * nested string values, which is enough for a vast majority of the cases.
     */
    abstract fun query(path: String): FrontMatterElement?

    protected fun handleQuery(path: String, processSegment: (String) -> FrontMatterElement? = { null }): FrontMatterElement? {
        if (path.isEmpty()) return this

        val (segment, rest) = path.consumePath()
        return processSegment(segment)?.query(rest)
    }

    fun getValue(path: String): List<String> {
        return get(path) ?: throw NoSuchElementException("Yaml path [${path}] not found or associated with a value list.")
    }

    /**
     * A helper method that provides convenient map-like query access for fetching (possibly nested) frontmatter values.
     *
     * Imagine you have the following frontmatter declaration:
     *
     * ```yaml
     * title: Title
     * data:
     *   assets:
     *     images:
     *       - cat.png
     *       - dog.png
     *     font: Roboto
     * ```
     *
     * Once parsed into a [FrontMatterElement], you can query it like so:
     *
     * ```
     * val fm: FrontMatterElement = ...
     * fm["title"] // returns listOf("Title")
     * fm["data.assets.images"] // returns listOf("cat.png", "dog.png")
     * fm["data.assets.images.1"] // returns listOf("dog.png")
     * fm["data.assets.font"] // returns listOf("Roboto")
     * fm["key-not-found"] // returns null
     * ```
     *
     * If you are sure that you are fetching a scalar value, you can use the `single` method:
     *
     * ```
     * fm.getValue("title").single()
     * ```
     *
     * Note that querying a path to a map (e.g. `fm["data.assets"]` for the case above) will return null and NOT an
     * empty list, as a map is not a scalar nor a list of scalars. If you absolutely need to distinguish between these
     * two cases, use the [query] method instead.
     */
    operator fun get(path: String): List<String>? {
        return when (val result = query(path)) {
            is Scalar -> listOf(result.scalar)
            is ValueList -> result.scalarList()
            else -> null
        }
    }

    protected fun String.consumePath(): Pair<String, String> {
        val nextSegment = substringBefore('.')
        val rest = substringAfter('.', "")
        return nextSegment to rest
    }

    fun scalarOrNull() = (this as? Scalar)?.scalar

    fun scalarMap() = (this as? ValueMap)
        ?.map?.mapNotNull { (key, value) ->
            value.scalarOrNull()?.let { scalarValue ->
                key to scalarValue
            }
        }?.toMap()
        ?: emptyMap()

    fun scalarList() = (this as? ValueList)
        ?.list?.mapNotNull { it.scalarOrNull() }
        ?: emptyList()

    class Scalar(val scalar: String) : FrontMatterElement() {
        override fun query(path: String): FrontMatterElement? = handleQuery(path)
    }

    class ValueMap(val map: Map<String, FrontMatterElement>) : FrontMatterElement() {
        @FrontMatterElementBuilder
        class Builder {
            private var map = mutableMapOf<String, FrontMatterElement>()
            fun addScalar(key: String, value: String): Builder {
                map[key] = Scalar(value)
                return this
            }
            fun addMap(key: String, populate: Builder.() -> Unit): Builder {
                val builder = Builder()
                builder.populate()
                map[key] = builder.build()
                return this
            }
            fun addList(key: String, populate: ValueList.Builder.() -> Unit): Builder {
                val builder = ValueList.Builder()
                builder.populate()
                map[key] = builder.build()
                return this
            }

            fun build() = ValueMap(map)
        }

        init {
            // We have to reject "." in key names because that has syntactic meaning in the way we query elements
            map.keys.filter { it.contains('.') }.takeIf { it.isNotEmpty() }?.let { invalidKeys ->
                throw IllegalArgumentException("Kobweb FrontMatter keys do not allow periods in them. Got: [${invalidKeys.joinToString()}]")
            }
        }

        override fun query(path: String): FrontMatterElement? {
            return handleQuery(path) { segment -> map[segment] }
        }
    }

    class ValueList(val list: List<FrontMatterElement>) : FrontMatterElement() {
        @FrontMatterElementBuilder
        class Builder {
            private var list = mutableListOf<FrontMatterElement>()
            fun addScalar(value: String): Builder {
                list.add(Scalar(value))
                return this
            }
            fun addMap(populate: ValueMap.Builder.() -> Unit): Builder {
                val builder = ValueMap.Builder()
                builder.populate()
                list.add(builder.build())
                return this
            }

            fun build() = ValueList(list)
        }

        override fun query(path: String): FrontMatterElement? {
            return handleQuery(path) { segment ->
                segment.toIntOrNull()?.let { index -> list.getOrNull(index) }
            }
        }
    }
}
