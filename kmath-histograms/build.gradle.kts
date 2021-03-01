plugins {
    id("ru.mipt.npm.gradle.mpp")
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
