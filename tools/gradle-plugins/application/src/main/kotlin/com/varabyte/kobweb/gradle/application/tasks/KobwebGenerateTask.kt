package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import org.gradle.api.tasks.Internal

/**
 * Common base class for all "Kobweb generate" tasks.
 *
 * This allows us to search for all Kobweb generate tasks by type.
 */
abstract class KobwebGenerateTask(@get:Internal protected val appBlock: AppBlock, desc: String) : KobwebTask(desc)
