package com.varabyte.kobweb.cli.create.freemarker.methods

import com.varabyte.kobweb.cli.common.Validations

class IsNotEmptyMethod : SingleArgMethodModel() {
    override fun exec(value: String): String? {
        return Validations.isNotEmpty(value)
    }
}