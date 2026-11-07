package com.varabyte.kobweb.icons.codegen

import java.io.File
import java.io.InputStream
import java.net.URI

/**
 * Throws if any two of these raw icon names convert to the same method name, which would otherwise generate
 * conflicting declarations.
 *
 * For example, "arrow-1" and "arrow1" both become "Arrow1", since separators before digits disappear.
 */
internal fun Iterable<String>.requireUniqueMethodNames(toMethodName: (String) -> String) {
    val clashes = groupBy(toMethodName).filterValues { it.size > 1 }
    check(clashes.isEmpty()) { "Multiple icons map to the same method name: $clashes" }
}

/** Opens a connection for downloading upstream icon data from this URI, logging what's being fetched. */
internal fun URI.openDownloadStream(): InputStream {
    println("Fetching $this")
    val connection = toURL().openConnection()
    connection.setRequestProperty("User-Agent", "kobweb-icons-codegen")
    return connection.getInputStream()
}

/** Returns the (freshly emptied) directory that files in [packageName] belong in, under this source root. */
internal fun File.preparePackageDir(packageName: String): File {
    val packageDir = resolve(packageName.replace('.', '/'))
    packageDir.deleteRecursively()
    packageDir.mkdirs()
    return packageDir
}
