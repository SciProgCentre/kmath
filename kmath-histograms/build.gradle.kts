plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()
    useCoroutines()
}

//apply(plugin = "kotlinx-atomicfu")

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(spclibs.atomicfu)
        }
    }
    commonTest {
        dependencies {
            implementation(project(":kmath-for-real"))
            implementation(projects.kmath.kmathStat)
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
