plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
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
                compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Ver.atomicfuVersion}")
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu:${Ver.atomicfuVersion}")
            }
        }
        val jsMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Ver.atomicfuVersion}")
            }
        }

    }
}

atomicfu {
    variant = "VH"
}