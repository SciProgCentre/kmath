plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()

    dependencies {
        api(project(":kmath-core"))
        api(project(":kmath-complex"))
        api(spclibs.kotlinx.coroutines.core)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}