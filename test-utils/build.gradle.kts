plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmath.kmathCore)
            api(kotlin("test"))
            api(spclibs.logback.classic)
        }
    }
}
