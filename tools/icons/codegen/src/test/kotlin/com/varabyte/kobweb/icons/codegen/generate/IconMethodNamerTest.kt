package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.model.IconMethodNamer
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class IconMethodNamerTest {
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
