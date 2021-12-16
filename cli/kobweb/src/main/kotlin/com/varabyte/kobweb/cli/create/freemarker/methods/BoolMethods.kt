package com.varabyte.kobweb.cli.create.freemarker.methods

class YesNoToBoolMethod : SingleArgMethodModel() {
    override fun exec(value: String): String {
        val valueLower = value.lowercase()
        return when {
            "yes".startsWith(valueLower) -> "true"
            "true".startsWith(valueLower) -> "true"
            else -> "false"
        }
    }
}

class IsYesNoMethod : SingleArgMethodModel() {
    override fun exec(value: String): String {
        val valueLower = value.lowercase()
        return when {
            "yes".startsWith(valueLower) -> "true"
            "no".startsWith(valueLower) -> "true"
            else -> "false"
        }
    }
}