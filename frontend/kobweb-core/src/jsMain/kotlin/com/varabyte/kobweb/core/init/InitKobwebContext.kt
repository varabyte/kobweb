package com.varabyte.kobweb.core.init

import com.varabyte.kobweb.navigation.Router

/**
 * Various classes useful for methods that are called when a page is first loaded.
 */
class InitKobwebContext(val config: MutableKobwebConfig, val router: Router)

fun initKobweb(router: Router, init: (InitKobwebContext) -> Unit) {
    val config = MutableKobwebConfig()
    init(InitKobwebContext(config, router))
    MutableKobwebConfigInstance = config
}