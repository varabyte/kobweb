package com.varabyte.kobweb.api.http

/**
 * A collection of HTTP header values.
 *
 * In a majority of cases, HTTP headers are normally a collection of simple key/value pairs,
 * but per official spec, a header field can technically contain multiple values. As a result,
 * users are able to [append] new values, besides simply [set] them.
 *
 * Also, according to the RFC, header field names are case-insensitive. In other words, accessing
 * "Content-Type" is functionally identical to "content-type".
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#section-5">HTTP Semantics Section 5</a>
 */
interface Headers : Iterable<Pair<String, List<String>>> {

    val names: Set<String>

    /**
     * Gets first value from the list of values associated with the given name, or null if the name is not present
     */
    operator fun get(key: String): String?

    /**
     * Gets all entries associated with the given name
     */
    fun values(name: String): List<String>

    fun contains(name: String): Boolean
}

class MutableHeaders() : Headers {
    private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()

    constructor(headers: Headers) : this() {
        headers.names.forEach { name ->
            headers.values(name).forEach { value -> append(name, value) }
        }
    }

    constructor(headers: Map<String, List<String>>) : this() {
        headers.entries.forEach { entry ->
            entry.value.forEach { value -> append(entry.key, value) }
        }
    }

    override val names: Set<String>
        get() = headers.keys

    override operator fun get(key: String): String? {
        return headers[key.lowercase()]?.firstOrNull()
    }

    override fun values(name: String): List<String> {
        return headers.getOrDefault(name.lowercase(), emptyList())
    }

    override fun contains(name: String): Boolean {
        return headers.containsKey(name.lowercase())
    }

    override fun iterator(): Iterator<Pair<String, List<String>>> {
        return headers.entries.map { it.key to it.value.toList() }.iterator()
    }

    /**
     * Appends a header value associated with the given name
     */
    fun append(name: String, value: String) {
        headers.getOrPut(name.lowercase()) { mutableListOf() }.add(value)
    }

    /**
     * Sets a single value associated with the given name
     *
     * Use [append] if the value should be added to the list of values instead
     */
    operator fun set(name: String, value: String) {
        headers[name.lowercase()] = mutableListOf(value)
    }

}