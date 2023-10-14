package com.varabyte.kobweb.ksp.backend

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAnnotationsByType
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
import com.varabyte.kobweb.ksp.common.API_STREAM_FQN
import com.varabyte.kobweb.ksp.common.API_STREAM_SIMPLE_NAME
import com.varabyte.kobweb.ksp.common.DISPOSE_API_FQN
import com.varabyte.kobweb.ksp.common.INIT_API_FQN
import com.varabyte.kobweb.ksp.common.PACKAGE_MAPPING_API_FQN
import com.varabyte.kobweb.ksp.common.getPackageMappings
import com.varabyte.kobweb.ksp.common.processRoute
import com.varabyte.kobweb.ksp.util.getAnnotationsByName
import com.varabyte.kobweb.ksp.util.nameWithoutExtension
import com.varabyte.kobweb.project.backend.ApiEntry
import com.varabyte.kobweb.project.backend.ApiStreamEntry
import com.varabyte.kobweb.project.backend.BackendData
import com.varabyte.kobweb.project.backend.DisposeApiEntry
import com.varabyte.kobweb.project.backend.InitApiEntry
import com.varabyte.kobweb.project.backend.assertValid
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackendProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val genFile: String,
    private val qualifiedApiPackage: String,
) : SymbolProcessor {
    private lateinit var initMethods: List<InitApiEntry>
    private lateinit var apiMethods: List<ApiEntry>
    private lateinit var disposeMethods: List<DisposeApiEntry>
    private val apiStreams = mutableListOf<ApiStreamEntry>()

    // fqPkg to subdir, e.g. "api.id._as._int" to "int"
    private lateinit var packageMappings: Map<String, String>

    // We track all files we depend on so that ksp can perform smart recompilation
    // Even though our output is aggregating so generally requires full reprocessing, this at minimum means processing
    // will be skipped if the only change is deleted file(s) that we do not depend on.
    private val fileDependencies = mutableListOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        initMethods = resolver.getSymbolsWithAnnotation(INIT_API_FQN).map { annotatedFun ->
            fileDependencies.add(annotatedFun.containingFile!!)
            val name = (annotatedFun as KSFunctionDeclaration).qualifiedName!!.asString()
            InitApiEntry(name)
        }.toList()

        disposeMethods = resolver.getSymbolsWithAnnotation(DISPOSE_API_FQN).map { annotatedFun ->
            fileDependencies.add(annotatedFun.containingFile!!)
            val name = (annotatedFun as KSFunctionDeclaration).qualifiedName!!.asString()
            DisposeApiEntry(name)
        }.toList()

        val allFiles = resolver.getAllFiles()

        // package mapping must be processed before api methods & streams
        packageMappings = allFiles.flatMap { file ->
            getPackageMappings(file, qualifiedApiPackage, PACKAGE_MAPPING_API_FQN, logger).toList()
                .also { if (it.isNotEmpty()) fileDependencies.add(file) }
        }.toMap()

        apiMethods = resolver.getSymbolsWithAnnotation(API_FQN)
            .filterIsInstance<KSFunctionDeclaration>() // @Api for stream properties is handled separately
            .mapNotNull { annotatedFun ->
                processApiFun(annotatedFun, qualifiedApiPackage, packageMappings, logger)
                    ?.also { fileDependencies.add(annotatedFun.containingFile!!) }
            }.toList()

        val visitor = ApiVisitor()
        allFiles.forEach { file ->
            file.accept(visitor, Unit)
        }

        return emptyList()
    }

    private inner class ApiVisitor : KSVisitorVoid() {
        @OptIn(KspExperimental::class)
        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            val type = property.type.toString()
            if (type != API_STREAM_SIMPLE_NAME) return

            val expensiveFullType = property.type.resolve().declaration.qualifiedName?.asString()
            if (expensiveFullType != API_STREAM_FQN) return

            val propertyName = property.simpleName.asString()
            val topLevelSuppression = "TOP_LEVEL_API_STREAM"
            val privateSuppression = "PRIVATE_API_STREAM"
            if (property.parent !is KSFile) {
                if (property.getAnnotationsByType(Suppress::class).none { topLevelSuppression in it.names }) {
                    logger.warn(
                        "Not registering ApiStream `val $propertyName`, as only top-level component styles are supported at this time. Although fixing this is recommended, you can manually register your API Stream inside an @InitSilk block instead (`ctx.apis.register($propertyName)`). Suppress this message by adding a `@Suppress(\"$topLevelSuppression\")` annotation.",
                        property
                    )
                }
                return
            }
            if (!property.isPublic()) {
                if (property.getAnnotationsByType(Suppress::class).none { privateSuppression in it.names }) {
                    logger.warn(
                        "Not registering ApiStream `val $propertyName`, as it is not public. Although fixing this is recommended, you can manually register your API Stream inside an @InitSilk block instead (`ctx.apis.register($propertyName)`). Suppress this message by adding a `@Suppress(\"$privateSuppression\")` annotation.",
                        property
                    )
                }
                return
            }
            fileDependencies.add(property.containingFile!!)

            val routeOverride = property.getAnnotationsByName(API_FQN)
                .firstNotNullOfOrNull { it.arguments.firstOrNull()?.value?.toString() }

            val resolvedRoute = processRoute(
                pkg = property.packageName.asString(),
                slugFromFile = property.containingFile!!.nameWithoutExtension.lowercase(),
                routeOverride = routeOverride,
                qualifiedPackage = qualifiedApiPackage,
                packageMappings = packageMappings,
                supportDynamicRoute = false,
            )

            apiStreams.add(ApiStreamEntry(property.qualifiedName!!.asString(), resolvedRoute))
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.declarations.forEach { it.accept(this, Unit) }
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }
    }

    override fun finish() {
        val backendData = BackendData(initMethods, apiMethods, apiStreams, disposeMethods).also {
            it.assertValid(throwError = { msg -> logger.error(msg) })
        }

        val (path, extension) = genFile.split('.')
        codeGenerator.createNewFileByPath(
            Dependencies(aggregating = true, *fileDependencies.toTypedArray()),
            path = path,
            extensionName = extension,
        ).writer().use { writer ->
            writer.write(Json.encodeToString(backendData))
        }
    }
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
            pkg = annotatedFun.packageName.asString(),
            slugFromFile = file.nameWithoutExtension.lowercase(),
            routeOverride = routeOverride,
            qualifiedPackage = qualifiedApiPackage,
            packageMappings = packageMappings,
            supportDynamicRoute = false,
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
