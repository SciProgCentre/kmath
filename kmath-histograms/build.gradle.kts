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
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        "jvmTest" {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        "jsTest" {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
