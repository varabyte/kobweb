package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.KobwebComposeStyles
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.style.breakpoint.SilkBreakpointDisplayStyles
import org.jetbrains.compose.web.css.*

/**
 * Handle initialization so that the rest of your application can use Silk widgets.
 *
 * NOTE: If you are calling this method manually yourself (that is, you're not using `SilkApp` which handles this for
 * you), you may also want to call [KobwebComposeStyles] to enable support for compose-ish widgets like `Box`,
 * `Column`, `Row`, etc.
 */
@Composable
fun SilkFoundationStyles(initSilk: (InitSilkContext) -> Unit = {}) {
    key(Unit) {
        // Use (abuse?) key to run logic only first time SilkApp is called
        initSilk { ctx -> initSilk(ctx) }
    }

    Style(SilkStyleSheet)
    SilkBreakpointDisplayStyles()
}
