package com.varabyte.kobweb.common.error

class KobwebException(msg: String = "", cause: Throwable? = null) : Exception(msg, cause)
