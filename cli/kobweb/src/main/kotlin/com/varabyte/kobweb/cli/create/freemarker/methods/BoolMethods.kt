package com.varabyte.kobweb.cli.create.freemarker.methods

class IsYesNoMethod : SingleArgMethodModel() {
    override fun exec(value: String): String? {
        val valueLower = value.lowercase()
        return if (listOf("yes", "no", "true", "false").any { it.startsWith(valueLower) }) {
            null
        }
        else {
            "Answer must be yes or no"
        }
    }
}

class YesNoToBoolMethod : SingleArgMethodModel() {
    override fun exec(value: String): String {
        val valueLower = value.lowercase()
        return (listOf("yes", "true").any { it.startsWith(valueLower) }).toString()
    }
}

