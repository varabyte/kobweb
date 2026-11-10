package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class IconMethodNamerTest {
    @Test
    fun capitalizesEachPartAndAddsPrefix() {
        val namer = IconMethodNamer(prefix = "Lucide", separator = '-')
        assertThat(namer.methodNameFor("arrow-right")).isEqualTo("LucideArrowRight")
        assertThat(namer.methodNameFor("arrow-1")).isEqualTo("LucideArrow1")
    }

    @Test
    fun onlySplitsOnTheSeparator() {
        val namer = IconMethodNamer(prefix = "Ms", separator = '_')
        assertThat(namer.methodNameFor("arrow_back")).isEqualTo("MsArrowBack")
        assertThat(namer.methodNameFor("arrow-back")).isEqualTo("MsArrow-back")
    }

    @Test
    fun uniqueNamesPass() {
        IconMethodNamer(prefix = "Fa", separator = '-').checkUnique(listOf("bell", "bell-slash"))
    }

    @Test
    fun clashingNamesAreRejected() {
        val e = assertThrows<IllegalStateException> {
            IconMethodNamer(prefix = "Fa", separator = '-').checkUnique(listOf("arrow-1", "arrow1", "bell"))
        }
        assertThat(e.message!!).isEqualTo("Multiple icons map to the same method name: {FaArrow1=[arrow-1, arrow1]}")
    }
}
