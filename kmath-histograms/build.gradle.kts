plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
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
        }
    }
}

readme {
    this.maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
