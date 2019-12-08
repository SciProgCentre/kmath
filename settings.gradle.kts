pluginManagement {

    plugins {
        id("scientifik.mpp") version "0.2.5"
        id("scientifik.jvm") version "0.2.5"
        id("scientifik.atomic") version "0.2.5"
        id("scientifik.publish") version "0.2.5"
    }

    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/mipt-npm/scientifik")
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "scientifik.mpp", "scientifik.publish" -> useModule("scientifik:gradle-tools:${requested.version}")
            }
        }
    }
}

rootProject.name = "kmath"
include(
    ":kmath-memory",
    ":kmath-core",
//    ":kmath-io",
    ":kmath-coroutines",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
    ":kmath-koma",
    ":kmath-prob",
    ":kmath-io",
    ":examples"
)
