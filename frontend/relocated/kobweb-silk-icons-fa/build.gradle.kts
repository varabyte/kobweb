plugins {
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kobwebPublication {
    relocationDetails {
        groupId.set("com.varabyte.kobwebx")
        artifactId.set("silk-icons-fa")
        message.set("All kobweb-silk-* artifacts have been renamed to silk-*, to emphasize that Silk can be used separately from Kobweb.")
    }

    description.set("[RELOCATED] A collection of Kobweb Silk components that directly wrap Font Awesome icons")
}
