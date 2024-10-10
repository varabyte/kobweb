plugins {
    `kotlin-dsl`
}

group = "com.varabyte.kobweb.gradle"
version = "1.0.0-SNAPSHOT"

gradlePlugin {
    plugins {
        create("publishKobwebArtifact") {
            id = "com.varabyte.kobweb.internal.publish"
            implementationClass = "com.varabyte.kobweb.gradle.publish.KobwebPublishPlugin"
        }
    }
}
