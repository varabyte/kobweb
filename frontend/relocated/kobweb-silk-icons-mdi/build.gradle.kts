plugins {
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kobwebPublication {
    relocationDetails {
        artifactId.set("silk-icons-mdi")
        message.set("All kobweb-silk-* artifacts have been renamed to silk-*, to emphasize that Silk can be used separately from Kobweb.")
    }

    description.set("[RELOCATED] A collection of Kobweb Silk components that directly wrap Material Design icons")
}
