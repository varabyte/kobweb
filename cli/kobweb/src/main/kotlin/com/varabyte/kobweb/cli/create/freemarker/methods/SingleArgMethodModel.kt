package com.varabyte.kobweb.cli.create.freemarker.methods

import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModelException

abstract class SingleArgMethodModel : TemplateMethodModelEx {
    final override fun exec(arguments: MutableList<Any?>): Any? {
        if (arguments.size != 1) {
            throw TemplateModelException("Expected a single value, got: [${arguments.joinToString()}]")
        }
        return handleArgument(arguments[0].toString())
    }

    protected abstract fun handleArgument(value: String): String?
}