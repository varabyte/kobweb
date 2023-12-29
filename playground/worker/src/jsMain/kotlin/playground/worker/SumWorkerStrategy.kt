package playground.worker

import com.varabyte.kobweb.worker.WorkerStrategy
import com.varabyte.kobwebx.worker.kotlinx.serialization.util.createIOSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SumInputs(val a: Int, val b: Int)

@Serializable
data class SumOutput(val sum: Int)

internal class SumWorkerStrategy: WorkerStrategy<SumInputs, SumOutput>() {
    override fun onInput(input: SumInputs) {
        postOutput(SumOutput(input.a + input.b))
    }

    override val ioSerializer = Json.createIOSerializer<SumInputs, SumOutput>()
}
