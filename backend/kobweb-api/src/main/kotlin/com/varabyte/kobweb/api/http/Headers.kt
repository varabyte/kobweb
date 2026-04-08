package com.varabyte.kobweb.api.http

class Headers {
    private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()

    val names: Set<String>
        get() = headers.keys

    operator fun get(key: String): String? {
        return headers[key]?.firstOrNull()
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