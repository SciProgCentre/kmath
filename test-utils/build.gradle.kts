plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()
    wasm()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmath.kmathCore)
            api(kotlin("test"))
        }
    }
}
