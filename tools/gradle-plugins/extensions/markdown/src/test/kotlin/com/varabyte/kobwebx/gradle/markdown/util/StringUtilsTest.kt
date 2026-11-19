@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.truthish.assertThat
import org.junit.Test

class StringUtilsTest {
    @Test
    fun `escapes quotes`() {
        assertThat("""say "hi"""".escapeQuotes()).isEqualTo("""say \"hi\"""")
    }

    @Test
    fun `escapes dollars so they work in both single and triple quoted strings`() {
        assertThat("$5".escapeDollars()).isEqualTo($$"""${'$'}5""")
    }

    @Test
    fun `escapes triple quotes`() {
        assertThat("a\"\"\"b".escapeTripleQuotes()).isEqualTo($$"""a${"\"\"\""}b""")
    }

    @Test
    fun `escapes backslashes`() {
        assertThat("""C:\Users""".escapeBackslashes()).isEqualTo("""C:\\Users""")
    }

    @Test
    fun `escapes new lines`() {
        assertThat("a\nb".escapeNewLines()).isEqualTo("""a\nb""")
    }
}
