package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask

/**
 * Common base class for all "Kobweb generate" tasks.
 *
 * This allows us to search for all Kobweb generate tasks by type.
 */
abstract class KobwebGenerateTask(desc: String) : KobwebTask(desc)
