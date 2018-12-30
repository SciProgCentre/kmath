pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}

//enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "kmath"
include(
        ":kmath-core",
        ":kmath-io",
        ":kmath-coroutines",
        ":benchmarks"
)
