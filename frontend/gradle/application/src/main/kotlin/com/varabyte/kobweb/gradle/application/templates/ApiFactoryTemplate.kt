package com.varabyte.kobweb.gradle.application.templates

fun createApisFactoryImpl(apiFqcnRoutes: Map<String, String>, apiInitMethods: List<String>): String {
    return """
        import com.varabyte.kobweb.api.Apis
        import com.varabyte.kobweb.api.ApiInitContext
        import com.varabyte.kobweb.api.ApisFactory
        import com.varabyte.kobweb.api.data.Data
        import com.varabyte.kobweb.api.data.MutableData
        import com.varabyte.kobweb.api.log.Logger

        class ApisFactoryImpl : ApisFactory {
            override fun create(logger: Logger): Apis {
                val data = MutableData()
                val apis = Apis(data, logger)
                ${
                    // Generates lines like: apis.register("/path/to/api") { ctx -> path.to.api.method(ctx)  }
                    apiFqcnRoutes.entries.joinToString("\n                ") { entry ->
                        val apiFcqn = entry.key
                        val route = entry.value
                        
                        """apis.register("$route") { ctx -> $apiFcqn(ctx) }"""
                    }
                }

                ${
                    if (apiInitMethods.isNotEmpty()) {
                        "val initCtx = ApiInitContext(apis, data, logger)"
                    }
                    else {
                        ""
                    }
                }
                ${
                    apiInitMethods.joinToString("\n                ") { apiInitFcqn ->
                        """$apiInitFcqn(initCtx)"""
                    }
                }
                return apis
            }
        }
    """.trimIndent()
}
