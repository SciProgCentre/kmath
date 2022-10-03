plugins {
    id("space.kscience.gradle.jvm")
}

description = "Binding for https://github.com/JetBrains-Research/viktor"

dependencies {
    api(project(":kmath-core"))
    api("org.jetbrains.bio:viktor:1.2.0")
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
}
