package com.varabyte.kobweb.silk.style.layer

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import kotlin.test.Test

class LayerListBuilderTest {
    @Test
    fun defaultBuilderProducesSilkLayers() {
        val builder = LayerListBuilder()
        assertThat(builder.build()).containsExactly(SilkLayer.entries.map { it.layerName }).inOrder()
    }

    @Test
    fun canAddCustomLayersAfterAndIntoSilkLayers() {
        val builder = LayerListBuilder()
        builder.add("utilities", "overrides")
        builder.add("thirdparty", after = SilkLayer.BASE)

        val (beforeBaseLayers, afterBaseLayers) = SilkLayer.entries.partition { it.ordinal < SilkLayer.COMPONENT_STYLES.ordinal }

        assertThat(builder.build()).containsExactly(
            buildList {
                addAll(beforeBaseLayers.map { it.layerName })
                add("thirdparty")
                addAll(afterBaseLayers.map { it.layerName })
                add("utilities")
                add("overrides")
            }
        ).inOrder()
    }

    @Test
    fun canUseCustomLayersAsAfterValue() {
        val builder = LayerListBuilder()
        builder.add("a", "b", "d")
        builder.add("c", after = "b")
        builder.add("e")
        builder.add("f", after = "e")

        assertThat(builder.build()).containsAllIn("a", "b", "c", "d", "e", "f").inOrder()
    }

    @Test
    fun afterParameterMustExist() {
        val builder = LayerListBuilder()
        builder.add("a", "b", "c")
        assertThrows<IllegalArgumentException> {
            builder.add("x", after = "z")
        }
    }

    @Test
    fun duplicateLayerNamesAreNotAllowed() {
        val builder = LayerListBuilder()

        builder.add("a", "b", "c")

        assertThrows<IllegalArgumentException> {
            builder.add("b")
        }

        assertThrows<IllegalArgumentException> {
            builder.add(SilkLayer.BASE.layerName)
        }
    }
}
