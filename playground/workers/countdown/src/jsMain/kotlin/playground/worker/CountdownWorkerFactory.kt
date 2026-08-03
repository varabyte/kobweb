package playground.worker

import com.varabyte.kobweb.browser.util.setInterval
import com.varabyte.kobweb.serialization.createIOSerializer
import com.varabyte.kobweb.worker.OutputDispatcher
import com.varabyte.kobweb.worker.WorkerFactory
import com.varabyte.kobweb.worker.WorkerStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class CountdownInput(val id: Int, val amount: Duration, val step: Duration = 1.seconds)

@Serializable
data class CountdownOutput(val id: Int, val remaining: Duration) {
    fun isFinished() = remaining == CountdownWorkerFactory.FINISHED_DURATION
}

internal class CountdownWorkerFactory : WorkerFactory<CountdownInput, CountdownOutput> {
    companion object {
        internal val FINISHED_DURATION = 0.seconds
    }

    private val remaining = mutableMapOf<Int, Duration>()

    override fun createStrategy(postOutput: OutputDispatcher<CountdownOutput>) = WorkerStrategy<CountdownInput> { input ->
        if (input.amount == FINISHED_DURATION) {
            remaining.remove(input.id)
            return@WorkerStrategy
        }
        remaining[input.id] = input.amount
        postOutput(CountdownOutput(input.id, input.amount))

        self.setInterval(input.step) {
            var remainingAmount = remaining[input.id]
            if (remainingAmount == null) {
                cancelInterval()
            } else {
                if (remainingAmount < input.step) {
                    remaining.remove(input.id)
                    postOutput(CountdownOutput(input.id, FINISHED_DURATION))
                    cancelInterval()
                } else {
                    remainingAmount -= input.step
                    remaining[input.id] = remainingAmount
                    postOutput(CountdownOutput(input.id, remainingAmount))
                }
            }
        }
    }

    override fun createIOSerializer() = Json.createIOSerializer<CountdownInput, CountdownOutput>()
}
