package com.varabyte.kobweb.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class WorkerProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return WorkerProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options.getValue(KSP_WORKER_OUTPUT_PATH_KEY),
            environment.options[KSP_WORKER_FQCN_KEY]
        )
    }
}
