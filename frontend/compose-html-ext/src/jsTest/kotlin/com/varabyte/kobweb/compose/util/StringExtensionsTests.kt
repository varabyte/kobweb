package com.varabyte.kobweb.compose.util

import com.varabyte.truthish.assertThat
import kotlin.test.Test

class StringExtensionsTests {
    @Test
    fun titleCamelCaseToKebabCaseWorksAsExpected() {
        val beforeAfterCases: List<Pair<String, String>> = listOf(
            "ExampleText" to "example-text",
            "ExampleTextPartTwo" to "example-text-part-two",
            "ExampleABC" to "example-abc",
            "ABCExample" to "abc-example",
            "Pascal" to "pascal",
            "ALLCAPS" to "allcaps",
            "lowercase" to "lowercase",
        )

        beforeAfterCases.forEach { (before, after) ->
            assertThat(before.titleCamelCaseToKebabCase()).isEqualTo(after)
        }
    }

    @Test
    fun kebabCaseToTitleCaseWorksAsExpected() {
        val beforeAfterCases: List<Pair<String, String>> = listOf(
            "example-text" to "ExampleText",
            "example-text-part-two" to "ExampleTextPartTwo",
            "pascal" to "Pascal",
        )

        beforeAfterCases.forEach { (before, after) ->
            assertThat(before.kebabCaseToTitleCamelCase()).isEqualTo(after)
        }
    }

    @Test
    fun wrapQuotesIfNecessaryWorksAsExpected() {
        val beforeAfterCases: List<Pair<String, String>> = listOf(
            """no-quotes""" to """"no-quotes"""",
            """"pre-quoted"""" to """"pre-quoted"""",
            """"Is the test today?" he asked.""" to """"\"Is the test today?\" he asked."""",
        )

        beforeAfterCases.forEach { (before, after) ->
            assertThat(before.wrapQuotesIfNecessary()).isEqualTo(after)
        }
    }
}
