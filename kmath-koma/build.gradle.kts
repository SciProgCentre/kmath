plugins {
    `multiplatform-config`
}

repositories {
    maven("http://dl.bintray.com/kyonifer/maven")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("com.kyonifer:koma-core-api-common:0.12")
        }
    }
    jvmMain {
        dependencies {
            api("com.kyonifer:koma-core-api-jvm:0.12")
        }
    }
    jvmTest {
        dependencies {
            implementation("com.kyonifer:koma-core-ejml:0.12")
        }
    }
    jsMain {
        dependencies {
            api("com.kyonifer:koma-core-api-js:0.12")
        }
    }
}
