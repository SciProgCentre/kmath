plugins {
    id("scientifik.mpp")
}

description = "A proof of concept module for adding typ-safe dimensions to structures"

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }

    jvmMain{
        dependencies{
            api(kotlin("reflect"))
        }
    }
}