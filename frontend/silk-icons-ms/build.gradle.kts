plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

// A commit in https://github.com/google/material-design-icons, which has no release tags
val MS_VERSION = "49d4db35df873165d6bd6ba09b063c7dafbac2f4"
val ICON_LIST_FILE_NAME = "ms-icon-list.txt"
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
    description = "Download information that lets us create $ICON_LIST_FILE_NAME, which is used to generate icons."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    args("fetch", "ms", "--version", MS_VERSION, "--icons", ICON_LIST_FILE_NAME)
}

val generateIconsTask = tasks.register<JavaExec>("generateIcons") {
    group = "Icon"
    description = "Generate Kotlin bindings for Material Symbols."

    classpath = iconsCodegenClasspath.get()
    mainClass.set("com.varabyte.kobweb.icons.codegen.MainKt")
    // Paths are relative to the project directory (the default working directory) so the build cache can be shared
    // across machines
    args(
        "generate", "ms",
        "--icons", ICON_LIST_FILE_NAME,
        "--out", GENERATED_SRC_DIR,
        "--package", "com.varabyte.kobweb.silk.components.icons.ms",
        "--visibility", "public",
    )

    inputs.file(ICON_LIST_FILE_NAME).withPathSensitivity(PathSensitivity.NONE)
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
            }
        }
    }
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Material Symbols)")
    artifactId.set("silk-icons-ms")
    description.set("A collection of composables that directly wrap Google Material Symbols.")
}
