plugins {
    `kotlin-dsl`
}

group = "com.varabyte.kobweb"

dependencies {
    implementation(libs.compose.compiler.plugin)
    implementation(libs.gradle.publish.plugin)
    implementation(libs.kotlin.sam.receiver.plugin)
    implementation(libs.kotlin.assignment.plugin)
}
