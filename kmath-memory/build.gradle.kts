plugins {
    kotlin("multiplatform")
}

val ioVersion: String by rootProject.extra


kotlin {
    jvm()
    js()
//        mingwMain {
//        }
//        mingwTest {
//        }
    
}