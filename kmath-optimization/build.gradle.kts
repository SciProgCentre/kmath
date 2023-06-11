plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()
}

kotlin.sourceSets {

    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
            api(spclibs.atomicfu)
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}
