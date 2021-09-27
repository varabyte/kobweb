package com.varabyte.kobweb.cli.create.freemarker.methods

import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModelException

abstract class SingleArgMethodModel : TemplateMethodModelEx {
    final override fun exec(arguments: List<Any?>): Any? {
        if (arguments.size != 1) {
            throw TemplateModelException("Expected a single value, got: [${arguments.joinToString()}]")
        }
        return exec(arguments[0].toString())
    }

    abstract fun exec(value: String): String?
}