import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Shared setup for the `silk-icons-*` modules, which generate their Kotlin bindings with `tools/icons/codegen`.
// Each module says which icon set it wraps in a `silkIcons { ... }` block.

abstract class SilkIconsExtension {
    /** The icon set's id in icons-codegen, e.g. "fa" or "lucide". Also the last segment of the generated package. */
    abstract val iconSet: Property<String>

    /** The upstream version to fetch, e.g. a release tag or commit. */
    abstract val version: Property<String>

    /** The checked-in data file, relative to the project directory, that fetching writes and generation reads. */
    abstract val iconsFile: Property<String>
}

/** Passes codegen arguments to a [JavaExec] task while tracking them as task inputs, which a plain lambda would not. */
class CodegenArguments(@get:Input val args: Provider<List<String>>) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = args.get()
}

val silkIcons = extensions.create<SilkIconsExtension>("silkIcons")

val generatedSrcDir = "build/generated/icons/src/jsMain/kotlin"
val generatedMetadataDir = "build/generated/kobweb/icons/metadata"
val iconsPackageName = silkIcons.iconSet.map { "com.varabyte.kobweb.silk.components.icons.$it" }

val iconsCodegen = configurations.dependencyScope("iconsCodegen")
val iconsCodegenClasspath = configurations.resolvable("iconsCodegenClasspath") {
    extendsFrom(iconsCodegen.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}

dependencies {
    iconsCodegen(project(":tools:icons:codegen"))
}

tasks.register<JavaExec>("fetchIcons") {
    group = "Icon"
    description = "Download upstream icon data into the icons file that generateIcons reads."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    argumentProviders.add(CodegenArguments(provider {
        listOf(
            "fetch", silkIcons.iconSet.get(),
            "--version", silkIcons.version.get(),
            "--icons", silkIcons.iconsFile.get(),
        )
    }))
}

val generateIconsTask = tasks.register<JavaExec>("generateIcons") {
    group = "Icon"
    description = "Generate Kotlin bindings for this module's icons."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    // Paths are relative to the project directory (the default working directory) so the build cache can be shared
    // across machines
    argumentProviders.add(CodegenArguments(provider {
        listOf(
            "generate", silkIcons.iconSet.get(),
            "--icons", silkIcons.iconsFile.get(),
            "--out", generatedSrcDir,
            "--package", iconsPackageName.get(),
        )
    }))

    inputs.file(silkIcons.iconsFile).withPathSensitivity(PathSensitivity.NONE)
    outputs.dir(generatedSrcDir)
    outputs.cacheIf { true }
}

val generateIconsMetadataTask = tasks.register<JavaExec>("generateIconsMetadata") {
    group = "Icon"
    description = "Generate the icons.json file that tells apps which stylesheet this module's icons need."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    argumentProviders.add(CodegenArguments(provider {
        listOf(
            "metadata", silkIcons.iconSet.get(),
            "--version", silkIcons.version.get(),
            "--out", generatedMetadataDir,
        )
    }))

    outputs.dir(generatedMetadataDir)
    outputs.cacheIf { true }
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    // Type-safe `libs` accessors aren't generated for precompiled script plugins, so look libraries up by name
    val libs = the<VersionCatalogsExtension>().named("libs")

    configure<KotlinMultiplatformExtension> {
        js {
            browser()
        }

        sourceSets.named("jsMain") {
            kotlin.srcDir(generateIconsTask)
            resources.srcDir(generateIconsMetadataTask)
            dependencies {
                implementation(libs.findLibrary("compose-runtime").get())
                implementation(libs.findLibrary("compose-html-core").get())

                api(project(":frontend:kobweb-compose"))
            }
        }
    }
}
