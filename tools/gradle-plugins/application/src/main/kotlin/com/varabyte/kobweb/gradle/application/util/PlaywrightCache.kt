package com.varabyte.kobweb.gradle.application.util

import com.microsoft.playwright.Playwright
import com.microsoft.playwright.impl.driver.Driver
import com.varabyte.kobweb.gradle.application.Browser

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
    /**
     * The version of playwright used to populate the cache.
     *
     * Can be useful for uniquely identifying some cache bucket, so a CI can discard older caches.
     */
    val version = Playwright::class.java.getPackage().implementationVersion ?: "0.0.0"

    fun install(browser: Browser) {
        // HACK: I gave up and threw in the towel for now. The following code is a fork of Playwright code
        // https://github.com/microsoft/playwright-java/blob/5a4640fe2af67886a4ccbd1b5cd6b93476d22281/playwright/src/main/java/com/microsoft/playwright/CLI.java#L41
        // with the only difference that my version doesn't `System.exit()` when finished.
        // NOTE: We used to call Playwright code directly while blocking exiting using the Java security manager.
        // However, the Java security manager is being deprecated without any clear replacement for handling the exit
        // scenario yet, so fine, we're doing this for now. I have more important priorities to work on right now and
        // can't worry yet about the chance that copying this code may explode in our faces in a future version. See
        // below for another option to consider in the future (at least, I think, I attempted it but failed to get the
        // classpath).

        val driver = Driver.ensureDriverInstalled(emptyMap(), false)
        val pb = driver.createProcessBuilder()
        pb.command().addAll(listOf("install", browser.playwrightName))
        val version = Playwright::class.java.getPackage().implementationVersion
        if (version != null) {
            pb.environment()["PW_CLI_DISPLAY_VERSION"] = version
        }
        pb.inheritIO()
        println("Updating browser cache if necessary. This can take a while if out of date...")
        try {
            val process = pb.start()
            println("Browser cache updated. Process returned status code ${process.waitFor()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Another option for consideration is to run com.microsoft.playwright.CLI.main in a separate process, but to do
        // that we need to get the classpath that includes the playwright dependency. It would look something like this:
        //
        //  val javaHome = System.getenv("KOBWEB_JAVA_HOME") ?: System.getProperty("java.home")!!
        //  val classpath = ??????????????
        //  val className = com.microsoft.playwright.CLI::class.java.name
        //  val processParams = listOf(
        //      "${javaHome.toUnixSeparators()}/bin/java", "-cp", classpath, className,
        //      "install", browser.playwrightName
        //  )
        //
        //  try {
        //      val process = ProcessBuilder(processParams).inheritIO().start()
        //      println("Command exited with value ${process.waitFor()}")
        //
        //  } catch (e: Exception) {
        //      e.printStackTrace()
        //  }
    }
}