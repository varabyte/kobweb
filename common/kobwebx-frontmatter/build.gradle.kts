plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.truthish)
            implementation(kotlin("test"))
        }
    }
}

kobwebPublication {
    artifactName.set("Kobwebx Frontmatter")
    artifactId.set("kobwebx-frontmatter")
    description.set("Frontmatter support shared between the Markdown frontend artifact and the Kobweb Markdown plugin")
}
