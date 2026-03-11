plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()

    dependencies {
        api(projects.kmathCore)
    }

    dependencies(jvmMain) {
        api(kotlin("reflect"))
    }
}

description = "A proof of concept module for adding type-safe dimensions to structures"

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
