package com.varabyte.kobweb.browser.dom.css

import com.varabyte.truthish.assertAll
import kotlin.test.Test

class CssIdentTest {
    @Test
    fun testIdentifierValidityWorks() {
        val validIdentifiers = listOf(
            "validIdentifier",
            "_validIdentifier",
            "-validIdentifier",
            "valid-identifier",
            "valid_identifier",
            "valid123",
            "😊valid", // Unicode support
            "\u00A0valid", // Non-ASCII Unicode
            "\\0041valid", // Escaped 'A'
            "valid\\0041", // Escaped 'A' inside identifier
        )

        val invalidIdentifiers = listOf(
            "123invalid", // Cannot start with a digit
            "invalid identifier", // Space is not allowed
            "!invalid", // Special characters not allowed
            "@invalid", // Special characters not allowed
            "\\", // Just a backslash is not valid
        )

        assertAll {
            validIdentifiers.forEach { identifier ->
                withMessage("$identifier should be valid").that(CssIdent.isValid(identifier)).isTrue()
            }

            invalidIdentifiers.forEach { identifier ->
                withMessage("$identifier should be invalid").that(CssIdent.isValid(identifier)).isFalse()
            }
        }

        // NOTE: We have a few cases that are not rejected as invalid, but oh well -- better allowing invalid values
        // than rejecting valid ones. However, if some day we can fix these cases and move them into the
        // `invalidIdentifiers` list, then that would be nice.
        val validButShouldBeInvalid = listOf(
            "val\\xyz", // Invalid escape sequence
            "val\\004", // Incomplete escape sequence
        )

        assertAll {
            validButShouldBeInvalid.forEach { identifier ->
                withMessage("$identifier is valid but if this is fixed some day, this might throw an error meaning this test should be updated")
                    .that(CssIdent.isValid(identifier))
                    .isTrue()
            }
        }
    }
}
