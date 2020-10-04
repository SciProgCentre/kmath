@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("ru.mipt.npm.mpp")
}

kotlin {
    val hostOs = System.getProperty("os.name")

    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    val main by nativeTarget.compilations.getting {
        cinterops {
            val libgsl by creating {
                defFile("src/nativeInterop/cinterop/libgsl.def")
                includeDirs { allHeaders("./src/nativeMain/resources/") }
            }
        }
    }

    sourceSets.commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
}
