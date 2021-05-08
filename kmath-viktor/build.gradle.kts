plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

description = "Binding for https://github.com/JetBrains-Research/viktor"

dependencies {
    api(project(":kmath-core"))
    api("org.jetbrains.bio:viktor:1.1.0")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
}
