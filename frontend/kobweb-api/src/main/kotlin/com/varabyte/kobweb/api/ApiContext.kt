package com.varabyte.kobweb.api

import java.nio.file.Path

/**
 * A container for a bunch of relevant utility classes that may be needed when responding to an API call.
 *
 * The classes can be used to query the current state of the API call as well as respond to it.
 *
 * @param dataRoot The root of a folder which the server can use to store data, if necessary. Keep in mind that many
 *   servers are configured to be stateless, in that is there's no guarantee if you're hitting the same server or a
 *   brand new one with each request, but this path is still included for simpler server configurations or if you know
 *   what you're doing!
 *
 * @param req Request information sent from the client.
 */
class ApiContext(
    val dataRoot: Path,
    val req: Request,
) {
    val res = Response()
}