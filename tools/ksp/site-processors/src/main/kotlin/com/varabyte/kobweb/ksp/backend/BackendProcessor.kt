package com.varabyte.kobweb.ksp.backend

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.varabyte.kobweb.ksp.common.API_FQN
import com.varabyte.kobweb.ksp.common.API_INTERCEPTOR_FQN
import com.varabyte.kobweb.ksp.common.API_STREAM_FQN
import com.varabyte.kobweb.ksp.common.API_STREAM_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.INIT_API_FQN
import com.varabyte.kobweb.ksp.common.PACKAGE_MAPPING_API_FQN
import com.varabyte.kobweb.ksp.common.getPackageMappings
import com.varabyte.kobweb.ksp.common.processRoute
import com.varabyte.kobweb.ksp.frontend.FrontendProcessor
import com.varabyte.kobweb.ksp.symbol.getAnnotationsByName
import com.varabyte.kobweb.ksp.symbol.resolveQualifiedName
import com.varabyte.kobweb.ksp.symbol.suppresses
import com.varabyte.kobweb.project.backend.ApiEntry
import com.varabyte.kobweb.project.backend.ApiStreamEntry
import com.varabyte.kobweb.project.backend.BackendData
import com.varabyte.kobweb.project.backend.InitApiEntry
import com.varabyte.kobweb.project.backend.assertValid
import com.varabyte.kobweb.project.frontend.FrontendData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackendProcessor(
    private val isLibrary: Boolean,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    private val qualifiedApiPackage: String,
) : SymbolProcessor {
    private val apiVisitor = ApiVisitor()

    private val initMethods = mutableListOf<InitApiEntry>()

    private val apiMethodsDeclarations = mutableListOf<KSFunctionDeclaration>()
    private val apiStreamsDeclarations = mutableListOf<KSPropertyDeclaration>()

    // fqPkg to subdir, e.g. "api.id._as._int" to "int"
    private val packageMappings = mutableMapOf<String, String>()

    // We track all files we depend on so that ksp can perform smart recompilation
    // Even though our output is aggregating so generally requires full reprocessing, this at minimum means processing
    // will be skipped if the only change is deleted file(s) that we do not depend on.
    private val fileDependencies = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        initMethods += resolver.getSymbolsWithAnnotation(INIT_API_FQN).map { annotatedFun ->
            fileDependencies.add(annotatedFun.containingFile!!)
            val name = (annotatedFun as KSFunctionDeclaration).qualifiedName!!.asString()
            InitApiEntry(name)
        }

        if (isLibrary) {
            resolver.getSymbolsWithAnnotation(API_INTERCEPTOR_FQN).toList().forEach { apiInterceptorMethod ->
                logger.error(
                    "@ApiInterceptor functions cannot be defined in library projects.",
                    apiInterceptorMethod
                )
            }
        }

        val newFiles = resolver.getNewFiles()

        // package mapping must be processed before api methods & streams
        packageMappings += newFiles.flatMap { file ->
            getPackageMappings(file, qualifiedApiPackage, PACKAGE_MAPPING_API_FQN, logger).toList()
                .also { if (it.isNotEmpty()) fileDependencies.add(file) }
        }

        apiMethodsDeclarations += resolver.getSymbolsWithAnnotation(API_FQN)
            .filterIsInstance<KSFunctionDeclaration>() // @Api for stream properties is handled separately

        newFiles.forEach { file ->
            file.accept(apiVisitor, Unit)
        }

        return emptyList()
    }

    private inner class ApiVisitor : KSVisitorVoid() {
        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            val type = property.type.toString()
            if (type != API_STREAM_SIMPLE_NAME) return

            if (property.type.resolveQualifiedName() != API_STREAM_FQN) return

            val propertyName = property.simpleName.asString()
            val topLevelSuppression = "TOP_LEVEL_API_STREAM"
            val privateSuppression = "PRIVATE_API_STREAM"
            if (property.parent !is KSFile) {
                if (!property.suppresses(topLevelSuppression)) {
                    logger.warn(
                        "Not registering ApiStream `val $propertyName`, as only top-level API streams are supported at this time. Although fixing this is recommended, you can manually register your API Stream inside an @InitApi block instead (`ctx.apis.registerStream(\"route\", $propertyName)`). Suppress this message by adding a `@Suppress(\"$topLevelSuppression\")` annotation.",
                        property
                    )
                }
                return
            }
            if (!property.isPublic()) {
                if (!property.suppresses(privateSuppression)) {
                    logger.warn(
                        "Not registering ApiStream `val $propertyName`, as it is not public. Although fixing this is recommended, you can manually register your API Stream inside an @InitApi block instead (`ctx.apis.registerStream(\"route\", $propertyName)`). Suppress this message by adding a `@Suppress(\"$privateSuppression\")` annotation.",
                        property
                    )
                }
                return
            }
            fileDependencies.add(property.containingFile!!)
            apiStreamsDeclarations += property
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }
    }

    /**
     * Get the finalized metadata acquired over all rounds of processing.
     *
     * This function should only be called from [SymbolProcessor.finish] as it relies on all rounds of processing being
     * complete.
     *
     * @return A [Result] containing the finalized [FrontendData] and the file dependencies that should be
     * passed in when using KSP's [CodeGenerator] to store the data.
     */
    fun getProcessorResult(): Result {
        // api declarations must be processed at the end, as they rely on package mappings,
        // which may be populated over several rounds
        val apiMethods = apiMethodsDeclarations.mapNotNull { annotatedFun ->
            processApiFun(annotatedFun, qualifiedApiPackage, packageMappings, logger)
                ?.also { fileDependencies.add(annotatedFun.containingFile!!) }
        }
        val apiStreams = apiStreamsDeclarations.map { property ->
            val routeOverride = property.getAnnotationsByName(API_FQN)
                .firstNotNullOfOrNull { it.arguments.firstOrNull()?.value?.toString() }

            val resolvedRoute = processRoute(
                packageRoot = qualifiedApiPackage,
                pkg = property.packageName.asString(),
                file = property.containingFile!!,
                routeOverride = routeOverride,
                packageMappings = packageMappings,
                supportEmptyDynamicSegments = false,
            )

            ApiStreamEntry(property.qualifiedName!!.asString(), resolvedRoute)
        }

        val backendData = BackendData(initMethods, apiMethods, apiStreams).also {
            it.assertValid(throwError = { msg -> logger.error(msg) })
        }

        return Result(backendData, fileDependencies)
    }


    override fun finish() {
        val (path, extension) = genFile.split('.')
        val result = getProcessorResult()
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(Json.encodeToString(result.data))
        }
    }

    /**
     * Represents the result of [FrontendProcessor]'s processing, consisting of the generated [FrontendData] and the
     * files that contained relevant declarations.
     */
    data class Result(val data: BackendData, val fileDependencies: Set<KSFile>)
}

private fun processApiFun(
    annotatedFun: KSFunctionDeclaration,
    qualifiedApiPackage: String,
    packageMappings: Map<String, String>,
    logger: KSPLogger,
): ApiEntry? {
    val apiAnnotation = annotatedFun.getAnnotationsByName(API_FQN).first()
    val currPackage = annotatedFun.packageName.asString()
    val file = annotatedFun.containingFile ?: error("Symbol does not come from a source file")
    val routeOverride = apiAnnotation.arguments.first().value?.toString()?.takeIf { it.isNotBlank() }

    return if (routeOverride?.startsWith("/") == true || currPackage.startsWith(qualifiedApiPackage)) {
        val resolvedRoute = processRoute(
            packageRoot = qualifiedApiPackage,
            pkg = annotatedFun.packageName.asString(),
            file = file,
            routeOverride = routeOverride,
            packageMappings = packageMappings,
            supportEmptyDynamicSegments = false,
        )
        ApiEntry(annotatedFun.qualifiedName!!.asString(), resolvedRoute)
    } else {
        val funName = annotatedFun.simpleName.asString()
        val annotationName = apiAnnotation.shortName.asString()
        logger.warn(
            "Skipped over `@$annotationName fun ${funName}`. It is defined under package `$currPackage` but must exist under `$qualifiedApiPackage`.",
            annotatedFun
        )
        null
    }
}
