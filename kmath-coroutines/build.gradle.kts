plugins {
    `multiplatform-config`
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Ver.coroutinesVersion}")
        }
    }
    jvmMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Ver.coroutinesVersion}")
        }
    }
    jsMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Ver.coroutinesVersion}")
        }
    }
}
