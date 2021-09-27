package com.varabyte.kobweb.cli.create.freemarker.methods

class YesNoToBoolMethod : SingleArgMethodModel() {
    override fun exec(value: String): String {
        val value = value.lowercase()
        return when {
            "yes".startsWith(value) -> "true"
            "true".startsWith(value) -> "true"
            else -> "false"
        }
    }
}

class IsYesNoMethod : SingleArgMethodModel() {
    override fun exec(value: String): String {
        val value = value.lowercase()
        return when {
            "yes".startsWith(value) -> "true"
            "no".startsWith(value) -> "true"
            else -> "false"
        }
    }
}