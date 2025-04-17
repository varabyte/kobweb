package com.varabyte.kobwebx.markdown

import com.varabyte.kobwebx.frontmatter.FrontMatterElement
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import kotlin.test.Test

class FrontMatterElementTest {
    @Test
    fun ensureQueryingWorks() {
        val fmElement = FrontMatterElement.Builder {
            addScalar("title", "Title")
            addMap("data") {
                addMap("assets") {
                    addList("images") {
                        addScalar("cat.png")
                        addScalar("dog.png")
                    }
                    addScalar("font", "Roboto")
                }
            }
        }

        assertThat(fmElement.query("title")).isInstanceOf<FrontMatterElement.Scalar>()
        assertThat(fmElement.query("data.assets.images")).isInstanceOf<FrontMatterElement.ValueList>()
        assertThat(fmElement.query("data.assets.images.1")).isInstanceOf<FrontMatterElement.Scalar>()
        assertThat(fmElement.query("data.assets.font")).isInstanceOf<FrontMatterElement.Scalar>()
        assertThat(fmElement.query("data")).isInstanceOf<FrontMatterElement.ValueMap>()
        assertThat(fmElement.query("data.x.y.z")).isNull() // key doesn't exist
        assertThat(fmElement.query("data.assets.images.99")).isNull() // index out of bounds
        assertThat(fmElement.query("data.assets.font.Roboto")).isNull() // You can't query values, only keys
    }

    @Test
    fun ensureGetOperatorWorks() {
        val fmElement = FrontMatterElement.Builder {
            addScalar("title", "Title")
            addMap("data") {
                addMap("assets") {
                    addList("images") {
                        addScalar("cat.png")
                        addScalar("dog.png")
                    }
                    addScalar("font", "Roboto")
                }
            }
        }

        assertThat(fmElement["title"]).isEqualTo(listOf("Title"))
        assertThat(fmElement["data.assets.images"]).isEqualTo(listOf("cat.png", "dog.png"))
        assertThat(fmElement["data.assets.images.1"]).isEqualTo(listOf("dog.png"))
        assertThat(fmElement["data.assets.font"]).isEqualTo(listOf("Roboto"))
        assertThat(fmElement["data"]).isNull()
        assertThat(fmElement["data.x.y.z"]).isNull()
        assertThat(fmElement["data.assets.images.99"]).isNull()
        assertThat(fmElement["data.assets.font.Roboto"]).isNull()

        // Ensure getValue throws or not throws (as expected)
        fmElement.getValue("title")
        fmElement.getValue("data.assets.images")
        fmElement.getValue("data.assets.images.0")
        fmElement.getValue("data.assets.font")
        assertThrows<NoSuchElementException> { fmElement.getValue("data") }
        assertThrows<NoSuchElementException> { fmElement.getValue("data.x.y.z") }
        assertThrows<NoSuchElementException> { fmElement.getValue("data.assets.images.5") }
        assertThrows<NoSuchElementException> { fmElement.getValue("data.assets.font.Roboto") }
    }

    @Test
    fun dotsAreNotAllowedInKeys() {
        assertThrows<IllegalArgumentException> {
            FrontMatterElement.Builder {
                addScalar("invalid.key", "value")
            }
        }

        assertThrows<IllegalArgumentException> {
            FrontMatterElement.Builder {
                addMap("invalid.key") {}
            }
        }

        assertThrows<IllegalArgumentException> {
            FrontMatterElement.Builder {
                addList("invalid.key") {}
            }
        }

        // Values are fine
        FrontMatterElement.Builder {
            addScalar("validKey1", "valid.value")
            addList("validKey2") {
                addScalar("valid.value")
            }
        }
    }
}