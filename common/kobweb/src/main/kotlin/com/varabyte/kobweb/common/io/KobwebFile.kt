package com.varabyte.kobweb.common.io

import com.varabyte.kobweb.common.KobwebFolder
import kotlin.io.path.*

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.

open class KobwebReadableFile<T: Any>(
    kobwebFolder: KobwebFolder,
    name: String,
    private val deserialize: (String) -> (T),
) {
    internal val filePath = kobwebFolder.resolve(name)

    private var lastModified = 0L
    private lateinit var _wrapped: T

    val wrapped: T?
        get() {
            return filePath
                .takeIf { it.exists() }
                ?.let {
                    val lastModified = filePath.getLastModifiedTime()
                    if (this.lastModified != lastModified.toMillis()) {
                        this.lastModified = lastModified.toMillis()
                        _wrapped = deserialize(filePath.readText())
                    }

                    _wrapped
                }
        }
}

open class KobwebWritableFile<T: Any>(
    kobwebFolder: KobwebFolder,
    name: String,
    private val serialize: (T) -> String,
    deserialize: (String) -> (T),
) {
    private val readableDelegate = KobwebReadableFile(kobwebFolder, name, deserialize)

    var wrapped: T?
        get() = readableDelegate.wrapped
        set(value) {
            val filePath = readableDelegate.filePath
            if (!filePath.parent.exists()) {
                filePath.parent.createDirectories()
            }
            if (value != null) {
                filePath.writeText(serialize(value))
            }
            else {
                filePath.deleteExisting()
            }
        }
}