plugins {
    id("ru.mipt.npm.mpp")
}

kotlin.js {
    nodejs { // or `browser`
        testTask {
            useMocha {
                timeout = "0"// mochaTimeout here as string
            }
        }
    }

    browser { // or `browser`
        testTask {
            useMocha {
                timeout = "0"// mochaTimeout here as string
            }
        }
    }
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }

    jvmMain {
        dependencies {
            api("com.github.h0tk3y.betterParse:better-parse:0.4.0")
            implementation("org.ow2.asm:asm:8.0.1")
            implementation("org.ow2.asm:asm-commons:8.0.1")
        }
    }

    jsMain {
        dependencies {
            implementation(npm("binaryen", "98.0.0"))
            implementation(npm("js-base64", "3.6.0"))
            implementation(npm("webassembly", "0.11.0"))
        }
    }
}
