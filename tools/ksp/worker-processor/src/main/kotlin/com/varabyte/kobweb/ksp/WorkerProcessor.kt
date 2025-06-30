package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getKotlinClassByName
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

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // If no WorkerFactory class is found, then we are somehow applying this KSP processor to a non-worker codebase
        // We don't expect this to happen, but it is easy enough to handle it by early aborting.
        val workerFactoryClass = resolver.getKotlinClassByName(WORKER_FACTORY_FQN) ?: return emptyList()
        val visitor = WorkerFactoryVisitor(workerFactoryClass)
        resolver.getAllFiles().forEach { file ->
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

        val inputType = workerFactoryInfo.inputTypeDeclaration.qualifiedName!!.asString()
        val outputType = workerFactoryInfo.outputTypeDeclaration.qualifiedName!!.asString()

        codeGenerator.createNewFile(
            deps,
            workerPackage,
            workerClassName
        ).writer().use { writer ->
            val workerFactoryType = workerFactoryInfo.classDeclaration.qualifiedName!!.asString()

            // language=kotlin
            writer.write(
                """
            ${workerPackage.takeIf { it.isNotEmpty() }?.let { "package $it" } ?: ""}

            import com.varabyte.kobweb.worker.Attachments
            import com.varabyte.kobweb.worker.WorkerContext
            import org.w3c.dom.Worker
            import kotlin.js.Json
            import kotlin.js.json

            class $workerClassName(override var onOutput: WorkerContext.(output: $outputType) -> Unit = {}) :
                $WORKER_FQN<$inputType, $outputType> {

                private val ioSerializer = $workerFactoryType().createIOSerializer()

                private val worker = Worker("/${KOBWEB_PUBLIC_WORKER_ROOT}/$outputPath").apply {
                    onmessage = { e ->
                        val json = e.data.unsafeCast<Json>()
                        val outputDeserialized = try {
                            ioSerializer.deserializeOutput(json["_output"] as String)
                        } catch (e: Throwable) {
                            console.warn(buildString {
                                append("Unable to deserialize output generated by worker, ignoring it.")
                                e.message?.let { append("\nException: ${'$'}it") }
                            })
                            null
                        }
                        if (outputDeserialized != null) {
                            val ctx = WorkerContext(Attachments.fromJson(json))
                            ctx.onOutput(outputDeserialized)
                        }
                    }
                }

                override fun postInput(input: $inputType, attachments: Attachments) {
                    val inputSerialized = try {
                        ioSerializer.serializeInput(input)
                    } catch (e: Throwable) {
                        console.warn(buildString {
                            append("Unable to serialize argument when calling `${workerClassName}.postInput($inputType)`, ignoring the call.")
                            e.message?.let { append("\nException: ${'$'}it") }
                        })
                        null
                    }
                    if (inputSerialized != null) {
                        worker.postMessage(
                            json("_input" to inputSerialized).add(attachments.toJson()),
                            attachments.toValues()
                        )
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
            // language=kotlin
            writer.write(
                """
                    import com.varabyte.kobweb.worker.Attachments
                    import com.varabyte.kobweb.worker.InputMessage
                    import com.varabyte.kobweb.worker.OutputDispatcher
                    import kotlin.js.Json
                    import kotlin.js.json

                    private external val self: org.w3c.dom.DedicatedWorkerGlobalScope

                    fun main() {
                        val factory = ${workerFactoryInfo.classDeclaration.qualifiedName!!.asString()}()
                        val ioSerializer = factory.createIOSerializer()
                        val strategy = factory.createStrategy(object : OutputDispatcher<$outputType> {
                            override fun invoke(output: $outputType, attachments: Attachments) {
                                val outputSerialized = try {
                                    ioSerializer.serializeOutput(output)
                                } catch (e: Throwable) {
                                    console.warn(buildString {
                                        append("Unable to serialize argument when calling `${workerFactoryInfo.classDeclaration.simpleName.asString()}.postOutput($outputType)`, ignoring the call.")
                                        e.message?.let { append("\nException: ${'$'}it") }
                                    })
                                    null   
                                }
                                if (outputSerialized != null) {
                                    self.postMessage(
                                        json("_output" to outputSerialized).add(attachments.toJson()),
                                        attachments.toValues()
                                    )
                                }
                            }
                        })
                        self.onmessage = { e ->
                            val json = e.data.unsafeCast<Json>()
                            val inputDeserialized = try {
                                ioSerializer.deserializeInput(json["_input"] as String)
                            } catch (e: Throwable) {
                                console.warn(buildString {
                                    append("Unable to deserialize input passed into worker, ignoring it.")
                                    e.message?.let { append("\nException: ${'$'}it") }
                                })
                                null
                            }
                            if (inputDeserialized != null) {
                                val attachments = Attachments.fromJson(json)
                                strategy.onInput(InputMessage(inputDeserialized, attachments))
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
    private inner class WorkerFactoryVisitor(workerFactoryClass: KSClassDeclaration) : KSVisitorVoid() {
        private val _workerStrategies = mutableListOf<WorkerFactoryInfo>()
        val workerStrategies: List<WorkerFactoryInfo> = _workerStrategies

        // The star-projected KSType is useful to use for checking if a class is a subclass of this one
        private val workerFactoryClassStarProjected = workerFactoryClass.asStarProjectedType()

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            // Quick check / early abort first to see if this class is even a subclass of WorkerFactory. If so, we'll do
            // more expensive checks later.
            if (!workerFactoryClassStarProjected.isAssignableFrom(classDeclaration.asStarProjectedType())) return

            val workerFactoryBaseClass = classDeclaration
                .getAllSuperTypes()
                .first { it.declaration.qualifiedName?.asString() == WORKER_FACTORY_FQN }

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
