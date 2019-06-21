plugins {
    `npm-multiplatform`
}

description = "A proof of concept module for adding typ-safe dimensions to structures"

kotlin.sourceSets {
    commonMain {
        dependencies {
            implementation(kotlin("reflect"))
            api(project(":kmath-core"))
        }
    }
}