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
import org.w3c.dom.ImageBitmap
import org.w3c.dom.ImageData
import org.w3c.dom.MessagePort
import kotlin.js.Json
import kotlin.js.json

/**
 * A Json key used to store a list of additional keys.
 *
 * This will allow us to store and later retrieve key/value pairs of transferable values into a json object which we can
 * then query out later.
 */
private const val TRANSFERABLE_KEYS_KEY = "_transferableKeys"
private const val EXTRA_KEYS_KEY = "_extraKeys"

private fun suffixedKey(key: String, suffix: String?) = key + suffix?.let { "_$it" }.orEmpty()

/**
 * Transferables are special objects which are allowed to be moved between threads, rather than copied.
 *
 * Transferables are essentially a performance optimization. While serialization may work fine in most cases, it can
 * cause a noticeable, even multi-second stutter for very sizable objects (like large bitmaps).
 *
 * Transferables allow the developer to tell the browser that you want to take a large object and send it across
 * threads rather than copy it. After a transferable operation is complete, the original value is considered dead in the
 * thread sending it.
 *
 * Not every object can be transferred. In order to constrain users from sending unsupported types, we require users
 * create a `Transferables` instance using a [Transferables.Builder].
 *
 * Unlike default JS APIs, which just send an array of transferables, we require the user to specify the name of each
 * object, as an added layer of safety.
 *
 * ```
 * // Sender
 * worker.postMessage(message, Transferables { add("bitmap", bitmap) })
 *
 * // Receiver
 * internal class TransferableWorkerFactory : WorkerFactory<String, String> {
 *     override fun createStrategy(postOutput: OutputDispatcher<String> -> Unit) = WorkerStrategy<String> { input ->
 *         val bitmap = transferables.getImageBitmap("bitmap")
 *         ...
 *     }
 *
 *     override fun createIOSerializer() = createPassThroughSerializer()
 * }
 * ```
 *
 * Not every object in the web is transferable, so the `add` methods and `get` methods are appropriately typed to
 * constraint what you can register and fetch.
 *
 * Note that arrays are actually not transferable, but we add support for them here anyway, doing the conversion to
 * their underlying `ArrayBuffer` ourselves to avoid the users having to do a bunch of boilerplate.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects">Transferable objects</a>
 *
 * @property transferables A map of names to transferable objects.
 * @property extras A map of additional values describe metadata around the transferable objects but are not themselves
 *   transferable. These should be limited to primitive values, such as ints or strings.
 */
class Transferables private constructor(
    private val transferables: Map<String, Any>,
    private val extras: Map<String, Any>
) {
    companion object {
        val Empty = Transferables(emptyMap(), emptyMap())

        /**
         * Convenience method to remove some boilerplate around using a [Builder].
         */
        operator fun invoke(init: Builder.() -> Unit): Transferables {
            return Builder().apply(init).build()
        }

        /**
         * Deserialize a Transferables object from a JSON object that was created using [Transferables.toJson].
         */
        fun fromJson(json: Json): Transferables {
            val transferableNames = json[TRANSFERABLE_KEYS_KEY]?.unsafeCast<Array<String>>() ?: return Empty
            val extraNames = json[EXTRA_KEYS_KEY]?.unsafeCast<Array<String>>() ?: return Empty

            val transferables = mutableMapOf<String, Any>()
            for (name in transferableNames) {
                transferables[name] = json[name]!!
            }

            val extras = mutableMapOf<String, Any>()
            for (name in extraNames) {
                extras[name] = json[name]!!
            }
            return Transferables(transferables, extras)
        }
    }

    class Builder {
        private val transferables = mutableMapOf<String, Any>()
        private val extras = mutableMapOf<String, Any>()

        // Note: Suffix specified separately from key, so we can show the user a better error message that doesn't leak
        // the internal suffix to the user.
        @Suppress("FunctionName") // private helper method
        private fun _add(key: String, suffix: String?, value: Any): Builder {
            if (transferables.put(suffixedKey(key, suffix), value) != null) {
                error("Transferable with key \"$key\" was added twice")
            }
            return this
        }

        @Suppress("FunctionName") // private helper method
        private fun _add(key: String, value: Any) = _add(key, suffix = null, value)

        fun add(key: String, value: ArrayBuffer) = _add(key, value)
        fun add(key: String, value: MessagePort) = _add(key, value)
        fun add(key: String, value: ImageBitmap) = _add(key, value)
        // TODO: There are more official types that are supported, see
        //  https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects#supported_objects
        //  However, they aren't currently available in the Kotlin/JS stdlib, so if people ask for their support, we
        //  probably need to define the object ourselves.

        // region Convenience "transferables"

        // These methods allow adding values that themselves are not directly transferable but we can wrap transferable
        // concepts underneath them ourselves for user convenience.

        fun add(key: String, value: Int8Array) = _add(key, "Int8Array", value.buffer)
        fun add(key: String, value: Uint8Array) = _add(key, "Uint8Array", value.buffer)
        fun add(key: String, value: Uint8ClampedArray) = _add(key, "Uint8ClampedArray", value.buffer)
        fun add(key: String, value: Int16Array) = _add(key, "Int16Array", value.buffer)
        fun add(key: String, value: Uint16Array) = _add(key, "Uint16Array", value.buffer)
        fun add(key: String, value: Int32Array) = _add(key, "Int32Array", value.buffer)
        fun add(key: String, value: Uint32Array) = _add(key, "Uint32Array", value.buffer)
        fun add(key: String, value: Float32Array) = _add(key, "Float32Array", value.buffer)
        fun add(key: String, value: Float64Array) = _add(key, "Float64Array", value.buffer)
        fun add(key: String, value: ImageData) {
            _add(key, "ImageData_buffer", value.data.buffer)
            extras[suffixedKey(key, "ImageData_width")] = value.width
            extras[suffixedKey(key, "ImageData_height")] = value.height
        }
        // endregion

        fun build() = Transferables(transferables, extras)
    }

    /**
     * Fetch the transferable associated with the key.
     *
     * This will return null if there is no transferable matching the key name or if the requested type is not the
     * correct type.
     */
    private fun <T : Any> get(key: String): T? {
        val transferable = transferables[key] ?: return null
        @Suppress("UNCHECKED_CAST") // It's a nullable cast so not sure why the compiler is complaining
        return transferable as? T
    }

    fun getArrayBuffer(key: String): ArrayBuffer? = get(key)
    fun getMessagePort(key: String): MessagePort? = get(key)
    fun getImageBitmap(key: String): ImageBitmap? = get(key)

    fun getInt8Array(key: String): Int8Array? =
        (get(suffixedKey(key, "Int8Array")) as? ArrayBuffer)?.let { Int8Array(it) }

    fun getUint8Array(key: String): Uint8Array? =
        (get(suffixedKey(key, "Uint8Array")) as? ArrayBuffer)?.let { Uint8Array(it) }
    fun getUint8ClampedArray(key: String): Uint8ClampedArray? =
        (get(suffixedKey(key, "Uint8ClampedArray")) as? ArrayBuffer)?.let { Uint8ClampedArray(it) }

    fun getInt16Array(key: String): Int16Array? =
        (get(suffixedKey(key, "Int16Array")) as? ArrayBuffer)?.let { Int16Array(it) }

    fun getUint16Array(key: String): Uint16Array? =
        (get(suffixedKey(key, "Uint16Array")) as? ArrayBuffer)?.let { Uint16Array(it) }

    fun getInt32Array(key: String): Int32Array? =
        (get(suffixedKey(key, "Int32Array")) as? ArrayBuffer)?.let { Int32Array(it) }

    fun getUint32Array(key: String): Uint32Array? =
        (get(suffixedKey(key, "Uint32Array")) as? ArrayBuffer)?.let { Uint32Array(it) }
    fun getFloat32Array(key: String): Float32Array? =
        (get(suffixedKey(key, "Float32Array")) as? ArrayBuffer)?.let { Float32Array(it) }

    fun getFloat64Array(key: String): Float64Array? =
        (get(suffixedKey(key, "Float64Array")) as? ArrayBuffer)?.let { Float64Array(it) }

    fun getImageData(key: String): ImageData? {
        val buffer = get<ArrayBuffer>(suffixedKey(key, "ImageData_buffer")) ?: return null
        val width = extras[suffixedKey(key, "ImageData_width")] as? Int ?: return null
        val height = extras[suffixedKey(key, "ImageData_height")] as? Int ?: return null

        return ImageData(Uint8ClampedArray(buffer), width, height)
    }

    fun toJson(): Json {
        return json(
            TRANSFERABLE_KEYS_KEY to transferables.keys.toTypedArray(),
            EXTRA_KEYS_KEY to extras.keys.toTypedArray(),
            *(transferables.entries.map { it.toPair() }.toTypedArray() + extras.entries.map { it.toPair() }
                .toTypedArray())
        )
    }

    fun toValues(): Array<Any> {
        return transferables.values.toTypedArray()
    }
}
