package com.varabyte.kobweb.gradle.application.project.site

import com.varabyte.kobweb.gradle.application.project.common.KOBWEB_CORE_FQN_PREFIX

const val APP_SIMPLE_NAME = "App"
const val APP_FQN = "$KOBWEB_CORE_FQN_PREFIX$APP_SIMPLE_NAME"

/**
 * Information about a method in the user's code targeted by an `@App` annotation.
 *
 * @param fqn The fully qualified name of the method
 */
class AppEntry(val fqn: String)