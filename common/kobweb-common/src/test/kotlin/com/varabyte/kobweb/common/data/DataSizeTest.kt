package com.varabyte.kobweb.common.data

import com.varabyte.kobweb.common.data.DataSize.Companion.b
import com.varabyte.kobweb.common.data.DataSize.Companion.bytes
import com.varabyte.kobweb.common.data.DataSize.Companion.gb
import com.varabyte.kobweb.common.data.DataSize.Companion.gib
import com.varabyte.kobweb.common.data.DataSize.Companion.gibibytes
import com.varabyte.kobweb.common.data.DataSize.Companion.gigabytes
import com.varabyte.kobweb.common.data.DataSize.Companion.kb
import com.varabyte.kobweb.common.data.DataSize.Companion.kib
import com.varabyte.kobweb.common.data.DataSize.Companion.kibibytes
import com.varabyte.kobweb.common.data.DataSize.Companion.kilobytes
import com.varabyte.kobweb.common.data.DataSize.Companion.mb
import com.varabyte.kobweb.common.data.DataSize.Companion.mebibytes
import com.varabyte.kobweb.common.data.DataSize.Companion.megabytes
import com.varabyte.kobweb.common.data.DataSize.Companion.mib
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertWithMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class DataSizeTest {
    @Test
    fun confirmEqualityRequiresSameUnitTypes() {
        assertThat(DataSize.parse("1000B")).isEqualTo(DataSize.Bytes(1000))
        assertThat(DataSize.parse("1000B")).isNotEqualTo(DataSize.Kilobytes(1))
        assertThat(DataSize.parse("1000B").inWholeBytes).isEqualTo(DataSize.Kilobytes(1).inWholeBytes)
    }

    @Test
    fun canUseExtensionUnits() {
        assertThat(123.b).isEqualTo(DataSize.Bytes(123))
        assertThat(123.kb).isEqualTo(DataSize.Kilobytes(123))
        assertThat(123.mb).isEqualTo(DataSize.Megabytes(123))
        assertThat(123.gb).isEqualTo(DataSize.Gigabytes(123))
        assertThat(123.kib).isEqualTo(DataSize.Kibibytes(123))
        assertThat(123.mib).isEqualTo(DataSize.Mebibytes(123))
        assertThat(123.gib).isEqualTo(DataSize.Gibibytes(123))

        assertThat(123.bytes).isEqualTo(DataSize.Bytes(123))
        assertThat(123.kilobytes).isEqualTo(DataSize.Kilobytes(123))
        assertThat(123.megabytes).isEqualTo(DataSize.Megabytes(123))
        assertThat(123.gigabytes).isEqualTo(DataSize.Gigabytes(123))
        assertThat(123.kibibytes).isEqualTo(DataSize.Kibibytes(123))
        assertThat(123.mebibytes).isEqualTo(DataSize.Mebibytes(123))
        assertThat(123.gibibytes).isEqualTo(DataSize.Gibibytes(123))
    }

    @Test
    fun dataSizeHasCorrectStringRepresentations() {
        assertThat(123.b.toString()).isEqualTo("123B")
        assertThat(123.kb.toString()).isEqualTo("123KB")
        assertThat(123.mb.toString()).isEqualTo("123MB")
        assertThat(123.gb.toString()).isEqualTo("123GB")
        assertThat(123.kib.toString()).isEqualTo("123KiB")
        assertThat(123.mib.toString()).isEqualTo("123MiB")
        assertThat(123.gib.toString()).isEqualTo("123GiB")
    }

    @Test
    fun parseDataSizes() {
        assertThat(DataSize.parse("123B")).isEqualTo(DataSize.Bytes(123))

        assertThat(DataSize.parse("123K")).isEqualTo(DataSize.Kilobytes(123))
        assertThat(DataSize.parse("123KB")).isEqualTo(DataSize.Kilobytes(123))
        assertThat(DataSize.parse("123K", forceBinarySize = true)).isEqualTo(DataSize.Kibibytes(123))
        assertThat(DataSize.parse("123KB", forceBinarySize = true)).isEqualTo(DataSize.Kibibytes(123))
        assertThat(DataSize.parse("123KiB")).isEqualTo(DataSize.Kibibytes(123))

        assertThat(DataSize.parse("123M")).isEqualTo(DataSize.Megabytes(123))
        assertThat(DataSize.parse("123MB")).isEqualTo(DataSize.Megabytes(123))
        assertThat(DataSize.parse("123M", forceBinarySize = true)).isEqualTo(DataSize.Mebibytes(123))
        assertThat(DataSize.parse("123MB", forceBinarySize = true)).isEqualTo(DataSize.Mebibytes(123))
        assertThat(DataSize.parse("123MiB")).isEqualTo(DataSize.Mebibytes(123))

        assertThat(DataSize.parse("123G")).isEqualTo(DataSize.Gigabytes(123))
        assertThat(DataSize.parse("123GB")).isEqualTo(DataSize.Gigabytes(123))
        assertThat(DataSize.parse("123G", forceBinarySize = true)).isEqualTo(DataSize.Gibibytes(123))
        assertThat(DataSize.parse("123GB", forceBinarySize = true)).isEqualTo(DataSize.Gibibytes(123))
        assertThat(DataSize.parse("123GiB")).isEqualTo(DataSize.Gibibytes(123))
    }

    @Test
    fun canConvertBetweenSizes() {
        assertThat(2.gb.inWholeMegabytes).isEqualTo(2000L)
        assertThat(2048.bytes.inWholeKibibytes).isEqualTo(2L)

        // 2 * 1024 * 1024 * 1024 = 2147483648 bytes
        // 2147483648 / 1000 / 1000 = 2147 megabytes
        assertThat(2.gib.inWholeMegabytes).isEqualTo(2147L)
    }

    @Test
    fun canSerializeAndDeserialize() {
        val sizes = listOf(
            123.b,
            123.kb,
            123.mb,
            123.gb,
            123.kib,
            123.mib,
            123.gib,
        )

        sizes.forEach { size ->
            val serialized = Json.encodeToString(size)
            val deserialized = Json.decodeFromString(DataSize.serializer(), serialized)

            assertWithMessage("Unexpected serialization failure for $size").that(deserialized).isEqualTo(size)
        }
    }
}
