plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

val LUCIDE_VERSION = "1.69.0"
val ICONS_JSON_FILE_NAME = "lucide-icons.json"
val GENERATED_SRC_DIR = "build/generated/icons/src/jsMain/kotlin"

val iconsCodegen = configurations.dependencyScope("iconsCodegen")
val iconsCodegenClasspath = configurations.resolvable("iconsCodegenClasspath") {
    extendsFrom(iconsCodegen.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}

dependencies {
    iconsCodegen(projects.tools.iconsCodegen)
}

tasks.register<JavaExec>("fetchIcons") {
    group = "Icon"
    description = "Download information that lets us create $ICONS_JSON_FILE_NAME, which is used to generate icons."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    args("fetch", "lucide", "--version", LUCIDE_VERSION, "--data", ICONS_JSON_FILE_NAME)
}

val generateIconsTask = tasks.register<JavaExec>("generateIcons") {
    group = "Icon"
    description = "Generate Kotlin bindings for Lucide icons."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    // Paths are relative to the project directory (the default working directory) so the build cache can be shared
    // across machines
    args(
        "generate", "lucide",
        "--data", ICONS_JSON_FILE_NAME,
        "--out", GENERATED_SRC_DIR,
        "--package", "com.varabyte.kobweb.silk.components.icons.lucide",
        "--visibility", "public",
    )

    inputs.file(ICONS_JSON_FILE_NAME).withPathSensitivity(PathSensitivity.NONE)
    outputs.dir(GENERATED_SRC_DIR)
    outputs.cacheIf { true }
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain {
            kotlin.srcDir(generateIconsTask)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.html.core)

                api(projects.frontend.kobwebCompose)
                api(projects.frontend.silkWidgets)
            }
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Lucide)")
    artifactId.set("silk-icons-lucide")
    description.set("A collection of composables that directly wrap Lucide icons.")
}
