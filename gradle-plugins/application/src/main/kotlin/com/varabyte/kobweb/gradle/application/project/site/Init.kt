package com.varabyte.kobweb.gradle.application.project.site

import com.varabyte.kobweb.gradle.application.project.KOBWEB_CORE_FQN_PREFIX

const val INIT_SIMPLE_NAME = "Init"
const val INIT_FQN = "$KOBWEB_CORE_FQN_PREFIX${com.varabyte.kobweb.gradle.application.project.api.INIT_SIMPLE_NAME}"

/**
 * Information about a method in the user's code targeted by an `@Init` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param acceptsContext If true, the method accepts a single `InitContext` argument; otherwise, no arguments.
 */
class InitEntry(
    val fqn: String,
    val acceptsContext: Boolean,
)