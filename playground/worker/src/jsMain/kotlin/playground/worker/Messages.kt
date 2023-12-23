package playground.worker

import kotlinx.serialization.Serializable

@Serializable
class WorkerInParams(val a: Int, val b: Int)

@Serializable
class WorkerOutParams(val sum: Int)
