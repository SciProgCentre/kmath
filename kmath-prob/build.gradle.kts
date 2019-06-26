plugins {
    `npm-multiplatform`
    id("kotlinx-atomicfu") version Versions.atomicfuVersion
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Versions.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            // https://mvnrepository.com/artifact/org.apache.commons/commons-rng-simple
            //api("org.apache.commons:commons-rng-sampling:1.2")
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
