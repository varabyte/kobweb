package com.varabyte.kobweb.cli.create.freemarker

import com.varabyte.kobweb.cli.common.queryUser
import com.varabyte.kobweb.cli.create.Instruction
import com.varabyte.kobweb.cli.create.freemarker.methods.*
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.runtime.KonsoleApp
import freemarker.cache.NullCacheStorage
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.TemplateMethodModelEx
import java.io.StringReader
import java.io.StringWriter
import java.nio.file.Path

private fun String.process(cfg: Configuration, model: Map<String, Any>): String {
    val reader = StringReader(this)
    val writer = StringWriter()
    Template("unused", reader, cfg).process(model, writer)
    return writer.buffer.toString()
}

class FreemarkerState(src: Path, dest: Path, projectFolder: String) {
    private val model = mutableMapOf<String, Any>(
        "projectFolder" to projectFolder,

        "isNotEmpty" to IsNotEmptyMethod(),
        "isPackage" to IsPackageMethod(),

        "fileToName" to FileToNameMethod(),
        "fileToPackage" to FileToPackageMethod(),
        "packageToPath" to PackageToPathMethod(),
    )

    // See also: https://freemarker.apache.org/docs/pgui_quickstart_all.html
    private val cfg = Configuration(Configuration.VERSION_2_3_31).apply {
        setDirectoryForTemplateLoading(src.toFile())
        // Kobweb doesn't serve templates - it just runs through files once. No need to cache.
        cacheStorage = NullCacheStorage()
        defaultEncoding = "UTF-8"
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        logTemplateExceptions = false
        wrapUncheckedExceptions = true
        fallbackOnNullLoopVariable = false
    }

    fun execute(app: KonsoleApp, instructions: List<Instruction>) {
        for (inst in instructions) {
            when (inst) {
                is Instruction.DefineVar -> {
                    model[inst.name] = inst.value.process(cfg, model)
                    app.apply {
                        konsole {
                            textLine("DefineVar: ${inst.name} = ${model[inst.name]}")
                        }.run()
                    }
                }
                is Instruction.Keep -> app.apply {
                    konsole {
                        textLine("Keep")
                    }.run()
                }

                is Instruction.Move -> app.apply {
                    konsole {
                        textLine("Move")
                    }.run()
                }

                is Instruction.ProcessFreemarker -> app.apply {
                    konsole {
                        textLine("ProcessFreemarker")
                    }.run()
                }

                is Instruction.QueryVar -> app.apply {
                    val default = inst.default?.process(cfg, model)
                    val answer = queryUser(inst.prompt, default, validateAnswer = { value ->
                        (model[inst.validation] as? TemplateMethodModelEx)?.exec(listOf(value))?.toString()
                    })
                    model[inst.name] = answer
                    konsole {
                        textLine("QueryVar: ${inst.name} = ${model[inst.name]}")
                    }.run()
                }
            }
        }
    }
}