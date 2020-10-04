@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("ru.mipt.npm.mpp")
}

kotlin {
    val nativeTarget = when (System.getProperty("os.name")) {
        "Mac OS X" -> macosX64("native")
        "Linux" -> linuxX64("native")
        else -> {
            logger.warn("Current OS cannot build any of kmath-gsl targets.")
            return@kotlin
        }
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
