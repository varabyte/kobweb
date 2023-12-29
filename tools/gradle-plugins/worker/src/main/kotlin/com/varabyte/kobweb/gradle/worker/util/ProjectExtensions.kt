package com.varabyte.kobweb.gradle.worker.util

import com.varabyte.kobweb.gradle.core.util.toUidString
import org.gradle.api.Project

/**
 * Create a worker name that is reasonably likely to be unique.
 */
fun Project.suggestWorkerName() = "worker-${project.toUidString().take(4).lowercase() }"
