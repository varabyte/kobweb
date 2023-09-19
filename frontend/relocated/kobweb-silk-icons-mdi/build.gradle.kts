plugins {
    `maven-publish`
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

publishing {
    publications {
        create<MavenPublication>("relocation") {
            pom {
                distributionManagement {
                    relocation {
                        artifactId.set("silk-icons-mdi")
                        message.set("All kobweb-silk-* artifacts have been renamed to silk-*, to emphasize that Silk can be used separately from Kobweb.")
                    }
                }
            }
        }
    }
}
