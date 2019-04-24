plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu") version "0.12.4"
}

val atomicfuVersion: String by rootProject.extra

kotlin {
    jvm ()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
                api(project(":kmath-coroutines"))
                compileOnly("org.jetbrains.kotlinx:atomicfu-common:$atomicfuVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu-js:$atomicfuVersion")
            }
        }

    }
}

atomicfu {
    variant = "VH"
}