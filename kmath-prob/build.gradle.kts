plugins {
    `multiplatform-config`
    id("kotlinx-atomicfu") version Versions.atomicfuVersion
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-coroutines"))
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Versions.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            compileOnly("org.jetbrains.kotlinx:atomicfu:${Versions.atomicfuVersion}")
        }
    }
    jsMain {
        dependencies {
            compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Versions.atomicfuVersion}")
        }
    }

}

atomicfu {
    variant = "VH"
}
