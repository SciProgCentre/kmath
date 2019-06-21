pluginManagement {
    repositories {
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/orangy/maven")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
                "kotlin-multiplatform" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                "kotlin2js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                //"org.jetbrains.kotlin.frontend" -> useModule("org.jetbrains.kotlin:kotlin-frontend-plugin:0.0.45")
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
    ":kmath-dimensions",
    ":examples"
)
