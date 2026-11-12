package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.codegen.IconSetCodegen
import com.varabyte.kobweb.icons.metadata.IconSetMetadata
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Writes the [IconSetMetadata] file for an artifact with this set's bindings at [version] under the resource root
 * [outputDir], which tells apps that depend on it which stylesheet to load.
 */
internal fun IconSetCodegen.writeMetadata(version: String, outputDir: File) {
    val metadata = IconSetMetadata(id, version, stylesheetUrl(version))
    outputDir.resolve(IconSetMetadata.PATH).apply {
        parentFile.mkdirs()
        writeText(Json.encodeToString(metadata))
    }
}
