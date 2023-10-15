package com.varabyte.kobweb.api.init

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.data.MutableData
import com.varabyte.kobweb.api.event.Events
import com.varabyte.kobweb.api.log.Logger

/**
 * A container for a bunch of relevant utility classes that may be useful when starting up a server.
 *
 * @property apis The parent [Apis] object, in case this method wants to register any additional API routes dynamically.
 *   This will be called AFTER methods annotated with [Api] have already been registered.
 * @property data A data store which can optionally be used for storing rich data values that can then be read by
 *   `@Api` methods.
 * @property events An interface to register custom events handlers that will be called during the lifetime of the server.
 */
class InitApiContext(val apis: Apis, val data: MutableData, val events: Events, val logger: Logger)