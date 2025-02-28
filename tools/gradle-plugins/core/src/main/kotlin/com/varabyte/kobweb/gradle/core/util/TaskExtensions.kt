package com.varabyte.kobweb.gradle.core.util

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Return a unique build cache directory for this task.
 *
 * This method should not be called in a task action, as it won't work when configuration caching is enabled.
 */
fun Task.kobwebCacheDir(): Provider<Directory> {
    return project.layout.kobwebCacheDir().map { it.dir(name) }
}

/**
 * Return a file under the cache directory for this task.
 *
 * This method should not be called in a task action, as it won't work when configuration caching is enabled.
 *
 * @see kobwebCacheDir
 */
fun Task.kobwebCacheFile(path: String): Provider<RegularFile> {
    return kobwebCacheDir().map { it.file(path) }
}
