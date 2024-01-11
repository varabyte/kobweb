package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.getAllSuperTypes
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
 * A KSP processor that generates code that instantiates / wraps a Worker class related to a given `WorkerStrategy`
 * implementation.
 *
 * For example, if the user defines a class called `CalculatePiWorkerStrategy`, then this processor will generate a
 * `main.kt` file that instantiates it plus a `CalculatePiWorker` class that wraps it and acts as the main way a
 * Kobweb application would interact with this module.
 */
class WorkerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val outputPath: String,
    workerFqcnOverride: String? = null,
) : SymbolProcessor {
    class WorkerStrategyInfo(
        val classDeclaration: KSClassDeclaration,
        val inputTypeDeclaration: KSDeclaration,
        val outputTypeDeclaration: KSDeclaration,
    )

    private var workerStrategyInfo: WorkerStrategyInfo? = null

    // See WorkerBlock.fqcn property documentation for explicit examples for converting the fqcn value to final
    // values for class generation.
    private val classNameOverride = workerFqcnOverride?.substringAfterLast('.')?.takeIf { it.isNotBlank() }
    private val classPackageOverride = workerFqcnOverride?.substringBeforeLast('.')?.takeIf { it.isNotBlank() }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allFiles = resolver.getAllFiles()

        val visitor = WorkerStrategyVisitor()

        allFiles.forEach { file ->
            file.accept(visitor, Unit)
        }

        val workerStrategy = visitor.workerStrategies.singleOrNull() ?: run {
            error(buildString {
                append("A Kobweb worker module must have exactly one class that implements `WorkerStrategy`. ")
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

        if (workerStrategy.classDeclaration.getConstructors()
                .none { (it.isPublic() || it.isInternal()) && (it.parameters.isEmpty() || it.parameters.all { param -> param.hasDefault }) }
        ) {
            error("A Kobweb `WorkerStrategy` implementation must have a public empty constructor. Please add one to `${workerStrategy.classDeclaration.qualifiedName!!.asString()}`.")
        }

        if (workerStrategy.classDeclaration.isPrivate()) {
            error("A Kobweb `WorkerStrategy` implementation cannot be private, as this prevents us from generating code that wraps it. Please make `${workerStrategy.classDeclaration.qualifiedName!!.asString()}` internal.")
        }
        if (workerStrategy.classDeclaration.isPublic()) {
            val publicSuppression = "PUBLIC_WORKER_STRATEGY"
            if (!workerStrategy.classDeclaration.suppresses(publicSuppression)) {
                logger.warn("It is recommended that you make your `WorkerStrategy` implementation internal to prevent Kobweb applications from using it unintentionally. Please add `internal` to `${workerStrategy.classDeclaration.qualifiedName!!.asString()}`. You can annotate your class with `@Suppress(\"$publicSuppression\")` to suppress this warning.")
            }
        }

        if (!workerStrategy.inputTypeDeclaration.isPublic()) {
            error("A Kobweb `WorkerStrategy` implementation's input type must be public so the Kobweb application can use it. Please make `${workerStrategy.inputTypeDeclaration.qualifiedName!!.asString()}` public.")
        }

        if (!workerStrategy.outputTypeDeclaration.isPublic()) {
            error("A Kobweb `WorkerStrategy` implementation's output type must be public so the Kobweb application can use it. Please make `${workerStrategy.outputTypeDeclaration.qualifiedName!!.asString()}` public.")
        }

        if (classNameOverride == null && !workerStrategy.classDeclaration.qualifiedName!!.asString()
                .let { it.endsWith("WorkerStrategy") && it != "WorkerStrategy" }
        ) {
            error("A Kobweb `WorkerStrategy` implementation's name should end with the suffix \"WorkerStrategy\", so we can automatically generate an associated \"Worker\" class from it. Please change the name of `${workerStrategy.classDeclaration.qualifiedName!!.asString()}` to meet this requirement. Alternately, call `kobweb { worker { fqcn.set(...) }` to explicitly set the generated class name.")
        }

        workerStrategyInfo = workerStrategy

        return emptyList()
    }

    override fun finish() {
        val workerStrategyInfo = workerStrategyInfo ?: return

        val deps = Dependencies(
            aggregating = true,
            *listOfNotNull(
                workerStrategyInfo.classDeclaration.containingFile,
                workerStrategyInfo.inputTypeDeclaration.containingFile,
                workerStrategyInfo.inputTypeDeclaration.containingFile
            ).toTypedArray()
        )

        val workerPackage = classPackageOverride?.let {
            if (it.startsWith('.')) {
                workerStrategyInfo.classDeclaration.packageName.asString() + it
            } else {
                it
            }
        } ?: workerStrategyInfo.classDeclaration.packageName.asString()
        val workerClassName = classNameOverride
            ?: workerStrategyInfo.classDeclaration.simpleName.asString().removeSuffix("Strategy")

        codeGenerator.createNewFile(
            deps,
            workerPackage,
            workerClassName
        ).writer().use { writer ->
            val strategyWorkerType = workerStrategyInfo.classDeclaration.qualifiedName!!.asString()
            val inputType = workerStrategyInfo.inputTypeDeclaration.qualifiedName!!.asString()
            val outputType = workerStrategyInfo.outputTypeDeclaration.qualifiedName!!.asString()

            writer.write(
                """
                    ${workerPackage.takeIf { it.isNotEmpty() }?.let { "package $it" } ?: ""}

                    import org.w3c.dom.Worker

                    class $workerClassName(override var onOutput: ($outputType) -> Unit = {}): com.varabyte.kobweb.worker.Worker<$inputType, $outputType> {
                        private val ioSerializer = $strategyWorkerType().ioSerializer

                        private val worker = Worker("${KOBWEB_PUBLIC_WORKER_ROOT}/$outputPath").apply {
                            onmessage = { e ->
                                // If `IOSerializer` throws, that means the message was invalid. Ignore it.
                                val outputDeserialized = try {
                                    ioSerializer.deserializeOutput(e.data as String)
                                } catch(e: Throwable) {
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
                            } catch(e: Throwable) {
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
                    fun main() {
                        // As a side effect, registers self.onmessage handler
                        ${workerStrategyInfo.classDeclaration.qualifiedName!!.asString()}()
                    }
                """.trimIndent()
            )
        }
    }

    /**
     * Search the codebase for classes that implement `WorkerStrategy`.
     *
     * After this processor runs, the [workerStrategies] property will be populated with all the classes that implement
     * this base class, along with their input and output types.
     *
     * Ideally, a Kobweb worker module has exactly one implementation. If there are none or multiple, an error should be
     * reported to the user, but this is handled at a higher level.
     */
    private inner class WorkerStrategyVisitor : KSVisitorVoid() {
        private val _workerStrategies = mutableListOf<WorkerStrategyInfo>()
        val workerStrategies: List<WorkerStrategyInfo> = _workerStrategies

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val workerStrategyBaseClass = classDeclaration
                .getAllSuperTypes()
                .filter { it.declaration.qualifiedName?.asString() == "com.varabyte.kobweb.worker.WorkerStrategy" }
                .firstOrNull()

            if (workerStrategyBaseClass != null) {
                val resolvedTypes = workerStrategyBaseClass.arguments.mapNotNull { it.type?.resolve() }

                // WorkerStrategy<I, O>
                check(resolvedTypes.size == 2) {
                    "Unexpected error parsing WorkerStrategy subclass. Expected 2 type arguments, got ${resolvedTypes.size}: [${
                        resolvedTypes.joinToString {
                            it.declaration.qualifiedName?.asString() ?: "?"
                        }
                    }]"
                }
                _workerStrategies.add(
                    WorkerStrategyInfo(
                        classDeclaration,
                        inputTypeDeclaration = resolvedTypes[0].declaration,
                        outputTypeDeclaration = resolvedTypes[1].declaration,
                    )
                )
            }
        }
    }
}
