plugins {
    kotlin("multiplatform")
}

val ioVersion: String by rootProject.extra


kotlin {
    jvm()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kmath-memory"))
            }
        }
//        mingwMain {
//        }
//        mingwTest {
//        }
    }
}