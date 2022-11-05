package com.varabyte.kobweb.cli.conf

import com.varabyte.kobweb.cli.common.assertKobwebApplication
import com.varabyte.kobweb.project.conf.KobwebConfFile

fun handleConf(query: String) {
    val kobwebApplication = assertKobwebApplication()
    if (query.isBlank()) return // No query? OK I guess we're done

    val confFile = KobwebConfFile(kobwebApplication.kobwebFolder)
    val conf = confFile.content!!

    // Use reflection to convert a query, e.g. "server.port", into an answer
    // We basically loop through all properties in the parsed conf.yaml and, if it matches the current part of the
    // query (e.g. "server"), we invoke the getter and continue this until we get to the terminating query part
    // (e.g. "port"), at which point the get value is the answer.
    var answer: Any? = null
    query.split(".").toMutableList().let { queryParts ->
        var ptr: Any = conf
        while (queryParts.isNotEmpty()) {
            // Use Java reflection instead of Kotlin reflection to avoid a 2MB dependency size penalty
            val nextPart = "get" + queryParts.removeFirst().capitalize() // e.g. server -> getServer
            val matchingMethod = ptr::class.java.methods.singleOrNull { it.name == nextPart } ?: break
            ptr = matchingMethod.invoke(ptr)
            if (queryParts.isEmpty()) {
                answer = ptr
            }
        }
    }
    if (answer != null) {
        println(answer)
    }
    else {
        System.err.println("Invalid query")
    }
}