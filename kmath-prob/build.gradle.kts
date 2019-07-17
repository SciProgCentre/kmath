plugins {
    id("scientifik.mpp")
    id("kotlinx-atomicfu")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
            compileOnly("org.jetbrains.kotlinx:atomicfu-common:${Scientifik.atomicfuVersion}")
        }
    }
    jvmMain {
        dependencies {
            // https://mvnrepository.com/artifact/org.apache.commons/commons-rng-simple
            //api("org.apache.commons:commons-rng-sampling:1.2")
            compileOnly("org.jetbrains.kotlinx:atomicfu:${Scientifik.atomicfuVersion}")
        }
    }
    jsMain {
        dependencies {
            compileOnly("org.jetbrains.kotlinx:atomicfu-js:${Scientifik.atomicfuVersion}")
        }
    }

}

atomicfu {
    variant = "VH"
}
