package com.varabyte.kobweb.icons.codegen.fetch

import java.io.InputStream
import java.net.URI

/** Opens a connection for downloading upstream icon data from this URI, logging what's being fetched. */
internal fun URI.openDownloadStream(): InputStream {
    println("Fetching $this")
    val connection = toURL().openConnection()
    connection.setRequestProperty("User-Agent", "kobweb-icons-codegen")
    return connection.getInputStream()
}
