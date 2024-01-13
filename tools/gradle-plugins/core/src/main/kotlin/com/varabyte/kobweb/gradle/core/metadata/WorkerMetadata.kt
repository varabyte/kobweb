package com.varabyte.kobweb.gradle.core.metadata

import kotlinx.serialization.Serializable

/**
 * Worker-specific metadata
 *
 * Its existence in a jar's metadata also identifies the jar as a Kobweb worker.
 */
@Serializable
class WorkerMetadata // Currently nothing, but just having a worker.json file identifies this artifact as a worker
