package com.varabyte.kobweb.api.intercept

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.data.Data
import com.varabyte.kobweb.api.env.Environment
import com.varabyte.kobweb.api.http.MutableRequest
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import com.varabyte.kobweb.api.log.Logger

/**
 * A context for a method annotated with [ApiInterceptor].
 *
 * The classes can be used to help the user write interception logic, dispatching the request either to its original
 * endpoint, an alternate one, or simply returning a custom response instead entirely. See [ApiInterceptor] for more
 * information.
 *
 * @property env The current server environment, in case you need to branch logic in development vs production
 *   environments.
 * @property path The path of the API endpoint being requested, including a leading slash.
 * @property req Request information sent from the client. This instance of the request is mutable, meaning some fields
 *   (headers, cookies, body, and content-type) can still be changed.
 * @property data Readonly data store potentially populated by methods annotated with [InitApi].
 *   See also: [InitApiContext].
 * @property logger A logger which can be used to log messages into the log files.
 */
class ApiInterceptorContext(
    val env: Environment,
    val dispatcher: Apis.Dispatcher,
    val path: String,
    val req: MutableRequest,
    val data: Data,
    val logger: Logger,
)