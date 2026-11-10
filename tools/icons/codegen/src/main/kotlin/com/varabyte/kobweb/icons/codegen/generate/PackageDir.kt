package com.varabyte.kobweb.icons.codegen.generate

import java.io.File

/**
 * Returns the directory that bindings in [packageName] belong in, under this source root.
 *
 * It's emptied first, clearing out bindings for icons that no longer exist.
 */
internal fun File.preparePackageDir(packageName: String): File {
    val packageDir = resolve(packageName.replace('.', '/'))
    packageDir.deleteRecursively()
    packageDir.mkdirs()
    return packageDir
}
