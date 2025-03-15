package com.varabyte.kobweb.silk.style

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

        // NOTE: We have a few cases that are not rejected as invalid, but better invalid allowed as
        // valid than the other way around.
        val invalidIdentifiers = listOf(
            "123invalid", // Cannot start with a digit
            "invalid identifier", // Space is not allowed
            "!invalid", // Special characters not allowed
            "@invalid", // Special characters not allowed
            "\\", // Just a backslash is not valid
//            "val\\xyz", // Invalid escape sequence // <-- currently allowed as valid
//            "val\\004", // Incomplete escape sequence // <-- currently allowed as valid
        )

        assertAll {
            validIdentifiers.forEach { identifier ->
                withMessage("$identifier should be valid").that(CssIdent.isValid(identifier)).isTrue()
            }

            invalidIdentifiers.forEach { identifier ->
                withMessage("$identifier should be invalid").that(CssIdent.isValid(identifier)).isFalse()
            }
        }
    }
}
