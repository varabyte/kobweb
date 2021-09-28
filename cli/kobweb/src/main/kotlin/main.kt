import com.varabyte.kobweb.cli.create.handleCreate
import com.varabyte.kobweb.cli.run.handleRun
import com.varabyte.kobweb.cli.version.handleVersion
import kotlinx.cli.*

enum class RunEnvironment {
    DEV,
    PROD,
}

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("kobweb")

    class Version : Subcommand("version", "Print the version of this binary") {
        override fun execute() {
            handleVersion()
        }
    }

    class Create : Subcommand("create", "Create a Kobweb app / site") {
        val template by argument(ArgType.String, "template", "The name of the template to start from, e.g. 'site'")

        override fun execute() {
            handleCreate(template)
        }
    }

    class Run : Subcommand("run", "Run a Kobweb server") {
        val env by option(ArgType.Choice<RunEnvironment>(), "env").default(RunEnvironment.DEV)

        override fun execute() {
            handleRun(env)
        }
    }

    parser.subcommands(Version(), Create(), Run())
    parser.parse(args.takeIf { it.isNotEmpty() } ?: arrayOf("-h"))
}