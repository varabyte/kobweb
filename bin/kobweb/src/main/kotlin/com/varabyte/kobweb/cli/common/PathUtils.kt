package com.varabyte.kobweb.cli.common

import java.nio.file.Path
import java.nio.file.Paths

object PathUtils {
    /**
     * Given a path, e.g. "myproject", return it OR the path with a number appended on it if there are already existing
     * folders at that path with that name, e.g. "myproject4"
     */
    fun generateEmptyPathName(name: String): String {
        require(Validations.folderName(name.substringAfterLast('/')) == null)
        var finalName = name
        var i = 2
        while (Validations.emptyPath(finalName) != null) {
            finalName = "$name$i"
            i++
        }
        return finalName
    }
}