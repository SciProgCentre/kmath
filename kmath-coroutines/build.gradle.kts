plugins {
    id("scientifik.mpp")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Scientifik.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Scientifik.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Scientifik.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu:${Scientifik.atomicfuVersion}")
        }
    }
    jsMain {
        dependencies {
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Scientifik.coroutinesVersion}")
            compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Scientifik.atomicfuVersion}")
        }
    }
}
