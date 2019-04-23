pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        //maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu" -> {
                    // Just hardcode version here, 
                    // because anyway different submodules cannot use different versions
                    useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.12.1")
                }
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
    ":kmath-sequential",
    ":benchmarks"
)
