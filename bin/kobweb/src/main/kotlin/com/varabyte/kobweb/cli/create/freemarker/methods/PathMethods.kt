package com.varabyte.kobweb.cli.create.freemarker.methods

import com.varabyte.kobweb.cli.common.Validations
import java.util.*

class FileToNameMethod : SingleArgMethodModel() {
    override fun handleArgument(value: String): String {
        return value
            .split(Regex("""[^\w]"""))
            .filter { it.isNotBlank() }
            .joinToString(" ") { it ->
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }
}

class FileToPackageMethod : SingleArgMethodModel() {
    override fun handleArgument(value: String): String {
        return value.lowercase().replace(Regex("""[^\w_]"""), "")
    }
}

class IsPackageMethod : SingleArgMethodModel() {
    override fun handleArgument(value: String): String? {
        return Validations.isValidPackage(value)
    }
}

class PackageToPathMethod : SingleArgMethodModel() {
    override fun handleArgument(value: String): String? {
        return value.replace(".", "/")
    }
}