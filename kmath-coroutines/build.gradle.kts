plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()

    dependencies {
        api(projects.kmathCore)
        api(spclibs.kotlinx.coroutines.core)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}