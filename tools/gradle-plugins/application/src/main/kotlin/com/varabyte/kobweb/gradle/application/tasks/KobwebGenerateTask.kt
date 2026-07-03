package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.core.tasks.KobwebTask
import org.gradle.work.DisableCachingByDefault

/**
 * Common base class for all "Kobweb generate" tasks.
 *
 * This allows us to search for all Kobweb generate tasks by type.
 */
@DisableCachingByDefault(because = "Base task; up to children to decide caching strategy for themselves.")
abstract class KobwebGenerateTask(desc: String) : KobwebTask(desc)
