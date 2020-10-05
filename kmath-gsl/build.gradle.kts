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
            val libgsl by creating {
                defFile("src/nativeInterop/cinterop/libgsl.def")
                includeDirs { allHeaders("./src/nativeMain/resources/") }
            }
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                api(project(":kmath-core"))
                api("org.jetbrains.kotlinx:kotlinx-io:0.2.0-tvis-3")
            }
        }
    }
}

internal val codegen: Task by tasks.creating {
    matricesCodegen(kotlin.sourceSets["nativeMain"].kotlin.srcDirs.first().absolutePath + "/generated/Matrices.kt")
    vectorsCodegen(kotlin.sourceSets["nativeMain"].kotlin.srcDirs.first().absolutePath + "/generated/Vectors.kt")
}

kotlin.sourceSets["nativeMain"].kotlin.srcDirs(files().builtBy(codegen))
