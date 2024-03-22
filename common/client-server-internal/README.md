Common code classes and utility methods that should be shared between Kobweb client (i.e. kobweb-core) and Kobweb
server (e.g. `Routing.kt`) code.

This module depends on Kotlinx serialization which can make it useful for classes that need to be sent across the wire
between a Kobweb client and server.

These classes / utility methods are internal only and will not be exposed to the developer using Kobweb.
