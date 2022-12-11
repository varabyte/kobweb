package com.varabyte.kobweb.common.text

fun String.prefixIfNot(prefix: String) = if (this.startsWith(prefix)) this else prefix + this
fun String.suffixIfNot(suffix: String) = if (this.endsWith(suffix)) this else this + suffix
fun String.ensureSurrounded(prefix: String, suffix: String = prefix) = this.prefixIfNot(prefix).suffixIfNot(suffix)
