plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()

    useCoroutines()

    commonMain {
        api(projects.kmathCoroutines)
        //implementation(spclibs.atomicfu)
    }

    jvmMain {
        api(libs.commons.rng.simple)
        api(libs.commons.rng.sampling)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}