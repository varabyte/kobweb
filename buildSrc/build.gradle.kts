plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("publishKobwebArtifact") {
            id = "com.varabyte.kobweb.publish"
            implementationClass = "com.varabyte.kobweb.plugins.publish.KobwebPublishPlugin"
        }
    }
}