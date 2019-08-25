pluginManagement {

    plugins {
        id("scientifik.mpp") version "0.1.6"
        id("scientifik.jvm") version "0.1.6"
        id("scientifik.atomic") version "0.1.6"
        id("scientifik.publish") version "0.1.6"
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

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "kmath"
include(
    ":kmath-memory",
    ":kmath-core",
//    ":kmath-io",
    ":kmath-coroutines",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-koma",
    ":kmath-prob",
    ":kmath-io",
    ":examples"
)
