plugins {
    id("space.kscience.gradle.jvm")
}

description = "JetBrains Multik connector"

dependencies {
    api(project(":kmath-tensors"))
    api("org.jetbrains.kotlinx:multik-default:0.1.0")
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}