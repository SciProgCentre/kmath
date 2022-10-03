plugins {
    id("space.kscience.gradle.jvm")
}

description = "Google tensorflow connector"

dependencies {
    api(project(":kmath-tensors"))
    api("org.tensorflow:tensorflow-core-api:0.4.0")
    testImplementation("org.tensorflow:tensorflow-core-platform:0.4.0")
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}