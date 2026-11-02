package com.varabyte.kobweb.api

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import kotlinx.browser.window
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ApiFetcherTest {
    @Test
    fun apiPathsCannotLeaveApiFolder() = runTest {
        assertThrows<IllegalArgumentException> { window.api.get("../users") }
        assertThrows<IllegalArgumentException> { window.api.get("a/../../users") }
        assertThrows<IllegalArgumentException> { window.api.get("%2e%2e/users") }
        assertThrows<IllegalArgumentException> { window.api.post("..") }
    }

    @Test
    fun tryMethodsReturnNullForApiPathsLeavingApiFolder() = runTest {
        assertThat(window.api.tryGet("../users")).isNull()
        assertThat(window.api.tryPost("..")).isNull()
    }
}
