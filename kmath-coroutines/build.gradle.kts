plugins {
    `npm-multiplatform`
    id("kotlinx-atomicfu") version Versions.atomicfuVersion
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Versions.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu:${Versions.atomicfuVersion}")
        }
    }
    jsMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Versions.atomicfuVersion}")
        }
    }
}
