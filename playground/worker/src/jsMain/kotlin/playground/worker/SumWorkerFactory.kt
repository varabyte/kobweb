package playground.worker

import com.varabyte.kobweb.worker.WorkerFactory
import com.varabyte.kobweb.worker.WorkerStrategy
import com.varabyte.kobwebx.worker.kotlinx.serialization.util.createIOSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SumInputs(val a: Int, val b: Int)

@Serializable
data class SumOutput(val sum: Int)

internal class SumWorkerFactory : WorkerFactory<SumInputs, SumOutput> {
    override fun createStrategy(postOutput: (SumOutput) -> Unit) = WorkerStrategy<SumInputs> { input ->
        postOutput(SumOutput(input.a + input.b))
    }

    override fun createIOSerializer() = Json.createIOSerializer<SumInputs, SumOutput>()
}
