plugins {
    id("ru.mipt.npm.mpp")
}

kotlin.js {
    nodejs {
        testTask {
            useMocha().timeout = "0"
        }
    }

    browser {
        testTask {
            useMocha().timeout = "0"
        }
    }
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }

    jsMain {
        dependencies {
            implementation(npm("astring", "1.4.3"))
        }
    }

    jvmMain {
        dependencies {
            api("com.github.h0tk3y.betterParse:better-parse:0.4.0")
            implementation("org.ow2.asm:asm:8.0.1")
            implementation("org.ow2.asm:asm-commons:8.0.1")
        }
    }
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}