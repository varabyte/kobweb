package com.varabyte.kobweb.icons.codegen.generate

import java.io.File

/** Returns the (freshly emptied) directory that files in [packageName] belong in, under this source root. */
internal fun File.preparePackageDir(packageName: String): File {
    val packageDir = resolve(packageName.replace('.', '/'))
    packageDir.deleteRecursively()
    packageDir.mkdirs()
    return packageDir
}
