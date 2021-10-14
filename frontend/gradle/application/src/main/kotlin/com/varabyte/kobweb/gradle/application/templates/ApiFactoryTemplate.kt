package com.varabyte.kobweb.gradle.application.templates

fun createApisFactoryImpl(apiFqcnRoutes: Map<String, String>): String {
    return """
        import com.varabyte.kobweb.api.Apis
        import com.varabyte.kobweb.api.ApisFactory

        class ApisFactoryImpl : ApisFactory {
            override fun create(): Apis {
                val apis = Apis()
                ${
                    // Generates lines like: apis.register("/path/to/api") { ctx -> path.to.api.method(ctx)  }
                    apiFqcnRoutes.entries.joinToString("\n                ") { entry ->
                        val apiFcqn = entry.key
                        val route = entry.value
                        
                        """apis.register("$route") { ctx -> $apiFcqn(ctx) }"""
                    }
                }
                return apis
            }
        }
    """.trimIndent()
}