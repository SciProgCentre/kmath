pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

//enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "kmath"
include(
    ":kmath-core",
    ":kmath-io",
    ":kmath-coroutines",
    ":kmath-commons",
    ":kmath-koma",
    ":benchmarks"
)
