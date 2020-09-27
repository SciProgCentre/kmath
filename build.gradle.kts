plugins {
    id("ru.mipt.npm.project")
}

val kmathVersion: String by extra("0.2.0-dev-2")
val bintrayRepo: String by extra("kscience")
val githubProject: String by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "kscience.kmath"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<ru.mipt.npm.gradle.KSciencePublishPlugin>()
}

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}
