import com.varabyte.kobweb.cli.create.runCreateFlow
import kotlinx.cli.*

enum class RunEnvironment {
    DEV,
    PROD,
}

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("kobweb")

    class Create : Subcommand("create", "Create a Kobweb app / site") {
        val template by argument(ArgType.String, "template", "The name of the template to start from, e.g. 'site'")

        override fun execute() {
            runCreateFlow(template)
        }
    }

    class Run : Subcommand("run", "Run a Kobweb server") {
        val env by option(ArgType.Choice<RunEnvironment>(), "env").default(RunEnvironment.DEV)

        override fun execute() {
            // TODO: Implement
            println("User asked to run a webserver. Environment: $env")
        }
    }

    parser.subcommands(Create(), Run())
    parser.parse(args.takeIf { it.isNotEmpty() } ?: arrayOf("-h"))
}