plugins {
    id("multiplatform-config")
}

kotlin {
    jvm()
    js()

    sourceSets.invoke {
        commonMain {
            dependencies {
                api(project(":kmath-core"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Ver.coroutinesVersion}")
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
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Ver.coroutinesVersion}")
            }
        }
        "jvmTest" {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        "jsMain" {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Ver.coroutinesVersion}")
            }
        }
        "jsTest" {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
