pluginManagement {

    plugins {
        id("scientifik.mpp") version "0.3.1"
        id("scientifik.jvm") version "0.3.1"
        id("scientifik.atomic") version "0.3.1"
        id("scientifik.publish") version "0.3.1"
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
                "scientifik.mpp", "scientifik.jvm", "scientifik.publish" -> useModule("scientifik:gradle-tools:${requested.version}")
            }
        }
    }
}

rootProject.name = "kmath"
include(
    ":kmath-memory",
    ":kmath-core",
    ":kmath-functions",
//    ":kmath-io",
    ":kmath-coroutines",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
    ":kmath-koma",
    ":kmath-prob",
    ":kmath-io",
    ":kmath-dimensions",
    ":examples"
)
