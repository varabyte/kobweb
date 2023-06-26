package com.varabyte.kobweb.core.init

import com.varabyte.kobweb.navigation.OpenLinkStrategy

interface OpenLinkStrategies {
    val internal: OpenLinkStrategy
    val external: OpenLinkStrategy
}

class MutableOpenLinkStrategies(
    override var internal: OpenLinkStrategy = OpenLinkStrategy.IN_PLACE,
    override var external: OpenLinkStrategy = OpenLinkStrategy.IN_NEW_TAB
) : OpenLinkStrategies

interface KobwebConfig {
    companion object {
        val Instance: KobwebConfig get() = MutableKobwebConfigInstance
    }

    val openLinkStrategies: OpenLinkStrategies
}

class MutableKobwebConfig : KobwebConfig {
    override val openLinkStrategies = MutableOpenLinkStrategies()
}

internal var MutableKobwebConfigInstance: MutableKobwebConfig = MutableKobwebConfig()
