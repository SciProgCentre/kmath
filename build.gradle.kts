plugins {
    id("scientifik.mpp") version "0.1.4" apply false
    id("scientifik.publish") version "0.1.4" apply false
    id("kotlinx-atomicfu") version "0.12.9" apply false
}

val kmathVersion by extra("0.1.4-dev-1")

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