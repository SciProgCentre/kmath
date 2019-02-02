plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}


kotlin {
    jvm ()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
                api(project(":kmath-coroutines"))
                compileOnly("org.jetbrains.kotlinx:atomicfu-common:0.12.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:atomicfu:0.12.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
//        val jsMain by getting {
//            dependencies {
//                api(kotlin("stdlib-js"))
//            }
//        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
//        mingwMain {
//        }
//        mingwTest {
//        }
    }
}