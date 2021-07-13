@file:Suppress("UNUSED_VARIABLE")

plugins {
    `kotlin-dsl`
    kotlin("kapt")
}

dependencies {
    compileOnly("com.google.auto.service:auto-service-annotations:1.0")
    compileOnly(kotlin("gradle-plugin"))
    kapt("com.google.auto.service:auto-service:1.0")
}

gradlePlugin {
    val kmathTorchScriptPlugin by plugins.registering {
        id = "space.kscience.kmath.torchscript.torchscript-compiler-plugin"
        implementationClass = "space.kscience.kmath.torchscript.gradle.TorchScriptKotlinGradleSubplugin"
    }
}
