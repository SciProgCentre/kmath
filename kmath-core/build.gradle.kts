plugins {
    id("multiplatform-config")
}

val ioVersion: String by rootProject.extra


kotlin {
    jvm()
    js()

    sourceSets {
        commonMain {
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
