plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()
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
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
