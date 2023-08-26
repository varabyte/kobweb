package com.varabyte.kobweb.gradle.application.templates

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.varabyte.kobweb.gradle.core.project.backend.BackendData

fun createApisFactoryImpl(backendData: BackendData): String {
    // Final code should look something like:
    //
    // class ApisFactoryImpl : ApisFactory {
    //    override fun create(logger: Logger): Apis {
    //        val data = MutableData()
    //        val apis = Apis(data, logger)
    //        apis.register("/add") { ctx -> example.api.add(ctx) }
    //        apis.register("/remove") { ctx -> example.api.remove(ctx) }
    //        apis.registerStream("/echo") { ctx -> example.api.echo }
    //        apis.registerStream("/chat") { ctx -> example.api.chat }
    //        val initCtx = InitApiContext(apis, data, logger)
    //        example.init(initCtx)
    //        return apis
    //    }
    //  }

    val fileBuilder = FileSpec.builder("", "ApisFactoryImpl").indent(" ".repeat(4))

    val apiPackage = "com.varabyte.kobweb.api"
    val classApis = ClassName(apiPackage, "Apis")
    val classApisFactory = ClassName(apiPackage, "ApisFactory")
    val classMutableData = ClassName("$apiPackage.data", "MutableData")
    val classInitApiContext = ClassName("$apiPackage.init", "InitApiContext")
    val classLogger = ClassName("$apiPackage.log", "Logger")

    fileBuilder.addType(
        TypeSpec.classBuilder("ApisFactoryImpl")
            .addSuperinterface(classApisFactory)
            .addFunction(
                FunSpec.builder("create")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(ParameterSpec("logger", classLogger))
                    .returns(classApis)
                    .addCode(CodeBlock.builder().apply {
                        addStatement("val data = %T()", classMutableData)
                        addStatement("val apis = %T(data, logger)", classApis)
                        backendData.apiMethods.sortedBy { entry -> entry.route }.forEach { entry ->
                            addStatement("apis.register(%S) { ctx -> ${entry.fqn}(ctx) }", entry.route)
                        }
                        backendData.apiStreamMethods.sortedBy { entry -> entry.route }.forEach { entry ->
                            addStatement("apis.registerStream(%S, ${entry.fqn})", entry.route)
                        }
                        if (backendData.initMethods.isNotEmpty()) {
                            addStatement("val initCtx = %T(apis, data, logger)", classInitApiContext)
                            backendData.initMethods.sortedBy { entry -> entry.fqn }.forEach { entry ->
                                addStatement("${entry.fqn}(initCtx)")
                            }
                        }
                        addStatement("")
                        addStatement("return apis")
                    }.build())
                    .build()
            )
            .build()
    )

    return fileBuilder.build().toString()
}
