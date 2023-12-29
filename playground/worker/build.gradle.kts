import com.varabyte.kobweb.gradle.worker.util.configAsKobwebWorker

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("com.varabyte.kobweb.worker")
}

group = "playground.worker"
version = "1.0-SNAPSHOT"

kobweb {
    worker {
        name.set("sum-worker")
    }

    kspProcessorDependency.set("com.varabyte.kobweb:worker-processor")
}

kotlin {
    configAsKobwebWorker()
    sourceSets {
        jsMain.dependencies {
            api(libs.kotlinx.serialization.json)
            implementation("com.varabyte.kobweb:kobweb-worker")
            implementation("com.varabyte.kobwebx:kobwebx-serialization-kotlinx")
        }
    }
}
