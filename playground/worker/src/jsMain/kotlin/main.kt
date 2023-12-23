import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.MessageEvent
import playground.worker.WorkerInParams
import playground.worker.WorkerOutParams

external val self: DedicatedWorkerGlobalScope

fun main() {
    self.onmessage = { m: MessageEvent ->
        val inParams = Json.decodeFromString<WorkerInParams>(m.data as String)
        self.postMessage(Json.encodeToString(WorkerOutParams(inParams.a + inParams.b)))
    }
}
