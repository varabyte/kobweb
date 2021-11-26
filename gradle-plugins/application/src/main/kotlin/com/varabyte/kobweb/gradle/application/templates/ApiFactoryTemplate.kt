package com.varabyte.kobweb.gradle.application.templates

import com.varabyte.kobweb.gradle.application.project.api.ApiData
import com.varabyte.kobweb.gradle.application.project.api.ApiEntry
import com.varabyte.kobweb.gradle.application.project.api.InitApiEntry

fun createApisFactoryImpl(apiData: ApiData): String {
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
                    // Sort values as it makes the generated registration logic easier to follow
                    apiData.apiMethods.sortedBy { entry -> entry.route }.joinToString("\n                ") { entry ->
                        """apis.register("${entry.route}") { ctx -> ${entry.fqn}(ctx) }"""
                    }
                }

                ${
                    if (apiData.initMethods.isNotEmpty()) {
                        "val initCtx = InitApiContext(apis, data, logger)"
                    }
                    else {
                        ""
                    }
                }
                ${
                    apiData.initMethods.joinToString("\n                ") { entry ->
                        """${entry.fqn}(initCtx)"""
                    }
                }
                return apis
            }
        }
    """.trimIndent()
}