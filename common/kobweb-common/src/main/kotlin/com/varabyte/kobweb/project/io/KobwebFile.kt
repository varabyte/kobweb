package com.varabyte.kobweb.project.io

import com.varabyte.kobweb.project.KobwebFolder
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.readBytes
import kotlin.io.path.writeText

// TODO(Bug #12): Use safer file logic here to protect against multiple writers etc.

/**
 * Base class for a file which may get updated behind the scenes, at which point, [content] will be lazily loaded with
 * the latest value.
 */
class LiveFile(val path: Path) {
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
    val kobwebFolder: KobwebFolder,
    name: String,
    private val deserialize: (String) -> (T),
) {
    private val delegateFile = LiveFile(kobwebFolder.path.resolve(name))

    val path = delegateFile.path

    private var lastBytes: ByteArray? = null
    private var _content: T? = null
    val content: T?
        get() {
            return delegateFile.content
                ?.let { bytes ->
                    if (lastBytes == null || bytes !== lastBytes) {
                        try {
                            _content = deserialize(bytes.toString(Charsets.UTF_8))
                            lastBytes = bytes
                        } catch (_: Exception) {
                            _content = null
                            lastBytes = null
                        }
                    }
                    _content
                }
        }
}

open class KobwebWritableTextFile<T : Any>(
    val kobwebFolder: KobwebFolder,
    name: String,
    private val serialize: (T) -> String,
    deserialize: (String) -> (T),
) {
    private val delegateFile = KobwebReadableTextFile(kobwebFolder, name, deserialize)
    val path = delegateFile.path

    var content: T?
        get() = delegateFile.content
        set(value) {
            val filePath = delegateFile.path
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
