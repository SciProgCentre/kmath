@file:Suppress("UNUSED_VARIABLE")

import kscience.kmath.gsl.codegen.matricesCodegen
import kscience.kmath.gsl.codegen.vectorsCodegen

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
            val libgsl by creating { includeDirs { headerFilterOnly("/usr/include/", "/usr/local/") } }
        }
    }

    sourceSets {
        val nativeMain by getting {
            val codegen by tasks.creating {
                matricesCodegen(kotlin.srcDirs.first().absolutePath + "/kscience/kmath/gsl/_Matrices.kt")
                vectorsCodegen(kotlin.srcDirs.first().absolutePath + "/kscience/kmath/gsl/_Vectors.kt")
            }

            kotlin.srcDirs(files().builtBy(codegen))

            dependencies {
                api(project(":kmath-core"))
                api("org.jetbrains.kotlinx:kotlinx-io:0.2.0-tvis-3")
            }
        }
    }
}
