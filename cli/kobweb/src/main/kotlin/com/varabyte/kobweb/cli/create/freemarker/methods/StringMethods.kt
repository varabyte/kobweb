package com.varabyte.kobweb.cli.create.freemarker.methods

import com.varabyte.kobweb.cli.common.Validations

class IsNotEmptyMethod : SingleArgMethodModel() {
    override fun handleArgument(value: String): String? {
        return Validations.isNotEmpty(value)
    }
}