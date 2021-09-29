package com.varabyte.kobweb.common.io

import com.varabyte.kobweb.common.KobwebFolder
import kotlin.io.path.*

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.

open class KobwebReadableFile<T: Any>(
    kobwebFolder: KobwebFolder,
    name: String,
    private val deserialize: (String) -> (T),
) {
    val path = kobwebFolder.resolve(name)

    private var lastModified = 0L
    private lateinit var _content: T

    val content: T?
        get() {
            return path
                .takeIf { it.exists() }
                ?.let {
                    val lastModified = path.getLastModifiedTime()
                    if (this.lastModified != lastModified.toMillis()) {
                        this.lastModified = lastModified.toMillis()
                        _content = deserialize(path.readText())
                    }

                    _content
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
    val path = readableDelegate.path

    var content: T?
        get() = readableDelegate.content
        set(value) {
            val filePath = readableDelegate.path
            if (!filePath.parent.exists()) {
                filePath.parent.createDirectories()
            }
            if (value != null) {
                filePath.writeText(serialize(value))
            }
            else {
                filePath.deleteIfExists()
            }
        }
}