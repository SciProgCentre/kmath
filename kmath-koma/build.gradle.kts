plugins {
    id("kotlin-multiplatform")
}

repositories {
    maven("http://dl.bintray.com/kyonifer/maven")
}

kotlin {
    jvm {
        compilations["main"].kotlinOptions.jvmTarget = "1.8"
        compilations["test"].kotlinOptions.jvmTarget = "1.8"
    }
    js()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
                api("com.kyonifer:koma-core-api-common:0.12")
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
                api("com.kyonifer:koma-core-api-jvm:0.12")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("com.kyonifer:koma-core-ejml:0.12")
            }
        }
        val jsMain by getting {
            dependencies {
                api("com.kyonifer:koma-core-api-js:0.12")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}