plugins {
    id("space.kscience.gradle.mpp")
}

description = "JetBrains Multik connector"

kscience {
    jvm()
    js()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.kmathTensors)
                api(libs.multik.core)
            }
        }
        commonTest {
            dependencies {
                api(libs.multik.default)
            }
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}