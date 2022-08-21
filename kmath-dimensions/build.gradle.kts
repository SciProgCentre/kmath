plugins {
    id("space.kscience.gradle.mpp")
    id("space.kscience.gradle.native")
}

description = "A proof of concept module for adding type-safe dimensions to structures"

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }

    jvmMain {
        dependencies {
            api(kotlin("reflect"))
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
