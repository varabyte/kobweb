package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.silk.theme.colors.ColorMode

/**
 * Configuration values which are frozen at initialization time and accessed globally within Silk after that point.
 */
interface SilkConfig {
    companion object {
        val Instance: SilkConfig get() = MutableSilkConfigInstance
    }

    val initialColorMode: ColorMode
}

class MutableSilkConfig : SilkConfig {
    override var initialColorMode: ColorMode = ColorMode.LIGHT
}

internal var MutableSilkConfigInstance: MutableSilkConfig = MutableSilkConfig()
