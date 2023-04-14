package com.varabyte.kobweb.gradle.application.util

import com.microsoft.playwright.Playwright
import com.varabyte.kobweb.gradle.application.Browser
import java.security.Permission

/**
 * Download browsers for Playwright if they don't already exist.
 *
 * The reason we control this behavior instead of letting Playwright do whatever it wants is
 * because Playwright downloads way more than we need by default.
 *
 * This class works by delegating to the playwright CLI. See also:
 * https://playwright.dev/java/docs/browsers#install-browsers
 */
internal class PlaywrightCache {

    // Note: We delegate to the Playwright CLI, which calls System.exit() when it's done. But we don't want
    // to exit Gradle... so we temporarily prevent this.
    // See also articles like https://www.javacodegeeks.com/2013/11/preventing-system-exit-calls.html
    private class ExitPreventedException(val status: Int) : SecurityException()
    private class NoExitSecurityManager : SecurityManager() {
        override fun checkPermission(perm: Permission) = Unit
        override fun checkPermission(perm: Permission, context: Any?) = Unit

        override fun checkExit(status: Int) {
            super.checkExit(status)
            throw ExitPreventedException(status)
        }
    }

    /**
     * The version of playwright used to populate the cache.
     *
     * Can be useful for uniquely identifying some cache bucket, so a CI can discard older caches.
     */
    val version = Playwright::class.java.getPackage().implementationVersion ?: "0.0.0"

    fun install(browser: Browser) {
        val prevSecurityManager = System.getSecurityManager()
        System.setSecurityManager(NoExitSecurityManager())

        try {
            com.microsoft.playwright.CLI.main(arrayOf("install", browser.playwrightName))
        } catch (_: ExitPreventedException) {
        } finally {
            System.setSecurityManager(prevSecurityManager)
        }
    }
}