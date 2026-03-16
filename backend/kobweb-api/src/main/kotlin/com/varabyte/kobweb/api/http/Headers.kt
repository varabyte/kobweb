package com.varabyte.kobweb.api.http

class Headers : MutableMap<String, String> {
    private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()

    // For backwards compatibility reasons
    // can be removed, once deprecated functions below are removed
    private val singleValueHeaders: Map<String, String>
        get() = headers
            .mapValues { it.value.firstOrNull() }
            .filterValues { it != null }
            .mapValues { it.value as String }

    @Deprecated("DO NOT IGNORE. Please change to `names` instead. This method will be removed soon in a backwards incompatible way.", replaceWith = ReplaceWith("names"))
    override val keys: MutableSet<String>
        get() = names.toMutableSet()
    @Deprecated("DO NOT IGNORE. Please change to `values(<name>)` instead. This method will be removed soon in a backwards incompatible way.", replaceWith = ReplaceWith("values('name')"))
    override val values: MutableCollection<String>
        get() = singleValueHeaders.values.toMutableList()
    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override val entries: MutableSet<MutableMap.MutableEntry<String, String>>
        get() = singleValueHeaders.toMutableMap().entries

    val names: Set<String>
        get() = headers.keys

    @Deprecated("DO NOT IGNORE. Please change to `append` instead. This method will be removed soon in a backwards incompatible way.", replaceWith = ReplaceWith("append"))
    override fun put(key: String, value: String): String? {
        val existingValue = singleValueHeaders[key]
        headers[key]?.clear()
        append(key, value)
        return existingValue
    }

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override fun remove(key: String): String? {
        return headers.remove(key)?.firstOrNull()
    }

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override fun putAll(from: Map<out String, String>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override fun clear() {
        headers.clear()
    }

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override val size: Int
        get() = singleValueHeaders.size

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override fun isEmpty(): Boolean {
        return singleValueHeaders.isEmpty()
    }

    @Deprecated("DO NOT IGNORE. Please change to `contains` instead. This method will be removed soon in a backwards incompatible way.", replaceWith = ReplaceWith("contains"))
    override fun containsKey(key: String): Boolean {
        return contains(key)
    }

    @Deprecated("DO NOT IGNORE. This method will be removed soon in a backwards incompatible way.")
    override fun containsValue(value: String): Boolean {
        return singleValueHeaders.containsValue(value)
    }

    override operator fun get(key: String): String? {
        return headers[key]?.firstOrNull()
    }

    @Deprecated("DO NOT IGNORE. Please change to `append` instead. This method will be removed soon in a backwards incompatible way.", replaceWith = ReplaceWith("append"))
    operator fun set(key: String, value: String) {
        put(key, value)
    }

    fun contains(name: String): Boolean {
        return headers.containsKey(name)
    }

    fun values(name: String): List<String> {
        return headers.getOrDefault(name, emptyList())
    }

    fun append(name: String, value: String) {
        headers.getOrPut(name) { mutableListOf() }.add(value)
    }

}