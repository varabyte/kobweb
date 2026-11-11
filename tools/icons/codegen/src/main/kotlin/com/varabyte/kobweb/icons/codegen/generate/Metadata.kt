package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.codegen.IconSetCodegen
import com.varabyte.kobweb.icons.metadata.IconsMetadata
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Writes the [IconsMetadata] file for an artifact with this set's bindings at [version] under the resource root
 * [outputDir], which tells apps that depend on it which stylesheet to load.
 */
internal fun IconSetCodegen.writeMetadata(version: String, outputDir: File) {
    val metadata = IconsMetadata(mapOf(id to IconsMetadata.IconSet(version, stylesheetUrl(version))))
    outputDir.resolve(IconsMetadata.PATH).apply {
        parentFile.mkdirs()
        writeText(Json.encodeToString(metadata))
    }
}
