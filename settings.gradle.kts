pluginManagement {
    val toolsVersion = "0.6.0-dev-5"

    plugins {
        id("kotlinx.benchmark") version "0.2.0-dev-20"
        id("ru.mipt.npm.mpp") version toolsVersion
        id("ru.mipt.npm.jvm") version toolsVersion
        id("ru.mipt.npm.publish") version toolsVersion
        kotlin("plugin.allopen") version "1.4.20-dev-3898-14"
    }

    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/mipt-npm/scientifik")
        maven("https://dl.bintray.com/mipt-npm/dev")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/kotlin-dev/")
    }
}

rootProject.name = "kmath"

include(
    ":kmath-memory",
    ":kmath-core",
    ":kmath-functions",
    ":kmath-coroutines",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
    ":kmath-koma",
    ":kmath-prob",
    ":kmath-io",
    ":kmath-dimensions",
    ":kmath-for-real",
    ":kmath-geometry",
//    ":kmath-ast",
    ":examples"
)
