package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.codegen.IconCodegenOptions
import java.io.File

/**
 * Returns the directory that bindings in [IconCodegenOptions.packageName] belong in, under the source root [outputDir].
 *
 * It's emptied first, clearing out bindings for icons that no longer exist.
 */
internal fun IconCodegenOptions.preparePackageDir(outputDir: File): File {
    val packageDir = outputDir.resolve(packageName.replace('.', '/'))
    packageDir.deleteRecursively()
    packageDir.mkdirs()
    return packageDir
}
