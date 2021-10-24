package com.varabyte.kobweb.gradle.application.templates

fun createApisFactoryImpl(apiFqcnRoutes: Map<String, String>, initMethods: List<String>): String {
    return """
        import com.varabyte.kobweb.api.Apis
        import com.varabyte.kobweb.api.ApisFactory
        import com.varabyte.kobweb.api.InitContext
        import com.varabyte.kobweb.api.data.MutableData
        import com.varabyte.kobweb.api.log.Logger

        class ApisFactoryImpl : ApisFactory {
            override fun create(logger: Logger): Apis {
                val data = MutableData()
                val apis = Apis(data, logger)
                ${
                    // Generates lines like: apis.register("/path/to/api") { ctx -> path.to.api.method(ctx)  }
                    apiFqcnRoutes.entries.joinToString("\n                ") { entry ->
                        val apiFqcn = entry.key
                        val route = entry.value
                        
                        """apis.register("$route") { ctx -> $apiFqcn(ctx) }"""
                    }
                }

                ${
                    if (initMethods.isNotEmpty()) {
                        "val initCtx = InitContext(apis, data, logger)"
                    }
                    else {
                        ""
                    }
                }
                ${
                    initMethods.joinToString("\n                ") { initFqcn ->
                        """$initFqcn(initCtx)"""
                    }
                }
                return apis
            }
        }
    """.trimIndent()
}