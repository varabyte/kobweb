import com.varabyte.kobweb.gradle.library.util.configAsKobwebLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("com.varabyte.kobweb.library")
}

group = "playground.sitelib"
version = "1.0-SNAPSHOT"

kobweb {
    kspProcessorDependency.set("com.varabyte.kobweb:site-processors")
    library {
        icons {
            public.set(true)
            fa { version.set("7.+") }
            lucide { version.set("1.+") }
            // Google's Material icons have no release tags, so their versions are commit SHAs and can't use a prefix
            // like "1.+". Leaving the version unset means "latest", i.e. whatever their repo's main branch has when
            // fetched.
            @Suppress("MaterialIcons")
            mdi()
            ms()
        }
    }
}

kotlin {
    configAsKobwebLibrary(includeServer = true)

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation("com.varabyte.kobweb:kobweb-core")
            implementation("com.varabyte.kobweb:kobweb-silk")
            implementation(project(":workers:sum"))
        }
        jvmMain.dependencies {
            implementation("com.varabyte.kobweb:kobweb-api")
        }
    }
}
