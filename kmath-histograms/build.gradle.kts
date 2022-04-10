plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
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
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
