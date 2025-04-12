package com.varabyte.kobweb.server.plugins

import com.varabyte.kobweb.api.Apis
import com.varabyte.kobweb.api.event.EventDispatcher
import com.varabyte.kobweb.api.http.EMPTY_BODY
import com.varabyte.kobweb.api.http.HttpMethod
import com.varabyte.kobweb.api.http.MutableRequest
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.log.Logger
import com.varabyte.kobweb.api.stream.ApiStream
import com.varabyte.kobweb.api.stream.Stream
import com.varabyte.kobweb.api.stream.StreamEvent
import com.varabyte.kobweb.api.stream.StreamId
import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.common.text.prefixIfNot
import com.varabyte.kobweb.project.conf.KobwebConf
import com.varabyte.kobweb.project.conf.Server.Redirect
import com.varabyte.kobweb.project.conf.Site
import com.varabyte.kobweb.server.ServerGlobals
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout
import com.varabyte.kobweb.server.io.ApiJarFile
import com.varabyte.kobweb.streams.StreamMessage
import com.varabyte.kobweb.streams.StreamMessage.Payload
import com.varabyte.kobweb.util.text.PatternMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import java.lang.StackTraceElement as JavaStackTraceElement // Needed to disambiguate from ktor `StackTraceElement`

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
    val logger = object : Logger {
        override fun trace(message: String) = log.trace(message)
        override fun debug(message: String) = log.debug(message)
        override fun info(message: String) = log.info(message)
        override fun warn(message: String) = log.warn(message)
        override fun error(message: String) = log.error(message)
    }

    when {
        siteLayout.isFullstack -> {
            when (env) {
                ServerEnvironment.DEV -> configureFullstackDevRouting(conf, globals, events, logger)
                ServerEnvironment.PROD -> configureFullstackProdRouting(conf, events, logger)
            }
        }

        else -> {
            check(siteLayout.isStatic)
            when (env) {
                ServerEnvironment.DEV -> configureStaticDevRouting(conf, globals, logger)
                ServerEnvironment.PROD -> configureStaticProdRouting(conf)
            }
        }
    }
}

val Site.basePathNormalized: String
    get() {
        // While the URL externally may have a prefix, internally they do not. In other words, if this site has the
        // prefix "a/b" and the user visits "a/b/nested/page", that means the local file we're going to serve is
        // "nested/page.html"
        // We remove any slashes here as it results in cleaner code as most routing code adds the slashes explicitly anyway
        return basePathOrRoutePrefix.removePrefix("/").removeSuffix("/")
    }

private fun RequestConnectionPoint.toRequestConnectionDetails() = Request.Connection.Details(
    scheme = scheme,
    version = version,
    localAddress = localAddress,
    localHost = localHost,
    localPort = localPort,
    remoteAddress = remoteAddress,
    remoteHost = remoteHost,
    remotePort = remotePort,
    serverHost = serverHost,
    serverPort = serverPort,
)

private suspend fun RoutingContext.handleApiCall(
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
        val request = MutableRequest(
            Request.Connection(
                origin = call.request.origin.toRequestConnectionDetails(),
                local = call.request.local.toRequestConnectionDetails(),
            ),
            httpMethod,
            query,
            query,
            headers,
            call.request.cookies.rawCookies,
            body,
            bodyContentType
        )
        try {
            val response = apiJar.apis.handle("/$pathStr", request)
            response.headers.forEach { (key, value) ->
                call.response.headers.append(key, value)
            }
            call.respondBytes(
                response.body.takeIf { httpMethod != HttpMethod.HEAD } ?: EMPTY_BODY,
                status = HttpStatusCode.fromValue(response.status),
                contentType = response.contentType?.takeIf { httpMethod != HttpMethod.HEAD }
                    ?.let { ContentType.parse(it) }
            )
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

private class WebSocketSessionData {
    class StreamData(val route: String)
    val streamEntries = mutableMapOf<StreamId, StreamData>()
}

private class StreamImpl(
    val sessions: Map<WebSocketSession, WebSocketSessionData>,
    val session: WebSocketSession,
    val apiJar: ApiJarFile,
    override val id: StreamId,
) : Stream {
    private suspend fun WebSocketSession.sendMessage(message: StreamMessage<Payload.Server>) {
        send(Json.encodeToString(message))
    }

    override suspend fun send(text: String) {
        session.sendMessage(StreamMessage.text(id.localStreamId, text))
    }

    override suspend fun broadcast(text: String, filter: (StreamId) -> Boolean) {
        sessions.entries.forEach { (currSession, currStreamData) ->
            val route = sessions.getValue(session).streamEntries.getValue(id).route
            // Users probably think that each ApiStream is a totally separate websocket, but in reality, we use one
            // websocket for all of them (helps us with live reloading and minimizes connection resources). However, it
            // means that here we must skip streams that are attached to a different route endpoint.
            currStreamData.streamEntries.forEach { (streamId, streamData) ->
                if (route == streamData.route && filter(streamId)) {
                    val message = StreamMessage.text(streamId.localStreamId, text)
                    currSession.sendMessage(message)
                }
            }
        }
    }

    override suspend fun disconnect() {
        val sessionData = sessions.getValue(session)
        val route = sessionData.streamEntries.remove(id)!!.route
        apiJar.apis.handle(route, StreamEvent.ClientDisconnected(this))
        if (sessionData.streamEntries.isEmpty()) {
            session.close()
        }
    }
}

/**
 * A thread-safe class which you can use to generate a unique Short ID that wraps after reaching [Short.MAX_VALUE].
 */
private class ShortIdGenerator {
    private val nextId = AtomicInteger(0)
    fun next() = (nextId.getAndIncrement() % Short.MAX_VALUE).toShort()
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
        pingPeriod = conf.server.streaming.pingPeriod
        timeout = conf.server.streaming.timeout
    }

    val clientIdGenerator = ShortIdGenerator()
    val sessions = Collections.synchronizedMap(mutableMapOf<WebSocketSession, WebSocketSessionData>())
    webSocket("/api/kobweb-streams") {
        // This callback is triggered once per connecting client (no matter how many streams they create)
        val clientId = clientIdGenerator.next()
        val session = this
        sessions[session] = WebSocketSessionData()
        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val incomingMessage = Json.decodeFromString<StreamMessage<Payload.Client>>(frame.readText())
                    val streamId = StreamId(clientId, incomingMessage.localStreamId)
                    val streamImpl = StreamImpl(sessions, session, apiJar, streamId)

                    try {
                        when (val payload = incomingMessage.payload) {
                            is Payload.Client.Connect -> {
                                sessions.getValue(session).apply {
                                    streamEntries[streamId] = WebSocketSessionData.StreamData(payload.route)
                                }
                                apiJar.apis.handle(
                                    payload.route,
                                    StreamEvent.ClientConnected(streamImpl)
                                )
                            }

                            Payload.Client.Disconnect -> streamImpl.disconnect()
                            is Payload.Text -> {
                                val route = sessions.getValue(session).streamEntries.getValue(streamId).route
                                apiJar.apis.handle(
                                    route,
                                    StreamEvent.Text(streamImpl, payload.text)
                                )
                            }
                        }
                    } catch (t: Throwable) {
                        // Note: Route should always be set unless somehow we crash on the Connect event, which
                        // shouldn't happen.
                        val route = sessions.getValue(session).streamEntries[streamId]?.route ?: "?"
                        logger.error(
                            """
                            |API stream ("$route", clientId=${clientId}) crashed
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

                        session.send(
                            Json.encodeToString(
                                StreamMessage.serverError(
                                    incomingMessage.localStreamId,
                                    callstack
                                )
                            )
                        )
                        streamImpl.disconnect()
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            logger.trace("WebSocket connection (with clientId = $clientId) closed: ${closeReason.await()}\n$e")
        } catch (e: Throwable) {
            logger.error("WebSocket connection (with clientId = $clientId) closed with an exception: ${closeReason.await()}\n$e")
        } finally {
            sessions.remove(session)?.let { sessionData ->
                sessionData.streamEntries.forEach { (streamId, streamData) ->
                    val streamImpl = StreamImpl(sessions, session, apiJar, streamId)
                    apiJar.apis.handle(streamData.route, StreamEvent.ClientDisconnected(streamImpl))
                }
            }
        }
    }
}

private fun Routing.configureApiRouting(
    env: ServerEnvironment,
    apiJar: ApiJarFile,
    basePath: String,
    logger: Logger
) {
    val path = "$basePath/api/{$KOBWEB_PARAMS...}"
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

private suspend fun RoutingContext.serveScriptFiles(
    path: String, script: Path, scriptMap: Path): Boolean {
    val filename = path.substringAfterLast('/').takeIf { it.isNotEmpty() } ?: return false

    when (filename) {
        script.name -> call.respondPath(script)
        scriptMap.name -> call.respondPath(scriptMap)
        else -> return false
    }
    return true
}

// Abort early on missing resources, so we don't serve giant html pages simply because someone forgot to
// add a favicon.ico file, for example.
private suspend fun RoutingContext.abortIfNotHtml(): Boolean {
    val acceptsHtml = call.request.acceptItems().any { it.value == ContentType.Text.Html.toString() }
    return if (!acceptsHtml) {
        call.respond(HttpStatusCode.NotFound)
        true
    } else false
}

// As a fallback, server the 'index.html' file if no other resource is matched by the path. The index file
// contains general logic which can figure out what to do (e.g. show the user a 404 page)
private suspend fun RoutingContext.serveIndexFile(index: Path) {
    call.respondPath(index)
}

/**
 * Common handler used by [configureCatchAllRouting] since we have multiple route patterns which need the same handling
 */
private suspend fun RoutingContext.handleCatchAllRouting(
    pathSegments: List<String>,
    vararg handlers: suspend RoutingContext.(String) -> Boolean,
) {
    val pathString = pathSegments.joinToString("/")

    for (handler in handlers) {
        if (handler(pathString)) break
    }
}

private fun List<Redirect>.toPatternMappers(): List<PatternMapper> {
    return this.map { redirect -> PatternMapper("^${redirect.from}\$", redirect.to) }
}

@Suppress("NAME_SHADOWING")
private suspend fun RoutingContext.handleRedirect(
    basePath: String,
    path: String,
    redirects: List<PatternMapper>
): Boolean {
    if (redirects.isEmpty()) return false

    val path = path.prefixIfNot("/")
    val redirectedPath = redirects.fold(path) { path, patternMapper -> patternMapper.map(path) ?: path }
    return if (redirectedPath != path) {
        call.respondRedirect("$basePath/${redirectedPath.removePrefix("/")}".prefixIfNot("/"), permanent = true)
        true
    } else false
}

private fun Routing.configureRedirects(basePath: String, redirects: List<PatternMapper>) {
    if (redirects.isEmpty()) return
    get("$basePath/{$KOBWEB_PARAMS...}") {
        val pathSegments = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleRedirect(basePath, pathSegments.joinToString("/"), redirects)
    }
}


// Note: This should be defined LAST in the routing { ... } block and used to handle general URLs. The site script
// itself looks at the user's current URL to figure out how to route itself, so in many cases, just returning
// "index.html" most of the time is enough for the client to figure out what to render next.
/**
 * @param script The path to the script.js file, which may be in a custom location depending on server configuration
 * @param index The path to the index.html file, which may be in a custom location depending on server configuration
 * @param findResource An optional handler so callers can notify this caller that a resource was found dynamically.
 */
private fun Routing.configureCatchAllRouting(
    conf: KobwebConf,
    script: Path,
    index: Path,
    basePath: String,
    findResource: (String) -> File? = { null }
) {
    val scriptMap = Path("$script.map")
    val patternMappers = conf.server.redirects.toPatternMappers()

    get("$basePath/{$KOBWEB_PARAMS...}") {
        val pathSegments = call.parameters.getAll(KOBWEB_PARAMS)!!
        handleCatchAllRouting(
            pathSegments,
            { path -> serveScriptFiles(path, script, scriptMap) },
            { path -> handleRedirect(basePath, path, patternMappers) },
            { path ->
                findResource(path).let { contentFile ->
                    if (contentFile != null) {
                        call.respondFile(contentFile)
                        true
                    } else false
                }
            },
            { _ -> abortIfNotHtml() },
            {
                serveIndexFile(index).also {
                    application.log.debug(
                        "Served fallback index.html file in response to \"/${
                            pathSegments.joinToString("/")
                        }\""
                    )
                }; true
            },
        )
    }

    head("$basePath/{$KOBWEB_PARAMS...}") {
        val path = call.parameters.getAll(KOBWEB_PARAMS)!!.joinToString("/")
        if (findResource(path) != null) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
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
        !this.exists() -> logger.warn("API jar specified but does not exist! Please fix conf.yaml, updating the path (or removing the value if you aren't declaring API endpoints). Invalid path: \"$this\"")
        else -> {
            logger.info("API jar found and will be loaded: \"$this\"")
            return ApiJarFile(this, env, events, logger, nativeLibraryMappings)
        }
    }
    return null
}

private fun Application.configureDevRouting(
    apiJar: ApiJarFile?,
    conf: KobwebConf,
    globals: ServerGlobals,
    logger: Logger
) {
    val script = Path(conf.server.files.dev.script)
    val contentRoot = Path(conf.server.files.dev.contentRoot)

    install(SSE)
    routing {
        // Set up SSE (server-sent events) for the client to hear about the state of our server
        sse("/api/kobweb-status") {
            logger.debug("Client connected and is requesting kobweb status events.")
            heartbeat()

            try {
                // If we don't swallow exceptions, sometimes the server freaks out when things are shutting down
                val swallowExceptionHandler = CoroutineExceptionHandler { _, _ -> }
                withContext(Dispatchers.IO + swallowExceptionHandler) {
                    var lastVersion: Int? = null
                    var lastStatus: String? = null
                    while (true) {
                        if (lastVersion != globals.version) {
                            lastVersion = globals.version
                            send(event = "version", data = lastVersion.toString())
                        }

                        if (lastStatus != globals.status) {
                            lastStatus = globals.status
                            val statusData = mapOf(
                                "text" to globals.status.orEmpty(),
                                "isError" to globals.isStatusError.toString(),
                            )
                            send(event = "status", data = Json.encodeToString(statusData))
                        }

                        delay(300)
                    }
                }
            } catch (t: Throwable) {
                logger.debug("Stopped sending kobweb status events, probably because client disconnected or server is shutting down. (${t::class.simpleName}: ${t.message})")
            }
        }
        val basePath = conf.site.basePathNormalized

        if (apiJar != null) {
            configureApiRouting(ServerEnvironment.DEV, apiJar, basePath, logger)
            setupStreaming(ServerEnvironment.DEV, this@configureDevRouting, conf, apiJar, logger)
        }

        val contentRootFile = contentRoot.toFile()
        configureCatchAllRouting(conf, script, contentRoot.resolve("index.html"), basePath) { path ->
            // We fetch resources dynamically in dev mode because things may get added, removed, or renamed while the
            // server is running. In prod mode, files are registered at startup time instead.
            contentRootFile.resolve(path).takeIf { it.isFile && it.exists() }
        }
    }
}

private fun Application.configureFullstackDevRouting(
    conf: KobwebConf,
    globals: ServerGlobals,
    events: EventDispatcher,
    logger: Logger
) {
    val apiJar = conf.server.files.dev.api
        ?.let { Path(it) }
        .createApiJar(
            ServerEnvironment.DEV,
            logger,
            events,
            conf.server.nativeLibraries.associate { it.name to it.path })

    configureDevRouting(apiJar, conf, globals, logger)
}

private fun Application.configureFullstackProdRouting(
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
        .createApiJar(
            ServerEnvironment.PROD,
            logger,
            events,
            conf.server.nativeLibraries.associate { it.name to it.path })

    routing {
        val basePath = conf.site.basePathNormalized

        if (apiJar != null) {
            configureApiRouting(ServerEnvironment.PROD, apiJar, basePath, logger)
            // Since prod doesn't have live reloading, we can avoid setting up streaming if there are no API streams
            // declared at this point.
            if (apiJar.apis.numApiStreams > 0) {
                setupStreaming(ServerEnvironment.PROD, this@configureFullstackProdRouting, conf, apiJar, logger)
            }
        }

        resourcesRoot.toFile().let { resourcesRootFile ->
            resourcesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val resourcePath = "$basePath/${file.relativeTo(resourcesRootFile).invariantSeparatorsPath}"
                get(resourcePath) {
                    call.respondFile(file)
                }
                head(resourcePath) {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        pagesRoot.toFile().let { pagesRootFile ->
            pagesRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val relativeFile = file.relativeTo(pagesRootFile)
                val name = relativeFile.nameWithoutExtension
                val parent = relativeFile.parentFile?.let { "${it.invariantSeparatorsPath}/" } ?: ""

                get(if (name != "index") "$basePath/$parent$name" else "$basePath/$parent") {
                    call.respondFile(file)
                }
            }
        }

        configureCatchAllRouting(conf, script, fallbackIndex, basePath)
    }
}

// A static layout server in dev mode is pretty much identical to a fullstack server without API handling.
//
// Note that a real static layout server loads files from disk in a location where they were exported, while dev mode
// fakes this experience, by loading files from the project's build directory. This means that a static server in dev
// mode may seem to work fine but might not actually work when exported. However, we still do this as it lets users
// iterate on a static layout project while failing fast if they accidentally try using API routes or API streams.
private fun Application.configureStaticDevRouting(
    conf: KobwebConf,
    globals: ServerGlobals,
    logger: Logger
) {
    configureDevRouting(null, conf, globals, logger)
}


/**
 * Run a Kobweb server as a dumb, static server.
 *
 * This is kind of a waste of a Kobweb server, since it has all the smarts removed, but at the same time, it's supported
 * so a user can test-run the static site experience which will ultimately be provided by some external provider.
 */
private fun Application.configureStaticProdRouting(conf: KobwebConf) {
    val siteRoot = Path(conf.server.files.prod.siteRoot)
    val basePath = conf.site.basePathNormalized

    routing {
        // NOTE: This used to be:
        //    staticFiles(conf.site.basePathNormalized, siteRoot.toFile()) {
        //        enableAutoHeadResponse()
        //        extensions("html")
        //        default("404.html")
        //    }
        // but we ran into a bug where if you had a folder and a file with the same name (e.g. "example.html" and
        // "example/"), the ktor implementation returns a 404. Our manual implementation avoids this error.

        siteRoot.toFile().let { siteRootFile ->
            siteRootFile.walkBottomUp().filter { it.isFile }.forEach { file ->
                val relativeFile = file.relativeTo(siteRootFile)
                val name = relativeFile.name.removeSuffix(".html")
                val parent = relativeFile.parentFile?.let { "${it.invariantSeparatorsPath}/" } ?: ""

                val path = if (name != "index") "$basePath/$parent$name" else "$basePath/$parent"
                get(path) { call.respondFile(file) }
                head(path) { call.respond(HttpStatusCode.OK) }
            }

            // Anything not found is an error
            val errorFile = siteRootFile.resolve("404.html")
            if (errorFile.exists()) {
                // Catch URLs of the form a/b/c/
                get("{...}/") { call.respondFile(errorFile) }
                // Catch URLs of the form a/b/c/slug
                get("{...}") { call.respondFile(errorFile) }
            }
        }

        configureRedirects(basePath, conf.server.redirects.toPatternMappers())
    }
}
