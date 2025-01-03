plugins {
    `kotlin-dsl`
}

group = "com.varabyte.kobweb.gradle"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(libs.dokka.plugin) // dokka used to generate javadoc jars for kotlin multiplatform projects
    implementation(libs.kotlin.multiplatform.plugin)
}

gradlePlugin {
    plugins {
        create("publishKobwebArtifact") {
            id = "com.varabyte.kobweb.internal.publish"
            implementationClass = "com.varabyte.kobweb.gradle.publish.KobwebPublishPlugin"
        }
    }
}
