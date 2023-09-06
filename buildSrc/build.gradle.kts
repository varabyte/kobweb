import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.varabyte.kobweb.gradle"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// kotlin-dsl uses 1.4 by default, but this spawns warnings at build time.
// See also: https://handstandsam.com/2022/04/13/using-the-kotlin-dsl-gradle-plugin-forces-kotlin-1-4-compatibility/
afterEvaluate {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_1_5)
            languageVersion.set(KotlinVersion.KOTLIN_1_5)
        }
    }
}

gradlePlugin {
    plugins {
        create("publishKobwebArtifact") {
            id = "com.varabyte.kobweb.internal.publish"
            implementationClass = "com.varabyte.kobweb.gradle.publish.KobwebPublishPlugin"
        }
    }
}
