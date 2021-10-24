package com.varabyte.kobweb.api

import com.varabyte.kobweb.api.data.MutableData
import com.varabyte.kobweb.api.log.Logger

/**
 * Various classes useful for methods that are called when a server is started.
 *
 * @param apis The parent [Apis] object, in case this method wants to register any additional API routes dynamically.
 *   This will be called AFTER methods annotated with [Api] have already been registered.
 */
class InitContext(val apis: Apis, val data: MutableData, val logger: Logger)