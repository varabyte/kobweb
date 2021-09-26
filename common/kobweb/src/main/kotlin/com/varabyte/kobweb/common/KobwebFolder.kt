package com.varabyte.kobweb.common

import java.nio.file.Files
import java.nio.file.Path

private const val KOBWEB_FOLDER = ".kobweb"

/**
 * The Kobweb folder is a special folder which contains various configuration files, runtime files, and other output
 * associated with a Kobweb project.
 *
 * If a normal directory has a Kobweb folder inside of it, then it is considered a Kobweb project.
 *
 * If a Kobweb project contains any subfolders that themselves own a Kobweb folder, then that is considered a different
 * project and opaque to the parent project.
 */
class KobwebFolder private constructor(private val path: Path) {
    companion object {
        /**
         * Return a Kobweb folder if it is a child of the current path.
         */
        fun inPath(path: Path): KobwebFolder? {
            val kobwebFolderPath = path.resolve(KOBWEB_FOLDER).takeIf { Files.exists(it) } ?: return null
            return KobwebFolder(kobwebFolderPath)
        }

        /**
         * Return true if the current path represents a Kobweb project (that is, a project with a Kobweb folder in its
         * root).
         */
        fun isKobwebProject(path: Path): Boolean {
            return inPath(path) != null
        }

        /**
         * Given some arbitrary path, find the Kobweb folder associated with it, climbing up the ancestor tree to do
         * so.
         *
         * So for example, "my-project/.kobweb/a/b/c/d.txt" will return "my-project" for "d.txt"
         *
         * The file may itself be under the Kobweb folder, or it may be a normal file that lives inside a Kobweb
         * project. This method will return the Kobweb folder for all cases.
         */
        fun fromChildPath(path: Path): KobwebFolder? {
            var curr: Path? = path
            while (curr != null) {
                inPath(curr)?.let { foundFolder -> return foundFolder }
                curr = curr.parent
            }
            return null
        }

        /**
         * Helper function for giving the name of a path inside a Kobweb folder (necessary as we don't expose the
         * value of [KOBWEB_FOLDER]).
         */
        fun pathNameTo(child: String): String = "$KOBWEB_FOLDER/$child"
    }

    /**
     * Return a child file that lives (or will live) within the Kobweb folder.
     */
    fun resolve(child: String): Path = path.resolve(child)

    /**
     * Return the parent project folder which owns this Kobweb configuration folder.
     */
    fun getProjectPath(): Path = path.parent
}