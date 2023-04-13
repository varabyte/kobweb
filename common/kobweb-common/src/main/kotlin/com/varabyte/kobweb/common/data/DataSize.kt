package com.varabyte.kobweb.common.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class DataSizeSerializer : KSerializer<DataSize> {
    private val delegateSerializer = String.serializer()
    override val descriptor = PrimitiveSerialDescriptor("DataSize", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DataSize) {
        delegateSerializer.serialize(encoder, value.toString())
    }

    override fun deserialize(decoder: Decoder): DataSize {
        return DataSize.parse(delegateSerializer.deserialize(decoder))
    }
}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE") // Our delegate serializer handles subclasses fine
@Serializable(with = DataSizeSerializer::class)
sealed class DataSize(val amount: Long, private val factor: Int) {
    val inWholeBytes = amount * factor
    val inWholeKilobytes = inWholeBytes / 1000
    val inWholeMegabytes = inWholeKilobytes / 1000
    val inWholeGigabytes = inWholeMegabytes / 1000
    val inWholeKibibytes = inWholeBytes / 1024
    val inWholeMebibytes = inWholeKibibytes / 1024
    val inWholeGibibytes = inWholeMebibytes / 1024

    override fun equals(other: Any?): Boolean {
        return other is DataSize
                && this::class == other::class // 1000B != 1KB
                && this.amount == other.amount
    }
    override fun hashCode() = (amount * factor).hashCode()

    @Serializable(with = DataSizeSerializer::class)
    class Bytes(amount: Long) : DataSize(amount, 1) {
        companion object { const val Unit = "B" }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Kilobytes(amount: Long) : DataSize(amount, 1 * 1000) {
        companion object {
            const val Unit = "KB"
            const val UnitAlt = "K"
        }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Kibibytes(amount: Long) : DataSize(amount, 1 * 1024) {
        companion object { const val Unit = "KiB" }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Megabytes(amount: Long) : DataSize(amount, 1 * 1000 * 1000) {
        companion object {
            const val Unit = "MB"
            const val UnitAlt = "M"
        }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Mebibytes(amount: Long) : DataSize(amount, 1 * 1024 * 1024) {
        companion object { const val Unit = "MiB" }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Gigabytes(amount: Long) : DataSize(amount, 1 * 1000 * 1000 * 1000) {
        companion object {
            const val Unit = "GB"
            const val UnitAlt = "G"
        }
        override fun toString() = "$amount$Unit"
    }
    @Serializable(with = DataSizeSerializer::class)
    class Gibibytes(amount: Long) : DataSize(amount, 1 * 1024 * 1024 * 1024) {
        companion object { const val Unit = "GiB" }
        override fun toString() = "$amount$Unit"
    }

    companion object {
        inline val Long.b get() = Bytes(this)
        inline val Long.kb get() = Kilobytes(this)
        inline val Long.kib get() = Kibibytes(this)
        inline val Long.mb get() = Megabytes(this)
        inline val Long.mib get() = Mebibytes(this)
        inline val Long.gb get() = Gigabytes(this)
        inline val Long.gib get() = Gibibytes(this)
        inline val Long.bytes get() = Bytes(this)
        inline val Long.kilobytes get() = Kilobytes(this)
        inline val Long.kibibytes get() = Kibibytes(this)
        inline val Long.megabytes get() = Megabytes(this)
        inline val Long.mebibytes get() = Mebibytes(this)
        inline val Long.gigabytes get() = Gigabytes(this)
        inline val Long.gibibytes get() = Gibibytes(this)

        inline val Int.b get() = Bytes(this.toLong())
        inline val Int.kb get() = Kilobytes(this.toLong())
        inline val Int.kib get() = Kibibytes(this.toLong())
        inline val Int.mb get() = Megabytes(this.toLong())
        inline val Int.mib get() = Mebibytes(this.toLong())
        inline val Int.gb get() = Gigabytes(this.toLong())
        inline val Int.gib get() = Gibibytes(this.toLong())
        inline val Int.bytes get() = Bytes(this.toLong())
        inline val Int.kilobytes get() = Kilobytes(this.toLong())
        inline val Int.kibibytes get() = Kibibytes(this.toLong())
        inline val Int.megabytes get() = Megabytes(this.toLong())
        inline val Int.mebibytes get() = Mebibytes(this.toLong())
        inline val Int.gigabytes get() = Gigabytes(this.toLong())
        inline val Int.gibibytes get() = Gibibytes(this.toLong())

        /**
         * Parse a string into a [DataSize] object.
         *
         * The string must be in the format of "<number><unit>" where the unit is one of
         * ["B", "K", "M", "G", "KB", "MB", "GB", "KiB", "MiB", "GiB"] (not case sensitive).
         *
         * @param forceBinarySize If true, always return the data size that's a power of 2. For example,
         *   `parse("1KB", forceBinarySize = true)` will return 1024B, not 1000B as it otherwise would. This is because
         *   users commonly think of "GB" as 1024³, not 1000³. Note that this property has no effect if the unit is
         *   already a binary unit type (e.g. "1KiB").
         */
        fun tryParse(str: String, forceBinarySize: Boolean = false): DataSize? {
            val amountStr = str.takeWhile { it.isDigit() }
            val amount = amountStr.toLongOrNull()
            check(amount != null && amountStr.isNotEmpty()) { "Expected a number before the unit, but got '$str'" }

            val unitStr = str.removePrefix(amountStr)
            check(unitStr.isNotEmpty()) { "Expected a unit after the number, but got '$str'" }

            return when {
                unitStr.equals(Bytes.Unit, ignoreCase = true) -> Bytes(amount)

                unitStr.equals(Kilobytes.Unit, ignoreCase = true)
                        || unitStr.equals(Kilobytes.UnitAlt, ignoreCase = true) -> {
                    if (forceBinarySize) Kibibytes(amount) else Kilobytes(amount)
                }
                unitStr.equals(Kibibytes.Unit, ignoreCase = true) -> Kibibytes(amount)

                unitStr.equals(Megabytes.Unit, ignoreCase = true)
                        || unitStr.equals(Megabytes.UnitAlt, ignoreCase = true) -> {
                    if (forceBinarySize) Mebibytes(amount) else Megabytes(amount)
                }
                unitStr.equals(Mebibytes.Unit, ignoreCase = true) -> Mebibytes(amount)

                unitStr.equals(Gigabytes.Unit, ignoreCase = true)
                        || unitStr.equals(Gigabytes.UnitAlt, ignoreCase = true) -> {
                    if (forceBinarySize) Gibibytes(amount) else Gigabytes(amount)
                }
                unitStr.equals(Gibibytes.Unit, ignoreCase = true) -> Gibibytes(amount)

                else -> null
            }
        }

        /**
         * Same as [tryParse], but throws an exception if the string is not a valid data size.
         */
        fun parse(str: String, forceBinarySize: Boolean = false): DataSize {
            return tryParse(str, forceBinarySize) ?: error("Invalid data size '$str'")
        }
    }
}
