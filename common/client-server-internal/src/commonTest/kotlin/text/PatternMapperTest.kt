package text

import com.varabyte.kobweb.util.text.PatternMapper
import com.varabyte.truthish.assertThat
import kotlin.test.Test

class PatternMapperTest {
    @Test
    fun simpleMappingWorks() {
        val mapper = PatternMapper("Hello .+", "Greeting")
        assertThat(mapper.map("Hello World")).isEqualTo("Greeting")
    }

    @Test
    fun substitutionWorks() {
        val mapper = PatternMapper("(.)/(.)", "$2/$1")
        assertThat(mapper.map("a/b")).isEqualTo("b/a")
    }

    @Test
    fun noMappingProducedIfNoMatch() {
        val mapper = PatternMapper("(.)/(.)", "$2/$1")
        assertThat(mapper.map("hi")).isNull()
    }

    @Test
    fun dollarOneDoesNotConsumeDollarElevenEtc() {
        // A naive implementation would replace "$11" with the value of "$1"
        val mapper =
            PatternMapper("(.) (.) (.) (.) (.) (.) (.) (.) (.) (.) (.) (.)", "$12 $11 $10 $9 $8 $7 $6 $5 $4 $3 $2 $1")
        assertThat(mapper.map("a b c d e f g h i j k l")).isEqualTo("l k j i h g f e d c b a")
    }
}
