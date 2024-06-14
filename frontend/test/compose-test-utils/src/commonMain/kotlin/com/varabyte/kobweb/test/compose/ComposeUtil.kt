package com.varabyte.kobweb.test.compose

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

// copied from https://github.com/JetBrains/compose-multiplatform/blob/b5ca5d4dc129f34e1ed6a2aabfba08b300ed5213/html/compose-compiler-integration/main-template/src/jsMain/kotlin/Deps.kt#L4
private class UnitApplier : Applier<Unit> {
    override val current: Unit
        get() = Unit

    override fun down(node: Unit) {}
    override fun up() {}
    override fun insertTopDown(index: Int, instance: Unit) {}
    override fun insertBottomUp(index: Int, instance: Unit) {}
    override fun remove(index: Int, count: Int) {}
    override fun move(from: Int, to: Int, count: Int) {}
    override fun clear() {}
}

private fun createRecomposer(): Recomposer {
    val mainScope = CoroutineScope(
        NonCancellable + Dispatchers.Main + DefaultMonotonicFrameClock
    )

    return Recomposer(mainScope.coroutineContext).also {
        mainScope.launch(start = CoroutineStart.UNDISPATCHED) {
            it.runRecomposeAndApplyChanges()
        }
    }
}


fun callComposable(content: @Composable () -> Unit) {
    val c = ControlledComposition(UnitApplier(), createRecomposer())
    c.setContent(content)
}
