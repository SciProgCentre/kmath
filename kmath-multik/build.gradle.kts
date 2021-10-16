plugins {
    id("ru.mipt.npm.gradle.jvm")
}

description = "JetBrains Multik connector"

dependencies {
    api(project(":kmath-tensors"))
    api("org.jetbrains.kotlinx:multik-api:0.1.0")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}