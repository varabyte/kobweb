package com.varabyte.kobweb.project

import com.varabyte.kobweb.common.error.KobwebException
import java.nio.file.Path

/**
 * Wrap a [path] that points to the root of the module which defines your Kobweb application.
 *
 * A kobweb application is the path that has a `.kobweb` folder in it.
 *
 * @property path The path that represents the root of the module (defaulting to the current working directory).
 *   A [KobwebException] is thrown if the path does not actually point to a Kobweb project.
 */
class KobwebApplication(val path: Path = Path.of("")) {
    val kobwebFolder = KobwebFolder.inPath(path)
        ?: throw KobwebException("Not a valid path to a Kobweb project (no .kobweb folder found): ${path.toAbsolutePath()}")
}