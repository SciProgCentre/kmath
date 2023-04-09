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
            api(projects.kmathCoroutines)
            //implementation(spclibs.atomicfu)
        }
    }

    getByName("jvmMain") {
        dependencies {
            api("org.apache.commons:commons-rng-sampling:1.3")
            api("org.apache.commons:commons-rng-simple:1.3")
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}