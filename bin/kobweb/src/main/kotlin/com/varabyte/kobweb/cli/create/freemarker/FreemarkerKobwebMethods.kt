package com.varabyte.kobweb.cli.create.freemarker

import com.varabyte.kobweb.cli.common.Validations
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModelException

class IsPackageMethod : TemplateMethodModelEx {
    override fun exec(arguments: MutableList<Any?>): Any? {
        if (arguments.size != 1) {
            throw TemplateModelException("`isPackage` expected a single value, got: [${arguments.joinToString()}]")
        }
        return Validations.validPackage(arguments[0].toString())
    }
}

class PackageToPathMethod : TemplateMethodModelEx {
    override fun exec(arguments: MutableList<Any?>): Any {
        if (arguments.size != 1) {
            throw TemplateModelException("`packateToPath` expected a single value, got: [${arguments.joinToString()}]")
        }
        return (arguments[0].toString()).replace(".", "/")
    }
}