plugins {
    `multiplatform-config`
    id("kotlinx-atomicfu") version Ver.atomicfuVersion
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-coroutines"))
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Ver.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            compileOnly("org.jetbrains.kotlinx:atomicfu:${Ver.atomicfuVersion}")
        }
    }
    jsMain {
        dependencies {
            compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Ver.atomicfuVersion}")
        }
    }

}

atomicfu {
    variant = "VH"
}
