package com.varabyte.kobweb.gradle.core.project.frontend

import com.varabyte.kobweb.gradle.core.project.common.KOBWEB_CORE_FQN_PREFIX
import kotlinx.serialization.Serializable

const val PAGE_SIMPLE_NAME = "Page"
const val PAGE_FQN = "$KOBWEB_CORE_FQN_PREFIX$PAGE_SIMPLE_NAME"

/**
 * Information about a method in the user's code targeted by an `@Page` annotation.
 *
 * @param fqn The fully qualified name of the method
 * @param route The associated route that should be generated for this page method, e.g. "/example/path". The final
 *   value is usually decided by the current file name but could be influenced by arguments in the `@Page` annotation
 *   as well.
 */
@Serializable
class PageEntry(
    val fqn: String,
    val route: String,
)