package com.varabyte.kobweb.gradle.application.templates

fun createApisFactoryImpl(apiFqcnRoutes: Map<String, String>): String {
    return """
        import com.varabyte.kobweb.api.Apis
        import com.varabyte.kobweb.api.ApisFactory
        import java.nio.file.Path

        class ApisFactoryImpl : ApisFactory {
            override fun create(dataRoot: Path): Apis {
                val apis = Apis(dataRoot)
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