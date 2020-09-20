import ru.mipt.npm.gradle.KSciencePublishPlugin

plugins {
    id("ru.mipt.npm.publish") apply false
}

val kmathVersion: String by extra("0.1.4")
val bintrayRepo: String by extra("scientifik")
val githubProject: String by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "scientifik"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<KSciencePublishPlugin>()
}
