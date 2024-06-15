package com.varabyte.kobweb.test.compose

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.dom.clear
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise
import kotlin.time.DurationUnit
import kotlin.time.toDuration

// initially forked from https://github.com/JetBrains/compose-multiplatform/blob/master/html/test-utils/src/jsMain/kotlin/org/jetbrains/compose/web/testutils/TestUtils.kt

/**
 * This class provides a set of utils methods to simplify compose-web tests.
 * There is no need to create its instances manually.
 *
 * @see [runComposeTest]
 */
class ComposeTestScope : CoroutineScope by MainScope() {

    /**
     * It's used as a parent element for the composition.
     * It's added into the document's body automatically.
     */
    val root = document.createElement("div") as HTMLElement

    private var waitForRecompositionCompleteContinuation: Continuation<Unit>? = null

    init {
        document.body!!.appendChild(root)
    }

    private fun onRecompositionComplete() {
        waitForRecompositionCompleteContinuation?.resume(Unit)
        waitForRecompositionCompleteContinuation = null
    }

    /**
     * Cleans up the [root] content.
     * Creates a new composition with a given Composable [content].
     */
    fun composition(content: @Composable () -> Unit) {
        root.clear()

        renderComposable(
            root = root, monotonicFrameClock = TestMonotonicClockImpl(
                onRecomposeComplete = this::onRecompositionComplete
            )
        ) {
            content()
        }
    }

     /**
     * Suspends until element with [elementId] observes any change to its html.
     */
    suspend fun waitForChanges(elementId: String) {
        waitForChanges(document.getElementById(elementId) as HTMLElement)
    }

    /**
     * Suspends until [element] observes any change to its html.
     */
    suspend fun waitForChanges(element: HTMLElement = root) {
        suspendCancellableCoroutine { continuation ->
            val observer = MutationObserver { _, observer ->
                continuation.resume(Unit)
                observer.disconnect()
            }
            observer.observe(element, MutationObserverOptions)

            continuation.invokeOnCancellation {
                observer.disconnect()
            }
        }
    }

    /**
     * Suspends until recomposition completes.
     */
    suspend fun waitForRecompositionComplete() {
        suspendCancellableCoroutine { continuation ->
            waitForRecompositionCompleteContinuation = continuation

            continuation.invokeOnCancellation {
                if (waitForRecompositionCompleteContinuation === continuation) {
                    waitForRecompositionCompleteContinuation = null
                }
            }
        }
    }
}

/**
 * Use this method to test compose-web components rendered using HTML.
 * Declare states and make assertions in [block].
 * Use [ComposeTestScope.composition] to define the code under test.
 *
 * For dynamic tests, use [ComposeTestScope.waitForRecompositionComplete]
 * after changing state's values and before making assertions.
 *
 * @see [ComposeTestScope.composition]
 * @see [ComposeTestScope.waitForRecompositionComplete]
 * @see [ComposeTestScope.waitForChanges].
 *
 * Test example:
 * ```
 * @Test
 * fun textChild() = runTest {
 *      var textState by mutableStateOf("inner text")
 *
 *      composition {
 *          Div {
 *              Text(textState)
 *          }
 *      }
 *      assertEquals("<div>inner text</div>", root.innerHTML)
 *
 *      textState = "new text"
 *      waitForRecompositionComplete()
 *
 *      assertEquals("<div>new text</div>", root.innerHTML)
 * }
 * ```
 */
fun runComposeTest(block: suspend ComposeTestScope.() -> Unit): Promise<Any> {
    val scope = ComposeTestScope()
    return scope.promise { block(scope) }
}

private object MutationObserverOptions : MutationObserverInit {
    override var childList: Boolean? = true
    override var attributes: Boolean? = true
    override var characterData: Boolean? = true
    override var subtree: Boolean? = true
    override var attributeOldValue: Boolean? = true
}

private class TestMonotonicClockImpl(private val onRecomposeComplete: () -> Unit) : MonotonicFrameClock {

    override suspend fun <R> withFrameNanos(
        onFrame: (Long) -> R
    ): R = suspendCoroutine { continuation ->
        window.requestAnimationFrame {
            val duration = it.toDuration(DurationUnit.MILLISECONDS)
            val result = onFrame(duration.inWholeNanoseconds)
            continuation.resume(result)
            onRecomposeComplete()
        }
    }
}
