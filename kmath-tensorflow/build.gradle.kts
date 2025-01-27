plugins {
    id("space.kscience.gradle.jvm")
}

description = "Google tensorflow connector"

dependencies {
    api(projects.kmathTensors)
    api(libs.tensorflow.core.api)
    testImplementation(libs.tensorflow.core.platform)
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}