pluginManagement {
    repositories {
        maven("https://repo.kotlin.link")
        mavenCentral()
        gradlePluginPortal()
    }

    val toolsVersion = "0.10.1"
    val kotlinVersion = "1.5.21"

    plugins {
        id("ru.mipt.npm.gradle.project") version toolsVersion
        id("ru.mipt.npm.gradle.mpp") version toolsVersion
        id("ru.mipt.npm.gradle.jvm") version toolsVersion
        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlinx.benchmark") version "0.3.1"
        kotlin("jupyter.api") version "0.10.0-131-1"
    }
}

rootProject.name = "kmath"

include(
    ":kmath-memory",
    ":kmath-complex",
    ":kmath-core",
    ":kmath-coroutines",
    ":kmath-functions",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
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
