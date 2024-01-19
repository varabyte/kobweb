package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.varabyte.kobweb.ksp.symbol.suppresses

/**
 * A KSP processor that generates code that instantiates / wraps a Worker class related to a given `WorkerFactory`
 * implementation.
 *
 * For example, if the user defines a class called `CalculatePiWorkerFactory`, then this processor will generate a
 * `main.kt` file that instantiates it plus a `CalculatePiWorker` class that wraps it and acts as the main way a
 * Kobweb application would interact with this module.
 */
class WorkerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val outputPath: String,
    workerFqcnOverride: String? = null,
) : SymbolProcessor {
    class WorkerFactoryInfo(
        val classDeclaration: KSClassDeclaration,
        val inputTypeDeclaration: KSDeclaration,
        val outputTypeDeclaration: KSDeclaration,
    )

    private var workerFactoryInfo: WorkerFactoryInfo? = null

    // See WorkerBlock.fqcn property documentation for explicit examples for converting the fqcn value to final
    // values for class generation.
    private val classNameOverride = workerFqcnOverride?.substringAfterLast('.')?.takeIf { it.isNotBlank() }
    private val classPackageOverride = workerFqcnOverride?.substringBeforeLast('.')?.takeIf { it.isNotBlank() }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allFiles = resolver.getAllFiles()

        val visitor = WorkerFactoryVisitor()

        allFiles.forEach { file ->
            file.accept(visitor, Unit)
        }

        val workerFactory = visitor.workerStrategies.singleOrNull() ?: run {
            error(buildString {
                append("A Kobweb worker module must have exactly one class that implements `$WORKER_FACTORY_SIMPLE_NAME`. ")
                if (visitor.workerStrategies.isEmpty()) {
                    append("However, none were found.")
                } else {
                    append("However, the following were found: [")
                    append(visitor.workerStrategies.joinToString {
                        it.classDeclaration.qualifiedName!!.asString()
                    })
                    append("].")
                }
            })
        }

        if (workerFactory.classDeclaration.getConstructors()
                .none { (it.isPublic() || it.isInternal()) && (it.parameters.isEmpty() || it.parameters.all { param -> param.hasDefault }) }
        ) {
            error("A Kobweb `$WORKER_FACTORY_SIMPLE_NAME` implementation must have a public empty constructor. Please add one to `${workerFactory.classDeclaration.qualifiedName!!.asString()}`.")
        }

        if (workerFactory.classDeclaration.isPrivate()) {
            error("A Kobweb `$WORKER_FACTORY_SIMPLE_NAME` implementation cannot be private, as this prevents us from generating code that wraps it. Please make `${workerFactory.classDeclaration.qualifiedName!!.asString()}` internal.")
        }
        if (workerFactory.classDeclaration.isPublic()) {
            val publicSuppression = "PUBLIC_WORKER_FACTORY"
            if (!workerFactory.classDeclaration.suppresses(publicSuppression)) {
                logger.warn("It is recommended that you make your `$WORKER_FACTORY_SIMPLE_NAME` implementation internal to prevent Kobweb applications from using it unintentionally. Please add `internal` to `${workerFactory.classDeclaration.qualifiedName!!.asString()}`. You can annotate your class with `@Suppress(\"$publicSuppression\")` to suppress this warning.")
            }
        }

        if (!workerFactory.inputTypeDeclaration.isPublic()) {
            error("A Kobweb `$WORKER_FACTORY_SIMPLE_NAME` implementation's input type must be public so the Kobweb application can use it. Please make `${workerFactory.inputTypeDeclaration.qualifiedName!!.asString()}` public.")
        }

        if (!workerFactory.outputTypeDeclaration.isPublic()) {
            error("A Kobweb `$WORKER_FACTORY_SIMPLE_NAME` implementation's output type must be public so the Kobweb application can use it. Please make `${workerFactory.outputTypeDeclaration.qualifiedName!!.asString()}` public.")
        }

        if (classNameOverride == null && !workerFactory.classDeclaration.qualifiedName!!.asString()
                .let { it.endsWith(WORKER_FACTORY_SIMPLE_NAME) && it != WORKER_FACTORY_SIMPLE_NAME }
        ) {
            error("A Kobweb `$WORKER_FACTORY_SIMPLE_NAME` implementation's name should end with the suffix \"$WORKER_FACTORY_SIMPLE_NAME\", so we can automatically generate an associated \"Worker\" class from it. Please change the name of `${workerFactory.classDeclaration.qualifiedName!!.asString()}` to meet this requirement. Alternately, call `kobweb { worker { fqcn.set(...) }` to explicitly set the generated class name.")
        }

        workerFactoryInfo = workerFactory

        return emptyList()
    }

    override fun finish() {
        val workerFactoryInfo = workerFactoryInfo ?: return

        val deps = Dependencies(
            aggregating = true,
            *listOfNotNull(
                workerFactoryInfo.classDeclaration.containingFile,
                workerFactoryInfo.inputTypeDeclaration.containingFile,
                workerFactoryInfo.inputTypeDeclaration.containingFile
            ).toTypedArray()
        )

        val workerPackage = classPackageOverride?.let {
            if (it.startsWith('.')) {
                workerFactoryInfo.classDeclaration.packageName.asString() + it
            } else {
                it
            }
        } ?: workerFactoryInfo.classDeclaration.packageName.asString()
        val workerClassName = classNameOverride
            ?: workerFactoryInfo.classDeclaration.simpleName.asString().removeSuffix("Factory")

        codeGenerator.createNewFile(
            deps,
            workerPackage,
            workerClassName
        ).writer().use { writer ->
            val workerFactoryType = workerFactoryInfo.classDeclaration.qualifiedName!!.asString()
            val inputType = workerFactoryInfo.inputTypeDeclaration.qualifiedName!!.asString()
            val outputType = workerFactoryInfo.outputTypeDeclaration.qualifiedName!!.asString()

            writer.write(
                """
            ${workerPackage.takeIf { it.isNotEmpty() }?.let { "package $it" } ?: ""}

            import org.w3c.dom.Worker

            class $workerClassName(override var onOutput: (output: $outputType) -> Unit = {}) :
                $WORKER_FQN<$inputType, $outputType> {

                private val ioSerializer = $workerFactoryType().createIOSerializer()

                private val worker = Worker("${KOBWEB_PUBLIC_WORKER_ROOT}/$outputPath").apply {
                    onmessage = { e ->
                        // If `IOSerializer` throws, that means the message was invalid. Ignore it.
                        val outputDeserialized = try {
                            ioSerializer.deserializeOutput(e.data as String)
                        } catch (e: Throwable) {
                            console.warn(buildString {
                                append("Unable to deserialize output generated by worker, ignoring it.")
                                e.message?.let { append("\nException: ${'$'}it") }
                            })
                            null
                        }
                        if (outputDeserialized != null) {
                            onOutput(outputDeserialized)
                        }
                    }
                }

                override fun postInput(input: $inputType) {
                    // If `IOSerializer` throws, that means the message was invalid. Ignore it.
                    val inputSerialized = try {
                        ioSerializer.serializeInput(input)
                    } catch (e: Throwable) {
                        null
                    }
                    if (inputSerialized != null) {
                        worker.postMessage(inputSerialized)
                    }
                }

                override fun terminate() {
                    worker.terminate()
                }
            }
                """.trimIndent()
            )
        }

        codeGenerator.createNewFile(
            deps,
            packageName = "",
            fileName = "main",
        ).writer().use { writer ->
            writer.write(
                """
                    private external val self: org.w3c.dom.DedicatedWorkerGlobalScope

                    fun main() {
                        val factory = ${workerFactoryInfo.classDeclaration.qualifiedName!!.asString()}()
                        val ioSerializer = factory.createIOSerializer()
                        val strategy = factory.createStrategy { output ->
                            // If `IOSerializer` throws, that means the message was invalid. Ignore it.
                            val outputSerialized = try {
                                ioSerializer.serializeOutput(output)
                            } catch (e: Throwable) {
                                null
                            }
                            if (outputSerialized != null) {
                                self.postMessage(outputSerialized)
                            }
                        }
                        self.onmessage = { e ->
                            // If `IOSerializer` throws, that means the message was invalid. Ignore it.
                            val inputDeserialized = try {
                                ioSerializer.deserializeInput(e.data as String)
                            } catch (e: Throwable) {
                                console.warn(buildString {
                                    append("Unable to deserialize input passed into worker, ignoring it.")
                                    e.message?.let { append("\nException: ${'$'}it") }
                                })
                                null
                            }
                            if (inputDeserialized != null) {
                                strategy.onInput(inputDeserialized)
                            }
                        }
                    }
                """.trimIndent()
            )
        }
    }

    /**
     * Search the codebase for classes that implement `WorkerFactory`.
     *
     * After this processor runs, the [workerStrategies] property will be populated with all the classes that implement
     * this base class, along with their input and output types.
     *
     * Ideally, a Kobweb worker module has exactly one implementation. If there are none or multiple, an error should be
     * reported to the user, but this is handled at a higher level.
     */
    private inner class WorkerFactoryVisitor : KSVisitorVoid() {
        private val _workerStrategies = mutableListOf<WorkerFactoryInfo>()
        val workerStrategies: List<WorkerFactoryInfo> = _workerStrategies

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val workerFactoryBaseClass = classDeclaration
                .superTypes
                .filter { it.toString() == WORKER_FACTORY_SIMPLE_NAME }
                .mapNotNull {
                    it.resolve()
                        .takeIf { resolved -> resolved.declaration.qualifiedName?.asString() == WORKER_FACTORY_FQN }
                }
                .firstOrNull()

            if (workerFactoryBaseClass != null) {
                val resolvedTypes = workerFactoryBaseClass.arguments.mapNotNull { it.type?.resolve() }

                // WorkerFactory<I, O>
                check(resolvedTypes.size == 2) {
                    "Unexpected error parsing $WORKER_FACTORY_SIMPLE_NAME subclass. Expected 2 type arguments, got ${resolvedTypes.size}: [${
                        resolvedTypes.joinToString {
                            it.declaration.qualifiedName?.asString() ?: "?"
                        }
                    }]"
                }
                _workerStrategies.add(
                    WorkerFactoryInfo(
                        classDeclaration,
                        inputTypeDeclaration = resolvedTypes[0].declaration,
                        outputTypeDeclaration = resolvedTypes[1].declaration,
                    )
                )
            }
        }
    }
}
