A Gradle plugin which, when applied, allows Kobweb to understand a worker model, generating relevant code and building
the final artifact in such a way that the application can easily extract and run the worker.

* A worker module is expected to contain a single `WorkerStrategy` implementation. This implementation will be used to
generate an associated `Worker` class.
  * The parsing of the `WorkerStrategy` implementation is handled by a KSP processor.
* This Gradle plugin will bundle a worker script into the final artifact (i.e. klib), which allows the Kobweb
  Application Gradle plugin to find it.
