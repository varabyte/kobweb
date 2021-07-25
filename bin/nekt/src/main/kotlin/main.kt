import kotlinx.cli.*

enum class RunEnvironment {
    DEV,
    PROD,
}

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("nekt")

    class Create : Subcommand("create", "Create a Nekt site") {
        val withMarkdown by option(ArgType.Boolean, "with-markdown").default(false)

        override fun execute() {
            // TODO: Implement
            println("User asked to create a site. With markdown? $withMarkdown")
        }
    }

    class Run : Subcommand("run", "Run a Nekt webserver") {
        val env by option(ArgType.Choice<RunEnvironment>(), "env").default(RunEnvironment.DEV)

        override fun execute() {
            // TODO: Implement
            println("User asked to run a webserver. Environment: $env")
        }
    }

    parser.subcommands(Create(), Run())
    parser.parse(args.takeIf { it.isNotEmpty() } ?: arrayOf("-h"))
}