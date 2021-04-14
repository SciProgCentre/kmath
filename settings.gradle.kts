pluginManagement {
    repositories {
        maven("https://repo.kotlin.link")
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }

    val toolsVersion = "0.9.5-dev"
    val kotlinVersion = "1.5.0-M2"

    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlinx.benchmark") version "0.3.0"
        id("ru.mipt.npm.gradle.project") version toolsVersion
        id("ru.mipt.npm.gradle.mpp") version toolsVersion
        id("ru.mipt.npm.gradle.jvm") version toolsVersion
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
    ":examples"
)
