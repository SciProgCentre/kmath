plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
            implementation(spclibs.atomicfu)
        }
    }

    jvmMain {
        dependencies {
            api("org.apache.commons:commons-rng-sampling:1.3")
            api("org.apache.commons:commons-rng-simple:1.3")
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}