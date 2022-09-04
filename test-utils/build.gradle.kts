plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmath.kmathCore)
            api(kotlin("test"))
        }
    }
}
