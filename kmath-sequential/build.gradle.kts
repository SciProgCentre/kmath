plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

val atomicfuVersion: String by rootProject.extra

kotlin {
    jvm ()
    //js()

    sourceSets.invoke {
        commonMain {
            dependencies {
                api(project(":kmath-core"))
                api(project(":kmath-coroutines"))
                compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Ver.atomicfuVersion}")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        "jvmMain" {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu:${Ver.atomicfuVersion}")
            }
        }
        "jvmTest" {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
//        val jsMain by getting {
//            dependencies {
//                compileOnly("org.jetbrains.kotlinx:atomicfu-js:$atomicfuVersion")
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }

    }
}

atomicfu {
    variant = "VH"
}
