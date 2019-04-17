plugins {
    id("multiplatform-config")
}

repositories {
    maven("http://dl.bintray.com/kyonifer/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-progressive"
            }
        }
    }
    js()

    sourceSets.invoke {

        commonMain {
            dependencies {
                api(project(":kmath-core"))
                api("com.kyonifer:koma-core-api-common:0.12")
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
                api("com.kyonifer:koma-core-api-jvm:0.12")
            }
        }
        "jvmTest" {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("com.kyonifer:koma-core-ejml:0.12")
            }
        }
        "jsMain" {
            dependencies {
                api("com.kyonifer:koma-core-api-js:0.12")
            }
        }
        "jsTest" {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
