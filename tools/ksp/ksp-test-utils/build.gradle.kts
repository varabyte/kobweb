plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.jvm)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.framework.get()

dependencies {
    api(libs.ksp.processing)
    api(kotlin("test-junit"))
    implementation(libs.ksp.standalone)
    implementation(libs.ksp.standalone.deps)
}
