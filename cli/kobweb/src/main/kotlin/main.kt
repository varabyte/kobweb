import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.enum
import com.varabyte.kobweb.cli.common.DEFAULT_BRANCH
import com.varabyte.kobweb.cli.common.DEFAULT_REPO
import com.varabyte.kobweb.cli.conf.handleConf
import com.varabyte.kobweb.cli.create.handleCreate
import com.varabyte.kobweb.cli.export.handleExport
import com.varabyte.kobweb.cli.list.handleList
import com.varabyte.kobweb.cli.run.handleRun
import com.varabyte.kobweb.cli.stop.handleStop
import com.varabyte.kobweb.cli.version.handleVersion
import com.varabyte.kobweb.server.api.ServerEnvironment
import com.varabyte.kobweb.server.api.SiteLayout

private enum class Mode {
    /** Expect a user at an ANSI-enabled terminal interacting with the command */
    INTERACTIVE,

    /** Expect the command to run in a constrained environment, e.g. a server, without user interaction */
    DUMB
}

private fun ParameterHolder.mode() = option("-m", "--mode",
    help = "(DEPRECATED, use `--[no]tty` instead) If interactive, runs in an ANSI-enabled terminal expecting user input. If dumb, use plain output only."
).enum<Mode>()

private fun ParameterHolder.layout() = option("-l", "--layout",
    help = "Specify the organizational layout of the site files.",
).enum<SiteLayout>().default(SiteLayout.KOBWEB)

private fun ParameterHolder.tty() = option("-t", "--tty",
    help = "Enable TTY support (default). Tries to run using ANSI support in an interactive mode if it can. Falls back to `--notty` otherwise."
).counted().validate { require(it <= 1) { "Cannot specify `--tty` more than once" } }

private fun ParameterHolder.notty() = option("--notty",
    help = "Explicitly disable TTY support. In this case, runs in plain mode, logging output sequentially without listening for user input, which is useful for CI environments or Docker containers.",
).counted().validate { require(it <= 1) { "Cannot specify `--notty` more than once" } }

private fun Mode.toTtyParam() = when (this) {
    Mode.INTERACTIVE -> "--tty"
    Mode.DUMB -> "--notty"
}

private fun Mode.printDeprecationWarning() {
    println("Warning: `--mode ${this.name.lowercase()}` is deprecated and will be removed in a future version. Please use `${this.toTtyParam()}` instead.")
}

/**
 * Resolve the current way to determine if we should use ANSI support.
 *
 * We currently have two approaches, the current way and the legacy way, to determine if we should try running with ANSI
 * support. This helper function resolves them, preferring the current way if present, or falling back to the legacy way
 * with a warning otherwise. If neither is present, we default to true.
 *
 * Note: tty and notty are ints, as they are treated as counting flags so we can differentiate if they are set or not.
 * Even though we expect them to be only set once or never.
 */
@Suppress("NAME_SHADOWING")
private fun shouldUseAnsi(tty: Int, notty: Int, mode: Mode?): Boolean {
    val tty = true.takeIf { tty > 0 }
    val notty = true.takeIf { notty > 0 }

    if (tty != null && notty != null) {
        throw UsageError("Both `--tty` and `--notty` are specified simultaneously.")
    }
    if ((tty != null || notty != null) && mode != null) {
        println("Warning: Both `--mode` and `--[no]tty` are specified. Ignoring `--mode`.")
    }
    return tty
        ?: notty?.not()
        ?: @Suppress("NAME_SHADOWING") mode?.let { mode ->
            mode.printDeprecationWarning()
            mode == Mode.INTERACTIVE
        }
        ?: true
}

fun main(args: Array<String>) {
    class Kobweb : NoOpCliktCommand() {
        init {
            context { helpFormatter = CliktHelpFormatter(showDefaultValues = true) }
        }

    }

    class Version : CliktCommand(help = "Print the version of this binary") {
        override fun run() {
            handleVersion()
        }
    }

    class List : CliktCommand(help = "List all project templates") {
        val repo by option(help = "The repository that hosts Kobweb templates").default(DEFAULT_REPO)
        val branch by option(help = "The branch in the repository to use").default(DEFAULT_BRANCH)

        override fun run() {
            handleList(repo, branch)
        }
    }

    class Create : CliktCommand("Create a Kobweb app / site from a template") {
        val template by argument(help = "The name of the template to instantiate, e.g. 'app'. If not specified, choices will be presented.").optional()
        val repo by option(help = "The repository that hosts Kobweb templates").default(DEFAULT_REPO)
        val branch by option(help = "The branch in the repository to use").default(DEFAULT_BRANCH)

        override fun run() {
            handleCreate(repo, branch, template)
        }
    }

    class Export : CliktCommand("Generate a static version of a Kobweb app / site") {
        val tty by tty()
        val notty by notty()
        val mode by mode()
        val layout by layout()

        override fun run() {
            handleExport(layout, shouldUseAnsi(tty, notty, mode))
        }
    }

    class Run : CliktCommand("Run a Kobweb server") {
        val env by option(help = "Whether the server should run in development mode or production.").enum<ServerEnvironment>().default(ServerEnvironment.DEV)
        val tty by tty()
        val notty by notty()
        val mode by mode()
        val layout by layout()

        override fun run() {
            handleRun(env, layout, shouldUseAnsi(tty, notty, mode))
        }
    }

    class Stop : CliktCommand("Stop a Kobweb server if one is running") {
        val tty by tty()
        val notty by notty()
        val mode by mode()

        override fun run() {
            handleStop(shouldUseAnsi(tty, notty, mode))
        }
    }

    class Conf : CliktCommand("Query a value from the .kobweb/conf.yaml file (e.g. \"server.port\")") {
        val query by argument(help = "The query to search the .kobweb/conf.yaml for (e.g. \"server.port\")")

        override fun run() {
            handleConf(query)
        }
    }

    // Special-case handling for `kobweb -v` and `kobweb --version`, which are special-cased since it's a format that
    // is expected for many tools.
    if (args.size == 1 && (args[0] == "-v" || args[0] == "--version")) {
        handleVersion()
        return
    }

    Kobweb()
        .subcommands(Version(), List(), Create(), Export(), Run(), Stop(), Conf())
        .main(args)
}