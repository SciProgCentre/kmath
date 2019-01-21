plugins {
    id("kotlin-multiplatform")
}

repositories {
    maven("http://dl.bintray.com/kyonifer/maven")
}

kotlin {
    jvm {
        compilations["main"].kotlinOptions.jvmTarget = "1.8"
    }
    js()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
                implementation("com.kyonifer:koma-core-api-common:0.12")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
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
                implementation(kotlin("stdlib-jdk8"))
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
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}