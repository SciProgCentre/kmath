rootProject.name = "kmath"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    val toolsVersion: String by extra

    repositories {
        mavenLocal()
        maven("https://repo.kotlin.link")
        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("npmlibs") {
            from("ru.mipt.npm:version-catalog:$toolsVersion")
        }
    }
}

include(
    ":test-utils",
    ":kmath-memory",
    ":kmath-complex",
    ":kmath-core",
    ":kmath-coroutines",
    ":kmath-functions",
    ":test-utils-functions",
    ":kmath-polynomialX",
    ":test-utils-polynomialX",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
    ":kmath-multik",
    ":kmath-tensorflow",
    ":kmath-optimization",
    ":kmath-stat",
    ":kmath-nd4j",
    ":kmath-dimensions",
    ":kmath-for-real",
    ":kmath-geometry",
    ":kmath-ast",
    ":kmath-ejml",
    ":kmath-kotlingrad",
    ":kmath-tensors",
    ":kmath-jupyter",
    ":kmath-symja",
    ":kmath-jafama",
    ":examples",
    ":benchmarks",
)