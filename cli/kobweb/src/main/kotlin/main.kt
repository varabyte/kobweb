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

private fun ArgParser.headlessOption() = option(
    ArgType.Boolean,
    fullName = "headless",
    description = "If true, allow user interaction / use colors and animations. Else, restrict to simple console logging",
).default(false)

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
        val isHeadless by headlessOption()

        override fun execute() {
            handleExport(!isHeadless)
        }
    }

    class Run : Subcommand("run", "Run a Kobweb server") {
        val env by option(ArgType.Choice<ServerEnvironment>(), "env").default(ServerEnvironment.DEV)
        val isHeadless by headlessOption()

        override fun execute() {
            handleRun(env, !isHeadless)
        }
    }

    class Stop : Subcommand("stop", "Stop a Kobweb server if one is running") {
        override fun execute() {
            handleStop()
        }
    }

    parser.subcommands(Version(), List(), Create(), Export(), Run(), Stop())
    parser.parse(args.takeIf { it.isNotEmpty() } ?: arrayOf("-h"))
}