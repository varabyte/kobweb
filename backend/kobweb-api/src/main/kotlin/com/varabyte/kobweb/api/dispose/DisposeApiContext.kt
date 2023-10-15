package com.varabyte.kobweb.api.dispose

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.log.Logger

/**
 * A container for a bunch of relevant utility classes that may be useful when disposing the api in case of server reload or shutdown.
 *
 * @property data The data store which was filled by the [InitApi] methods.
 * @property logger A [Logger] instance
 */
class DisposeApiContext(
    val data: Data,
    val logger: Logger,
)
