package com.varabyte.kobweb.cli.common

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

private const val KOBWEB_FOLDER = ".kobweb"

object KobwebUtils {
    /** Returns true if the current path represents the root directory of a Kobweb application */
    fun isKobwebProject(path: Path) = getKobwebFolderIn(path) != null

    /** Returns the special Kobweb folder in the current path, if it exists. */
    fun getKobwebFolderIn(path: Path) = Paths.get(path.toString(), KOBWEB_FOLDER).takeIf { it.exists() }

    /** Returns the child file under the Kobweb folder in the current path, if it exists. */
    fun getKobwebFileIn(path: Path, child: String) = getKobwebFolderIn(path)?.resolve(child)?.takeIf { it.exists() }

    /**
     * Given a file nested somewhere under the Kobweb folder, return its root project directory.
     *
     * So for example, "my-project/.kobweb/a/b/c/d.txt" will return "my-project" for "d.txt"
     *
     * This will return null if the file is not actually nested under the Kobweb folder.
     */
    fun getKobwebProjectFor(file: Path): Path? {
        var curr: Path? = file
        while (curr != null && !isKobwebProject(curr)) {
            curr = curr.parent
        }
        return curr
    }
}