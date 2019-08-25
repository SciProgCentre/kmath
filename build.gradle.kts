plugins {
    id("scientifik.publish") version "0.1.6" apply false
}

val kmathVersion by extra("0.1.4-dev")

val bintrayRepo by extra("scientifik")
val githubProject by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }

    group = "scientifik"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) {
        apply(plugin = "scientifik.publish")
    }
}