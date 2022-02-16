package com.varabyte.kobweb.gradle.application.project.site

import com.varabyte.kobweb.gradle.application.project.common.KOBWEB_CORE_FQN_PREFIX
import com.varabyte.kobweb.gradle.application.project.common.KOBWEB_SILK_FQN_PREFIX

const val INIT_KOBWEB_SIMPLE_NAME = "InitKobweb"
const val INIT_KOBWEB_FQN = "$KOBWEB_CORE_FQN_PREFIX$INIT_KOBWEB_SIMPLE_NAME"

const val INIT_SILK_SIMPLE_NAME = "InitSilk"
const val INIT_SILK_FQN = "$KOBWEB_SILK_FQN_PREFIX$INIT_SILK_SIMPLE_NAME"

class InitSilkEntry(
    val fqn: String,
)


/**
 * Information about a method in the user's code targeted by an `@InitKobweb` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param acceptsContext If true, the method accepts a single `InitKobwebContext` argument; otherwise, no arguments.
 */
class InitKobwebEntry(
    val fqn: String,
    val acceptsContext: Boolean,
)