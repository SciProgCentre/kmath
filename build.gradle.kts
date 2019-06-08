val kmathVersion by extra("0.1.3-dev-1")

allprojects {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    group = "scientifik"
    version = kmathVersion
}

subprojects {
    apply(plugin = "dokka-publish")
    if (name.startsWith("kmath")) {
        apply(plugin = "npm-publish")
    }
}
