package com.varabyte.kobweb.api

import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.log.Logger

/**
 * A container for a bunch of relevant utility classes that may be needed when responding to an API call.
 *
 * The classes can be used to query the current state of the API call as well as respond to it.
 *
 * @param req Request information sent from the client.
 * @param res Response information that will be returned to the client.
 */
class ApiContext(
    val req: Request,
    val data: Data,
    val logger: Logger,
) {
    val res = Response()
}