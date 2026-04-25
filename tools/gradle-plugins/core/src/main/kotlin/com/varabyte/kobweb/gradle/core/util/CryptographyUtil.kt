package com.varabyte.kobweb.gradle.core.util

import java.security.MessageDigest

fun String.toSha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
