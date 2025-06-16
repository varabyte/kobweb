package com.varabyte.kobweb.worker

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Float64Array
import org.khronos.webgl.Int16Array
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.DOMMatrix
import org.w3c.dom.DOMMatrixReadOnly
import org.w3c.dom.DOMPoint
import org.w3c.dom.DOMPointReadOnly
import org.w3c.dom.DOMQuad
import org.w3c.dom.DOMRect
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.ImageBitmap
import org.w3c.dom.ImageData
import org.w3c.dom.MessagePort
import org.w3c.files.Blob
import org.w3c.files.File
import org.w3c.files.FileList
import kotlin.js.Json
import kotlin.js.json

private const val CLONEABLE_NAMES_KEY = "_cloneableNames"
private const val TRANSFERABLE_NAMES_KEY = "_transferableNames"
private const val METADATA_NAMES_KEY = "_metadataNames"

private fun suffixedKey(key: String, suffix: String?) = key + suffix?.let { "_$it" }.orEmpty()

@Deprecated("Kobweb Worker `Transferables` has been renamed more generically to `Attachments` as it now supports adding structurally cloneable items as well.", replaceWith = ReplaceWith("Attachments"))
typealias Transferables = Attachments

/**
 * Attachments are special objects which are allowed to be moved between app / worker threads.
 *
 * In Web Workers, there are two types of objects allowed across worker boundaries --
 * [structured cloneables](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Structured_clone_algorithm)
 * and [transferables](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects).
 *
 * Cloneables are what they sound like -- objects that are copied when being sent to / from the target worker.
 *
 * Transferables are essentially a performance optimization. Instead of a value being copied, its ownership is passed
 * over to the target worker, and attempts to access the value in the sending script will see a null value.
 *
 * Kobweb workers push users to use serialization for their messages, which may work fine in most cases, but it can
 * cause a noticeable, even multi-second stutter for very sizable objects (like large bitmaps). By attaching these
 * objects to the message being posted to the worker, we can avoid converting some values to large strings and then back
 * again.
 *
 * Not every object can be sent between scripts. To constrain users from sending unsupported types, we require
 * users create an `Attachment` instance using a [Attachments.Builder].
 *
 * Unlike default JS APIs, which just send an array of transferables, we require the user to specify the name of each
 * object, as an added layer of safety.
 *
 * ```
 * // Sender
 * worker.postMessage(message, Attachments { add("bitmap", bitmap) })
 *
 * // Receiver
 * internal class TransferableWorkerFactory : WorkerFactory<String, String> {
 *     override fun createStrategy(postOutput: OutputDispatcher<String> -> Unit) = WorkerStrategy<String> { input ->
 *         val bitmap = attachments.getImageBitmap("bitmap")
 *         ...
 *     }
 *
 *     override fun createIOSerializer() = createPassThroughSerializer()
 * }
 * ```
 *
 * Not every object in the web is transferable / cloneable, so the `add` methods and `get` methods are appropriately
 * typed to constrain what you can register and fetch.
 *
 * Note that arrays are actually not transferable, but we add support for them here anyway, doing the conversion to
 * their underlying `ArrayBuffer` ourselves to avoid the users having to write a bunch of boilerplate to do this
 * themselves.
 *
 * @property cloneables A map of names to structured cloneable objects.
 * @property transferables A map of names to transferable objects.
 * @property metadata A map of additional values describing metadata around the transferable objects but are not
 *   themselves transferable. These should be limited to primitive values, such as ints or strings.
 */
class Attachments private constructor(
    private val cloneables: Map<String, Any>,
    private val transferables: Map<String, Any>,
    private val metadata: Map<String, Any>
) {
    companion object {
        val Empty = Attachments(emptyMap(), emptyMap(), emptyMap())

        /**
         * Convenience method to remove some boilerplate around using a [Builder].
         */
        operator fun invoke(init: Builder.() -> Unit): Attachments {
            return Builder().apply(init).build()
        }

        /**
         * Deserialize a Transferables object from a JSON object that was created using [Attachments.toJson].
         */
        fun fromJson(json: Json): Attachments {
            val cloneableNames = json[CLONEABLE_NAMES_KEY]?.unsafeCast<Array<String>>() ?: emptyArray()
            val transferableNames = json[TRANSFERABLE_NAMES_KEY]?.unsafeCast<Array<String>>() ?: emptyArray()
            val metadataNames = json[METADATA_NAMES_KEY]?.unsafeCast<Array<String>>() ?: emptyArray()

            if (cloneableNames.isEmpty() && transferableNames.isEmpty() && metadataNames.isEmpty()) return Empty

            val cloneables = mutableMapOf<String, Any>()
            for (name in cloneableNames) {
                cloneables[name] = json[name]!!
            }

            val transferables = mutableMapOf<String, Any>()
            for (name in transferableNames) {
                transferables[name] = json[name]!!
            }

            val metadata = mutableMapOf<String, Any>()
            for (name in metadataNames) {
                metadata[name] = json[name]!!
            }
            return Attachments(cloneables, transferables, metadata)
        }
    }

    class Builder {
        private val cloneables = mutableMapOf<String, Any>()
        private val transferables = mutableMapOf<String, Any>()
        private val metadata = mutableMapOf<String, Any>()

        private fun MutableMap<String, Any>.add(key: String, value: Any): Builder {
            return add(key, suffix = null, value)
        }

        private fun MutableMap<String, Any>.add(key: String, suffix: String?, value: Any): Builder {
            if (this.put(suffixedKey(key, suffix), value) != null) {
                error("Attachment with key \"$key\" was added twice.")
            }
            return this@Builder
        }

        // region cloneables

        // The following items come from https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Structured_clone_algorithm#webapi_types
        // and are also supported by Kotlin/JS bindings (or are otherwise commented out). Users can file an issue with
        // the Kobweb team if they want us to support any of the missing types.

        //        fun add(key: String, value: AudioData) = cloneables.add(key, "AudioData", value)
        fun add(key: String, value: Blob) = cloneables.add(key, "Blob", value)
//        fun add(key: String, value: CropTarget) = cloneables.add(key, "CropTarget", value)
//        fun add(key: String, value: CryptoKey) = cloneables.add(key, "CryptoKey", value)
        fun add(key: String, value: DOMMatrix) = cloneables.add(key, "DOMMatrix", value)
        fun add(key: String, value: DOMMatrixReadOnly) = cloneables.add(key, "DOMMatrixReadOnly", value)
        fun add(key: String, value: DOMPoint) = cloneables.add(key, "DOMPoint", value)
        fun add(key: String, value: DOMPointReadOnly) = cloneables.add(key, "DOMPointReadOnly", value)
        fun add(key: String, value: DOMQuad) = cloneables.add(key, "DOMQuad", value)
        fun add(key: String, value: DOMRect) = cloneables.add(key, "DOMRect", value)
        fun add(key: String, value: DOMRectReadOnly) = cloneables.add(key, "DOMRectReadOnly", value)
//        fun add(key: String, value: EncodedAudioChunk) = cloneables.add(key, "EncodedAudioChunk", value)
//        fun add(key: String, value: EncodedVideoChunk) = cloneables.add(key, "EncodedVideoChunk", value)
//        fun add(key: String, value: FencedFrameConfig) = cloneables.add(key, "FencedFrameConfig", value)
        fun add(key: String, value: File) = cloneables.add(key, "File", value)
        fun add(key: String, value: FileList) = cloneables.add(key, "FileList", value)
//        fun add(key: String, value: FileSystemDirectoryHandle) = cloneables.add(key, "FileSystemDirectoryHandle", value)
//        fun add(key: String, value: FileSystemFileHandle) = cloneables.add(key, "FileSystemFileHandle", value)
//        fun add(key: String, value: FileSystemHandle) = cloneables.add(key, "FileSystemHandle", value)
//        fun add(key: String, value: GPUCompilationInfo) = cloneables.add(key, "GPUCompilationInfo", value)
//        fun add(key: String, value: GPUCompilationMessage) = cloneables.add(key, "GPUCompilationMessage", value)
//        fun add(key: String, value: GPUPipelineError) = cloneables.add(key, "GPUPipelineError", value)
//        fun add(key: String, value: RTCCertificate) = cloneables.add(key, "RTCCertificate", value)
//        fun add(key: String, value: RTCEncodedAudioFrame) = cloneables.add(key, "RTCEncodedAudioFrame", value)
//        fun add(key: String, value: RTCEncodedVideoFrame) = cloneables.add(key, "RTCEncodedVideoFrame", value)
//        fun add(key: String, value: VideoFrame) = cloneables.add(key, "VideoFrame", value)
//        fun add(key: String, value: WebTransportError) = cloneables.add(key, "WebTransportError", value)


        // endregion

        // region transferables

        fun add(key: String, value: ArrayBuffer) = transferables.add(key, value)
        fun add(key: String, value: MessagePort) = transferables.add(key, value)
        fun add(key: String, value: ImageBitmap) = transferables.add(key, value)
        // TODO: There are more official types that are supported, see
        //  https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects#supported_objects
        //  However, they aren't currently available in the Kotlin/JS stdlib, so if people ask for their support, we
        //  probably need to define the object ourselves.

        // endregion

        // region Convenience "transferables"

        // These methods allow adding values that themselves are not directly transferable but we can wrap transferable
        // concepts underneath them ourselves for user convenience.

        fun add(key: String, value: Int8Array) = transferables.add(key, "Int8Array", value.buffer)
        fun add(key: String, value: Uint8Array) = transferables.add(key, "Uint8Array", value.buffer)
        fun add(key: String, value: Uint8ClampedArray) = transferables.add(key, "Uint8ClampedArray", value.buffer)
        fun add(key: String, value: Int16Array) = transferables.add(key, "Int16Array", value.buffer)
        fun add(key: String, value: Uint16Array) = transferables.add(key, "Uint16Array", value.buffer)
        fun add(key: String, value: Int32Array) = transferables.add(key, "Int32Array", value.buffer)
        fun add(key: String, value: Uint32Array) = transferables.add(key, "Uint32Array", value.buffer)
        fun add(key: String, value: Float32Array) = transferables.add(key, "Float32Array", value.buffer)
        fun add(key: String, value: Float64Array) = transferables.add(key, "Float64Array", value.buffer)
        fun add(key: String, value: ImageData) {
            transferables.add(key, "ImageData_buffer", value.data.buffer)
            metadata[suffixedKey(key, "ImageData_width")] = value.width
            metadata[suffixedKey(key, "ImageData_height")] = value.height
        }
        // endregion

        fun build() = Attachments(cloneables, transferables, metadata)
    }

    /**
     * Fetch the transferable associated with the key.
     *
     * This will return null if there is no attachment matching the key name or if the requested type is not the
     * correct type.
     */
    private fun <T : Any> Map<String, Any>.get(key: String): T? {
        val transferable = this[key] ?: return null
        @Suppress("UNCHECKED_CAST") // It's a nullable cast so not sure why the compiler is complaining
        return transferable as? T
    }

    // region cloneables

    fun getBlob(key: String) = cloneables.get<Blob>(suffixedKey(key, "Blob"))
    fun getDOMMatrix(key: String) = cloneables.get<DOMMatrix>(suffixedKey(key, "DOMMatrix"))
    fun getDOMMatrixReadOnly(key: String) = cloneables.get<DOMMatrixReadOnly>(suffixedKey(key, "DOMMatrixReadOnly"))
    fun getDOMPoint(key: String) = cloneables.get<DOMPoint>(suffixedKey(key, "DOMPoint"))
    fun getDOMPointReadOnly(key: String) = cloneables.get<DOMPointReadOnly>(suffixedKey(key, "DOMPointReadOnly"))
    fun getDOMQuad(key: String) = cloneables.get<DOMQuad>(suffixedKey(key, "DOMQuad"))
    fun getDOMRect(key: String) = cloneables.get<DOMRect>(suffixedKey(key, "DOMRect"))
    fun getDOMRectReadOnly(key: String) = cloneables.get<DOMRectReadOnly>(suffixedKey(key, "DOMRectReadOnly"))
    fun getFile(key: String) = cloneables.get<File>(suffixedKey(key, "File"))
    fun getFileList(key: String) = cloneables.get<FileList>(suffixedKey(key, "FileList"))

    // endregion

    // region transferables

    fun getArrayBuffer(key: String) = transferables.get<ArrayBuffer>(key)
    fun getMessagePort(key: String) = transferables.get<MessagePort>(key)
    fun getImageBitmap(key: String) = transferables.get<ImageBitmap>(key)

    // endregion

    // region convenience "transferables"

    fun getInt8Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Int8Array")))?.let { Int8Array(it) }
    fun getUint8Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Uint8Array")))?.let { Uint8Array(it) }
    fun getUint8ClampedArray(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Uint8ClampedArray")))?.let { Uint8ClampedArray(it) }
    fun getInt16Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Int16Array")))?.let { Int16Array(it) }
    fun getUint16Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Uint16Array")))?.let { Uint16Array(it) }
    fun getInt32Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Int32Array")))?.let { Int32Array(it) }
    fun getUint32Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Uint32Array")))?.let { Uint32Array(it) }
    fun getFloat32Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Float32Array")))?.let { Float32Array(it) }
    fun getFloat64Array(key: String) =
        (transferables.get<ArrayBuffer>(suffixedKey(key, "Float64Array")))?.let { Float64Array(it) }

    fun getImageData(key: String): ImageData? {
        val buffer = transferables.get<ArrayBuffer>(suffixedKey(key, "ImageData_buffer")) ?: return null
        val width = metadata[suffixedKey(key, "ImageData_width")] as? Int ?: return null
        val height = metadata[suffixedKey(key, "ImageData_height")] as? Int ?: return null

        return ImageData(Uint8ClampedArray(buffer), width, height)
    }

    // endregion

    fun toJson(): Json {
        return json(
            CLONEABLE_NAMES_KEY to cloneables.keys.toTypedArray(),
            TRANSFERABLE_NAMES_KEY to transferables.keys.toTypedArray(),
            METADATA_NAMES_KEY to metadata.keys.toTypedArray(),
            *(cloneables.entries.map { it.toPair() }.toTypedArray() + transferables.entries.map { it.toPair() }.toTypedArray() + metadata.entries.map { it.toPair() }
                .toTypedArray())
        )
    }

    fun toValues(): Array<Any> {
        return transferables.values.toTypedArray()
    }
}
