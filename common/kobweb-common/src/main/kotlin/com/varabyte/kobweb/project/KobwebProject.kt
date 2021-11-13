package com.varabyte.kobweb.project

import com.varabyte.kobweb.common.error.KobwebException
import java.nio.file.Path

/**
 * Construct a project that represents the Kobweb project rooted at the specified [path] and provides methods for
 * parsing it and gaining insights into the project (see: [parseData])
 *
 * @param path The path that represents the root of the Kobweb project (defaulting to the current working directory).
 *   A [KobwebException] is thrown if the path does not actually point to a Kobweb project.
 */
class KobwebProject(
    val path: Path = Path.of("")
) {
    val kobwebFolder = KobwebFolder.inPath(path)
        ?: throw KobwebException("Not a valid path to a Kobweb project (no .kobweb folder found): ${path.toAbsolutePath()}")
}