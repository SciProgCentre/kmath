plugins {
    `multiplatform-config`
    id("kotlinx-atomicfu") version Versions.atomicfuVersion
}



kotlin {
    jvm ()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
                api(project(":kmath-coroutines"))
                compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Versions.atomicfuVersion}")
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu:${Versions.atomicfuVersion}")
            }
        }
        val jsMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Versions.atomicfuVersion}")
            }
        }

    }
}

atomicfu {
    variant = "VH"
}