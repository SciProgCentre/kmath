plugins {
    id("scientifik.mpp")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-memory"))
        }
    }
}
