plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

kotlin.js {
    nodejs {
        testTask {
            useMocha().timeout = "5000"
        }
    }

    browser {
        testTask {
            useMocha().timeout = "5000"
        }
    }
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
