plugins {
    id("space.kscience.gradle.mpp")
}

description = "Google tensorflow connector"

kscience {
    jvm()

    jvmMain {
        api(projects.kmathTensors)
        api(libs.tensorflow.core.api)
    }

    jvmTest {
        implementation(libs.tensorflow.core.platform)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}