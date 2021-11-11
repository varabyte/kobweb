package com.varabyte.kobweb.gradle.application.templates

import com.varabyte.kobweb.gradle.application.project.api.ApiEntry
import com.varabyte.kobweb.gradle.application.project.api.InitApiEntry

fun createApisFactoryImpl(apiEntries: List<ApiEntry>, initEntries: List<InitApiEntry>): String {
    return """
        import com.varabyte.kobweb.api.Apis
        import com.varabyte.kobweb.api.ApisFactory
        import com.varabyte.kobweb.api.InitApiContext
        import com.varabyte.kobweb.api.data.MutableData
        import com.varabyte.kobweb.api.log.Logger

        class ApisFactoryImpl : ApisFactory {
            override fun create(logger: Logger): Apis {
                val data = MutableData()
                val apis = Apis(data, logger)
                ${
                    // Generates lines like: apis.register("/path/to/api") { ctx -> path.to.api.method(ctx)  }
                    apiEntries.joinToString("\n                ") { entry ->
                        """apis.register("${entry.route}") { ctx -> ${entry.fqn}(ctx) }"""
                    }
                }

                ${
                    if (initEntries.isNotEmpty()) {
                        "val initCtx = InitApiContext(apis, data, logger)"
                    }
                    else {
                        ""
                    }
                }
                ${
                    initEntries.joinToString("\n                ") { entry ->
                        """${entry.fqn}(initCtx)"""
                    }
                }
                return apis
            }
        }
    """.trimIndent()
}
