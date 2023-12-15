package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.event.EventDispatcher
import com.varabyte.kobweb.api.http.EMPTY_BODY
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.api.stream.ApiStream
import com.varabyte.kobweb.api.stream.Stream
import com.varabyte.kobweb.api.stream.StreamClientId
import com.varabyte.kobweb.api.stream.StreamEvent
import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.project.conf.Site
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.io.ApiJarFile
import com.varabyte.kobweb.streams.StreamMessage
import com.varabyte.kobweb.streams.StreamMessage.Payload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.time.toJavaDuration
import java.lang.StackTraceElement as JavaStackTraceElement // Needed to disambiguiate from ktor `StackTraceElement`

/** Somewhat uniqueish parameter key name so it's unlikely to clash with anything a user would choose by chance. */
private const val KOBWEB_PARAMS = "kobweb-params"

// A version of `stackTraceToString` that stops including traces once it hits a certain condition. This is a good way
// to filter out traces that are not relevant to the user.
private fun Throwable.stackTraceToString(includeUntil: (JavaStackTraceElement) -> Boolean): String {
    return buildString {
        var currThrowable: Throwable? = this@stackTraceToString
        var lastThrowable: Throwable? = null
        while (currThrowable != null) {
            if (lastThrowable != null) append("caused by: ")
            appendLine(currThrowable.toString())

            // If we're handling a "caused by" stack trace, make sure the first stack trace doesn't
            // get repeated in it.
            val lastThrowableFirstStackTrace = lastThrowable?.stackTrace?.firstOrNull()?.toString()
            currThrowable.stackTrace.takeWhile {
                !includeUntil(it)
                    && (lastThrowableFirstStackTrace == null || it.toString() != lastThrowableFirstStackTrace)
            }.forEach {
                appendLine("\tat $it")
            }

            lastThrowable = currThrowable
            currThrowable = currThrowable.cause
        }
    }
}

fun Application.configureRouting(
    env: ServerEnvironment,
    siteLayout: SiteLayout,
    conf: KobwebConf,
    globals: ServerGlobals,
    events: EventDispatcher
) {
    // "example/" should resolve to "example/index.html" if present, but default ktor behavior rejects trailing slashes.
    this.install(IgnoreTrailingSlash)

    val logger = object : Logger {
        override fun trace(message: String) = log.trace(message)
        override fun debug(message: String) = log.debug(message)
        override fun info(message: String) = log.info(message)
        override fun warn(message: String) = log.warn(message)
        override fun error(message: String) = log.error(message)
    }

    if (siteLayout == SiteLayout.STATIC && env != ServerEnvironment.PROD) {
        log.warn(
            """
            Static site layout is configured for a development server.

            This isn't expected, as development servers expect to read their values from the user's project. Static
            layouts are really only designed to be used in production. The server will still run in static mode as
            requested, but live-reloading, server APIs, etc. will not work with this configuration.
        """.trimIndent()
        )
    }

    when (siteLayout) {
        SiteLayout.KOBWEB -> {
            when (env) {
                ServerEnvironment.DEV -> configureDevRouting(env, conf, globals, events, logger)
                ServerEnvironment.PROD -> configureProdRouting(env, conf, events, logger)
            }
        }

        SiteLayout.STATIC -> configureStaticRouting(conf)
    }
}

val Site.routePrefixNormalized: String
    get() {
        // While the URL externally may have a prefix, internally they do not. In other words, if this site has the
        // prefix "a/b" and the user visits "a/b/nested/page", that means the local file we're going to serve is
        // "nested/page.html"
        // We remove any slashes here as it results in cleaner code as most routing code adds the slashes explicitly anyway
        return routePrefix.removePrefix("/").removeSuffix("/")
    }

private suspend fun PipelineContext<Unit, ApplicationCall>.handleApiCall(
    env: ServerEnvironment,
    apiJar: ApiJarFile,
    httpMethod: HttpMethod,
    logger: Logger,
) {
    call.parameters.getAll(KOBWEB_PARAMS)?.joinToString("/")?.let { pathStr ->
        val body: ByteArray? = when (httpMethod) {
            HttpMethod.PATCH, HttpMethod.POST, HttpMethod.PUT -> {
                withContext(Dispatchers.IO) { call.receiveStream().readAllBytes() }.takeIf { it.isNotEmpty() }
            }

            else -> null
        }
        val bodyContentType = if (body != null) call.request.contentType().toString() else null

        val query = call.request.queryParameters
            .flattenEntries()
            .toMap()

        val headers = call.request.headers.entries().associate { it.key to it.value }
        val request = Request(httpMethod, query, headers, body, bodyContentType)
        try {
            val response = apiJar.apis.handle("/$pathStr", request)
            if (response != null) {
                call.respondBytes(
                    response.body.takeIf { httpMethod != HttpMethod.HEAD } ?: EMPTY_BODY,
                    status = HttpStatusCode.fromValue(response.status),
                    contentType = response.contentType?.takeIf { httpMethod != HttpMethod.HEAD }
                        ?.let { ContentType.parse(it) }
                )
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        } catch (t: Throwable) {
            val fullErrorString = t.stackTraceToString()
            logger.error(fullErrorString)
            when {
                // Show the stack trace of the user's code but no need to share anything outside of that.
                // The user can't do anything with the extra information anyway, and this keeps the message
                // so much shorter.
                // Note: We use "startsWith" and not "equals" below because the full classname is an
                // anonymous inner class, something like "ApisFactoryImpl$create$2"
                env == ServerEnvironment.DEV && t.stackTrace.any { it.className.startsWith("ApisFactoryImpl") } -> {
                    call.respondText(
                        t.stackTraceToString(includeUntil = { it.className.startsWith("ApisFactoryImpl") }),
                        status = HttpStatusCode.InternalServerError,
                        contentType = ContentType.Text.Plain,
                    )
                }

                else -> call.respondBytes(EMPTY_BODY, status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

private class WebSocketSessionStreamData(val clientId: StreamClientId) {
    val streams = mutableSetOf<String>()
}

private class StreamImpl(
    val sessions: Map<WebSocketSession, WebSocketSessionStreamData>,
    val session: WebSocketSession,
    val apiJar: ApiJarFile,
    val route: String,
    val id: StreamClientId
) : Stream {
    private suspend fun WebSocketSession.sendMessage(message: StreamMessage<Payload.Server>) {
        send(Json.encodeToString(message))
    }

    override suspend fun send(text: String) {
        session.sendMessage(StreamMessage.text(route, text))
    }

    override suspend fun broadcast(text: String, filter: (StreamClientId) -> Boolean) {
        val message = StreamMessage.text(route, text)
        sessions.entries.forEach { (currSession, currStreamData) ->
            // A user might have connected for a different stream channel, so don't waste
            // bandwidth sending them a message they don't care about.
            if (currStreamData.streams.contains(route)) {
                if (filter(currStreamData.clientId)) {
                    currSession.sendMessage(message)
                }
            }
        }
    }

    override suspend fun disconnect() {
        apiJar.apis.handle(route, StreamEvent.ClientDisconnected(this, id))
        val streams = sessions[session]!!.streams
        streams.remove(route)
        if (streams.isEmpty()) {
            session.close()
        }
    }
}


private fun Routing.setupStreaming(
    env: ServerEnvironment,
    application: Application,
    conf: KobwebConf,
    apiJar: ApiJarFile,
    logger: Logger,
) {
    logger.info("Initializing Kobweb streams.")

    application.install(WebSockets) {
        pingPeriod = conf.server.streaming.pingPeriod.toJavaDuration()
        timeout = conf.server.streaming.timeout.toJavaDuration()
    }

    val sessions = Collections.synchronizedMap(mutableMapOf<WebSocketSession, WebSocketSessionStreamData>())
    webSocket("/api/kobweb-streams") {
        val id = StreamClientId.next()
        val session = this
        val streamData = WebSocketSessionStreamData(id)
        sessions[session] = streamData
        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val incomingMessage = Json.decodeFromString<StreamMessage<Payload.Client>>(frame.readText())
                    val streamImpl = StreamImpl(sessions, session, apiJar, incomingMessage.route, id)

                    try {
                        when (val payload = incomingMessage.payload) {
                            Payload.Client.Connect -> {
                                sessions[session]!!.streams.add(incomingMessage.route)
                                apiJar.apis.handle(
                                    incomingMessage.route,
                                    StreamEvent.ClientConnected(streamImpl, id)
                                )
                            }

                            Payload.Client.Disconnect -> streamImpl.disconnect()
                            is Payload.Text -> {
                                apiJar.apis.handle(
                                    incomingMessage.route,
                                    StreamEvent.Text(streamImpl, id, payload.text)
                                )
                            }
                        }
                    } catch (t: Throwable) {
                        logger.error(
                            """
                            |API stream ("${incomingMessage.route}", clientId=${id}) crashed
                            |payload: "${Json.encodeToString(incomingMessage.payload)}"
                            |${t.stackTraceToString()}
                            """.trimMargin()
                        )

                        // API streams can be created as objects or via `ApiStream` helper method. The
                        // `includeUntil` block includes filtering logic for both cases.
                        val callstack =
                            if (env == ServerEnvironment.DEV) {
                                t.stackTraceToString(includeUntil = {
                                    it.className == Apis::class.qualifiedName ||
                                        it.className.startsWith(ApiStream::class.qualifiedName!!)
                                })
                            } else null

                        session.send(Json.encodeToString(StreamMessage.serverError(incomingMessage.route, callstack)))
                        streamImpl.disconnect()
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            logger.trace("WebSocket connection (with clientId = $id) closed: ${closeReason.await()}\n$e")
        } catch (e: Throwable) {
            logger.error("WebSocket connection (with clientId = $id) closed with an exception: ${closeReason.await()}\n$e")
        } finally {
            sessions.remove(session)?.streams?.forEach { route ->
                val streamImpl = StreamImpl(sessions, session, apiJar, route, id)
                apiJar.apis.handle(route, StreamEvent.ClientDisconnected(streamImpl, id))
            }
        }
    }
}

private fun Routing.configureApiRouting(
    env: ServerEnvironment,
    apiJar: ApiJarFile,
    routePrefix: String,
    logger: Logger
) {
    val path = "$routePrefix/api/{$KOBWEB_PARAMS...}"
    HttpMethod.entries.forEach { httpMethod ->
        when (httpMethod) {
            HttpMethod.DELETE -> delete(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.GET -> get(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.HEAD -> head(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.OPTIONS -> options(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.PATCH -> patch(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.POST -> post(path) { handleApiCall(env, apiJar, httpMethod, logger) }
            HttpMethod.PUT -> put(path) { handleApiCall(env, apiJar, httpMethod, logger) }
        }
    }
}

/**
 * Common handler used by [configureCatchAllRouting] since we have multiple route patterns which need the same handling
 */
private suspend fun PipelineContext<*, ApplicationCall>.handleCatchAllRouting(
    script: Path,
    scriptMap: Path,
    index: Path,
    pathParts: List<String>,
    extraHandler: suspend PipelineContext<*, ApplicationCall>.(String) -> Boolean
) {
    var handled = false
    val filename = pathParts.lastOrNull()

    // Add special handling for script requests, since they may live in a totally different path based on server config
    if (filename != null) {
        handled = true
        when (filename) {
            script.name -> call.respondFile(script.toFile())
            scriptMap.name -> call.respondFile(scriptMap.toFile())
            else -> handled = false
        }
    }

    if (!handled) {
        handled = extraHandler(pathParts.joinToString("/"))
    }

    if (!handled) {
        if (filename != null) {
            // Abort early on missing resources, so we don't serve giant html pages simply because someone forgot to
            // add a favicon.ico file, for example.
            val ext = File(filename).extension.takeIf { it.isNotEmpty() }
            if (ext != null && ext != "html") {
                call.respond(HttpStatusCode.NotFound)
                handled = true
            }
        }
    }

    // If unhandled at this point, we have a URL that should either generate a real page or an error page. Return
    // the main index.html which, by referencing our site's script, should have the logic which handles this.
    if (!handled) {
        call.respondFile(index.toFile())
    }
}

// Note: This should be defined LAST in the routing { ... } block and it used to handle general URLs. The site script
// itself looks at the user's current URL to figure out how to route itself, so in many cases, just returning
// "index.html" most of the time is enough for the client to figure out what to render next.
/**
 * @param script The path to the script.js file, which may be in a custom location depending on server configuration
 * @param index The path to the index.html file, which may be in a custom location depending on server configuration
 * @param extraHandler An optional handler so callers can configure additional, one-off handling.
 */
private fun Routing.configureCatchAllRouting(
    script: Path,
    index: Path,
    routePrefix: String,
    extraHandler: suspend PipelineContext<*, ApplicationCall>.(String) -> Boolean = { false }
) {
    val scriptMap = Path("$script.map")

    get("$routePrefix/{$KOBWEB_PARAMS...}") {
        val pathParts = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleCatchAllRouting(script, scriptMap, index, pathParts, extraHandler)
    }
}

private fun Path?.createApiJar(
    env: ServerEnvironment,
    logger: Logger,
    events: EventDispatcher,
    nativeLibraryMappings: Map<String, String>
): ApiJarFile? {
    when {
        this == null -> logger.info("No API jar file specified in conf.yaml. Server API routes will not be available.")
        !this.exists() -> logger.warn("API jar specified but does not exist! Please fix conf.yaml. Invalid path: \"$this\"")
        else -> {
            logger.info("API jar found and will be loaded: \"$this\"")
            return ApiJarFile(this, env, events, logger, nativeLibraryMappings)
        }
    }
    return null
}

private fun Application.configureDevRouting(
    env: ServerEnvironment,
    conf: KobwebConf,
    globals: ServerGlobals,
    events: EventDispatcher,
    logger: Logger
) {
    val script = Path(conf.server.files.dev.script)
    val contentRoot = Path(conf.server.files.dev.contentRoot)
    val apiJar = conf.server.files.dev.api
        ?.let { Path(it) }
        .createApiJar(env, logger, events, conf.server.nativeLibraries.associate { it.name to it.path })

    routing {
        // Set up SSE (server-sent events) for the client to hear about the state of our server
        get("/api/kobweb-status") {
            logger.debug("Client connected and is requesting kobweb status events.")

            call.response.cacheControl(CacheControl.NoCache(null))
            try {
                call.respondTextWriter(contentType = ContentType.Text.EventStream) {
                    // If we don't swallow exceptions, sometimes the server freaks out when things are shutting down
                    val swallowExceptionHandler = CoroutineExceptionHandler { _, _ -> }
                    withContext(Dispatchers.IO + swallowExceptionHandler) {
                        var lastVersion: Int? = null
                        var lastStatus: String? = null
                        while (true) {
                            write(": keepalive\n")
                            write("\n")

                            if (lastVersion != globals.version) {
                                lastVersion = globals.version
                                write("event: version\n")
                                write("data: $lastVersion\n")
                                write("\n")
                            }

                            if (lastStatus != globals.status) {
                                lastStatus = globals.status
                                val statusData = mapOf(
                                    "text" to globals.status.orEmpty(),
                                    "isError" to globals.isStatusError.toString(),
                                )
                                write("event: status\n")
                                write("data: ${Json.encodeToString(statusData)}\n")
                                write("\n")
                            }

                            flush()
                            delay(300)
                        }
                    }
                }
            } catch (t: Throwable) {
                logger.debug("Stopped sending kobweb status events, probably because client disconnected or server is shutting down. (${t::class.simpleName}: ${t.message})")
            }
        }
        val routePrefix = conf.site.routePrefixNormalized

        if (apiJar != null) {
            configureApiRouting(ServerEnvironment.DEV, apiJar, routePrefix, logger)
            setupStreaming(ServerEnvironment.DEV, this@configureDevRouting, conf, apiJar, logger)
        }

        val contentRootFile = contentRoot.toFile()
        configureCatchAllRouting(script, contentRoot.resolve("index.html"), routePrefix) { path ->
            contentRootFile.resolve(path).let { contentFile ->
                if (contentFile.isFile && contentFile.exists()) {
                    call.respondFile(contentFile)
                    true
                } else {
                    false
                }
            }
        }
    }
}

private fun Application.configureProdRouting(
    env: ServerEnvironment,
    conf: KobwebConf,
    events: EventDispatcher,
    logger: Logger
) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)
    if (!siteRoot.exists()) {
        throw KobwebException("No site folder found. Did you run `kobweb export`?")
    }

    val systemRoot = siteRoot.resolve("system")
    val resourcesRoot = siteRoot.resolve("resources")
    val pagesRoot = siteRoot.resolve("pages")

    if (!systemRoot.exists()) {
        throw KobwebException("No site subfolders found. If you ran `kobweb export --layout static`, you should run `kobweb run --env prod --layout static` instead.")
    }

    val script = systemRoot.resolve(
        conf.server.files.prod.script.substringAfterLast("/")
    )
    val fallbackIndex = systemRoot.resolve("index.html")
    val apiJar = conf.server.files.dev.api
        ?.substringAfterLast("/")
        ?.let { systemRoot.resolve(it) }
        .createApiJar(env, logger, events, conf.server.nativeLibraries.associate { it.name to it.path })

    routing {
        val routePrefix = conf.site.routePrefixNormalized

        if (apiJar != null) {
            configureApiRouting(ServerEnvironment.PROD, apiJar, routePrefix, logger)
            // Since prod doesn't have live reloading, we can avoid setting up streaming if there are no API streams
            // declared at this point.
            if (apiJar.apis.numApiStreams > 0) {
                setupStreaming(ServerEnvironment.PROD, this@configureProdRouting, conf, apiJar, logger)
            }
        }

        resourcesRoot.toFile().let { resourcesRootFile ->
            resourcesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                get("$routePrefix/${file.relativeTo(resourcesRootFile).invariantSeparatorsPath}") {
                    call.respondFile(file)
                }
            }
        }
        pagesRoot.toFile().let { pagesRootFile ->
            pagesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val relativeFile = file.relativeTo(pagesRootFile)
                val name = relativeFile.nameWithoutExtension
                val parent = relativeFile.parentFile?.let { "${it.invariantSeparatorsPath}/" } ?: ""

                get(if (name != "index") "$routePrefix/$parent$name" else "$routePrefix/$parent") {
                    call.respondFile(file)
                }
            }
        }

        configureCatchAllRouting(script, fallbackIndex, routePrefix)
    }
}

/**
 * Run a Kobweb server as a dumb, static server.
 *
 * This is kind of a waste of a Kobweb server, since it has all the smarts removed, but at the same time, it's supported
 * so a user can test-run the static site experience which will ultimately be provided by some external provider.
 */
private fun Application.configureStaticRouting(conf: KobwebConf) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)

    routing {
        staticFiles(conf.site.routePrefixNormalized, siteRoot.toFile()) {
            extensions("html")
            default("404.html")
        }
    }
}
