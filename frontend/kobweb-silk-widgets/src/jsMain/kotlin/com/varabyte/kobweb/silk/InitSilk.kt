package com.varabyte.kobweb.silk

// TODO(#168): Remove in v1.0
@Deprecated(
    message = "InitSilkContext has moved. Please change your import to `com.varabyte.kobweb.silk.init.InitSilkContext`",
    replaceWith = ReplaceWith("com.varabyte.kobweb.silk.init.InitSilkContext")
)
typealias InitSilkContext = com.varabyte.kobweb.silk.init.InitSilkContext

// TODO(#168): Remove in v1.0
@Deprecated(
    message = "initSilk has moved. Please change your import to `com.varabyte.kobweb.silk.init.initSilk`",
    replaceWith = ReplaceWith("com.varabyte.kobweb.silk.init.initSilk(additionalInit)")
)
fun initSilk(additionalInit: (com.varabyte.kobweb.silk.init.InitSilkContext) -> Unit = {}) {
    com.varabyte.kobweb.silk.init.initSilk(additionalInit)
}