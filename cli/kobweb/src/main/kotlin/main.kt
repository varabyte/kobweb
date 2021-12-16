import com.varabyte.kobweb.cli.common.DEFAULT_BRANCH
import com.varabyte.kobweb.cli.common.DEFAULT_REPO
import com.varabyte.kobweb.cli.create.handleCreate
import com.varabyte.kobweb.cli.export.handleExport
import com.varabyte.kobweb.cli.list.handleList
import com.varabyte.kobweb.cli.run.handleRun
import com.varabyte.kobweb.cli.stop.handleStop
import com.varabyte.kobweb.cli.version.handleVersion
import com.varabyte.kobweb.server.api.ServerEnvironment
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default

private enum class Mode {
    /** Expect a user at an ANSI-enabled terminal interacting with the command */
    INTERACTIVE,

    /** Expect the command to run in a constrained environment, e.g. a server, without user interaction */
    DUMB
}

private fun ArgParser.mode() = option(
    ArgType.Choice<Mode>(),
    fullName = "mode",
    shortName = "m",
    description = "If interactive, runs in an ANSI-enabled terminal expecting user input. If dumb, command only outputs, using simple console logging",
).default(Mode.INTERACTIVE)

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("kobweb")

    class Version : Subcommand("version", "Print the version of this binary") {
        override fun execute() {
            handleVersion()
        }
    }

    class List : Subcommand("list", "List all project templates") {
        val repo by option(ArgType.String, "repo", "The repository that hosts Kobweb templates")
            .default(DEFAULT_REPO)
        val branch by option(ArgType.String, "branch", "The branch in the repository to use")
            .default(DEFAULT_BRANCH)

        override fun execute() {
            handleList(repo, branch)
        }
    }

    class Create : Subcommand("create", "Create a Kobweb app / site") {
        val template by argument(ArgType.String, "template", "The name of the template to start from, e.g. 'site'")
        val repo by option(ArgType.String, "repo", "The repository that hosts Kobweb templates")
            .default(DEFAULT_REPO)
        val branch by option(ArgType.String, "branch", "The branch in the repository to use")
            .default(DEFAULT_BRANCH)

        override fun execute() {
            handleCreate(repo, branch, template)
        }
    }

    class Export : Subcommand("export", "Generate a static version of a Kobweb app / site") {
        val mode by mode()

        override fun execute() {
            handleExport(mode == Mode.INTERACTIVE)
        }
    }

    class Run : Subcommand("run", "Run a Kobweb server") {
        val env by option(ArgType.Choice<ServerEnvironment>(), "env").default(ServerEnvironment.DEV)
        val mode by mode()

        override fun execute() {
            handleRun(env, mode == Mode.INTERACTIVE)
        }
    }

    class Stop : Subcommand("stop", "Stop a Kobweb server if one is running") {
        val mode by mode()

        override fun execute() {
            // TODO(#79): Delete this when fixed
            if (mode == Mode.INTERACTIVE) {
                println("Interactive mode is not yet implemented for this command, so it will run in dumb mode for now.")
                println("There won't be any functional difference in this case, just visual.")
                println("Follow https://github.com/varabyte/kobweb/issues/79 if you want to be notified when it is fixed")
                println()
            }
            handleStop()
        }
    }

    parser.subcommands(Version(), List(), Create(), Export(), Run(), Stop())
    parser.parse(args.takeIf { it.isNotEmpty() } ?: arrayOf("-h"))
}