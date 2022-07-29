plugins {
    id("space.kscience.gradle.mpp")
    id("space.kscience.gradle.native")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmath.kmathCore)
            api(kotlin("test"))
        }
    }
}
