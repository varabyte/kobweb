package com.varabyte.kobweb.project.io

import com.varabyte.kobweb.project.KobwebFolder
import java.nio.file.Path
import kotlin.io.path.*

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.

/**
 * Base class for a file which may get updated behind the scenes, at which point, [content] will be lazily loaded with
 * the latest value.
 */
open class ReadableFile(val path: Path) {
    private var lastModified = 0L
    private lateinit var _contentBytes: ByteArray

    val content: ByteArray?
        get() {
            return path
                .takeIf { it.exists() }
                ?.let {
                    val lastModified = path.getLastModifiedTime()
                    if (this.lastModified != lastModified.toMillis()) {
                        this.lastModified = lastModified.toMillis()
                        _contentBytes = path.readBytes()
                    }

                    _contentBytes
                }
        }
}


open class KobwebReadableTextFile<T : Any>(
    kobwebFolder: KobwebFolder,
    name: String,
    private val deserialize: (String) -> (T),
) {
    private val readableDelegate = ReadableFile(kobwebFolder.resolve(name))

    val path = readableDelegate.path

    private var lastBytes: ByteArray? = null
    private lateinit var _content: T
    val content: T?
        get() {
            return readableDelegate.content
                ?.let { bytes ->
                    if (lastBytes == null || bytes !== lastBytes) {
                        lastBytes = bytes
                        _content = deserialize(bytes.toString(Charsets.UTF_8))
                    }
                    _content
                }
        }
}

open class KobwebWritableTextFile<T : Any>(
    kobwebFolder: KobwebFolder,
    name: String,
    private val serialize: (T) -> String,
    deserialize: (String) -> (T),
) {
    private val readableDelegate = KobwebReadableTextFile(kobwebFolder, name, deserialize)
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
            } else {
                filePath.deleteIfExists()
            }
        }
}