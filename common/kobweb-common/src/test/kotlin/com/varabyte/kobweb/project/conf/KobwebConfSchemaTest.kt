package com.varabyte.kobweb.project.conf

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.data.DataSize
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import com.varabyte.truthish.assertWithMessage
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import java.io.File

// The default Json instance leaves out properties that still have their default values when encoding. We need them
// included, since encoding a conf with only its required values set is how we find out what the Kotlin defaults are.
private val jsonWithDefaults = Json { encodeDefaults = true }

/**
 * Verifies that the hand-written `kobweb-conf.schema.json` stays in sync with [KobwebConf].
 *
 * Structure, types and defaults are checked here. Descriptions are hand-written and kept up to date by review.
 */
class KobwebConfSchemaTest {
    // The schema lives outside this module (in the repo root's `schemas/` folder), so the test task in this module's
    // build.gradle.kts passes its path in.
    private val schema = Json.parseToJsonElement(
        File(
            System.getProperty("kobweb.conf.schema")
                ?: error("The `kobweb.conf.schema` system property isn't set. Run this test through Gradle.")
        ).readText()
    ).jsonObject

    private val JsonObject.properties get() = getValue("properties").jsonObject
    private val JsonObject.required get() = this["required"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()
    private val JsonObject.types: List<String>
        get() = when (val type = this["type"]) {
            null -> emptyList()
            is JsonArray -> type.map { it.jsonPrimitive.content }
            else -> listOf(type.jsonPrimitive.content)
        }

    private val SerialDescriptor.schemaType: String?
        get() = when (kind) {
            StructureKind.CLASS -> "object"
            StructureKind.LIST -> "array"
            SerialKind.ENUM -> null // Enums are described by the "enum" keyword instead
            PrimitiveKind.STRING -> "string"
            PrimitiveKind.INT, PrimitiveKind.LONG -> "integer"
            PrimitiveKind.BOOLEAN -> "boolean"
            else -> error("Unhandled descriptor kind $kind for $serialName")
        }

    private fun checkStructure(descriptor: SerialDescriptor, schema: JsonObject, path: String) {
        assertWithMessage("$path: additionalProperties should be false")
            .that(schema["additionalProperties"]).isEqualTo(JsonPrimitive(false))
        assertWithMessage("$path: properties")
            .that(schema.properties.keys).containsExactly(descriptor.elementNames.toList())
        assertWithMessage("$path: required")
            .that(schema.required)
            .containsExactly(descriptor.elementNames.filterIndexed { i, _ -> !descriptor.isElementOptional(i) })

        descriptor.elementNames.forEachIndexed { i, name ->
            checkElement(descriptor.getElementDescriptor(i), schema.properties.getValue(name).jsonObject, "$path.$name")
        }
    }

    private fun checkElement(descriptor: SerialDescriptor, schema: JsonObject, path: String) {
        val expectedTypes = listOfNotNull(descriptor.schemaType, "null".takeIf { descriptor.isNullable })
        assertWithMessage("$path: type").that(schema.types).containsExactly(expectedTypes)

        when (descriptor.kind) {
            StructureKind.CLASS -> checkStructure(descriptor, schema, path)
            StructureKind.LIST -> checkElement(
                descriptor.getElementDescriptor(0),
                schema.getValue("items").jsonObject,
                "$path[]"
            )

            SerialKind.ENUM -> assertWithMessage("$path: enum")
                .that(schema.getValue("enum").jsonArray.map { it.jsonPrimitive.content })
                .containsExactly(descriptor.elementNames.toList())

            else -> {}
        }
    }

    /**
     * Checks that every optional value has a schema default matching its Kotlin default.
     *
     * Objects themselves don't need a default (their properties have them), so we recurse into them instead.
     *
     * @param defaults A conf encoded from only its required values, so every other value is its Kotlin default.
     * @param withItems Like [defaults], but with one item (holding only its required values) in each list of
     *   objects, so we can recurse into it to check the defaults of list items.
     */
    private fun checkDefaults(
        descriptor: SerialDescriptor,
        schema: JsonObject,
        defaults: JsonObject,
        withItems: JsonObject,
        path: String,
    ) {
        descriptor.elementNames.forEachIndexed { i, name ->
            val elementDescriptor = descriptor.getElementDescriptor(i)
            val elementSchema = schema.properties.getValue(name).jsonObject
            val elementPath = "$path.$name"

            if (elementDescriptor.kind == StructureKind.CLASS) {
                checkDefaults(
                    elementDescriptor,
                    elementSchema,
                    defaults.getValue(name).jsonObject,
                    withItems.getValue(name).jsonObject,
                    elementPath
                )
                return@forEachIndexed
            }

            if (descriptor.isElementOptional(i)) {
                assertWithMessage("$elementPath: default")
                    .that(elementSchema["default"]).isEqualTo(defaults.getValue(name))
            } else {
                assertWithMessage("$elementPath: required properties shouldn't have a default")
                    .that(elementSchema["default"]).isNull()
            }

            if (elementDescriptor.kind == StructureKind.LIST) {
                val itemDescriptor = elementDescriptor.getElementDescriptor(0)
                if (itemDescriptor.kind == StructureKind.CLASS) {
                    val item = withItems.getValue(name).jsonArray.first().jsonObject
                    val itemSchema = elementSchema.getValue("items").jsonObject
                    checkDefaults(itemDescriptor, itemSchema, item, item, "$elementPath[]")
                }
            }
        }
    }

    @Test
    fun schemaMatchesKobwebConfStructure() {
        checkStructure(KobwebConf.serializer().descriptor, schema, "conf")
    }

    @Test
    fun schemaDefaultsMatchKobwebConfDefaults() {
        val requiredValues = """
            site:
              title: "Test"
            server:
              files:
                dev:
                  contentRoot: "content"
                  script: "dev.js"
                prod:
                  script: "prod.js"
        """.trimIndent()
        // Nested under `server:`
        val serverListItems = """
              cors:
                hosts:
                  - name: "example.com"
              redirects:
                - from: "/a"
                  to: "/b"
              nativeLibraries:
                - name: "test"
                  path: "libtest.so"
        """.replaceIndent("  ")

        fun encode(yaml: String) = jsonWithDefaults.encodeToJsonElement(
            KobwebConf.serializer(),
            Yaml.nonStrictDefault.decodeFromString(KobwebConf.serializer(), yaml)
        ).jsonObject

        checkDefaults(
            KobwebConf.serializer().descriptor,
            schema,
            defaults = encode(requiredValues),
            withItems = encode(requiredValues + "\n" + serverListItems),
            path = "conf"
        )
    }

    @Test
    fun totalSizeCapPatternMatchesDataSizeParsing() {
        val pattern = Regex(
            schema.properties.getValue("server").jsonObject
                .properties.getValue("logging").jsonObject
                .properties.getValue("totalSizeCap").jsonObject
                .getValue("pattern").jsonPrimitive.content
        )

        listOf("0B", "10MiB", "10mib", "500MB", "500mb", "1G", "1g", "2K", "3KB", "4KiB", "5GiB", "6M", "7b")
            .forEach { size ->
                assertWithMessage("$size should match").that(pattern.matches(size)).isTrue()
                assertWithMessage("$size should parse").that(DataSize.tryParse(size)).isNotNull()
            }

        listOf("10", "MiB", "10 MiB", "10TB", "10Mi", "10MIBS", "-1MB", "1.5GB").forEach { size ->
            assertWithMessage("$size should not match").that(pattern.matches(size)).isFalse()
            val parsed = runCatching { DataSize.tryParse(size) }.getOrNull()
            assertWithMessage("$size should not parse").that(parsed).isNull()
        }
    }
}
