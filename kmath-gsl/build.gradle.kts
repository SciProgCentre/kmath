@file:Suppress("UNUSED_VARIABLE")

import kscience.kmath.gsl.codegen.matricesCodegen
import kscience.kmath.gsl.codegen.vectorsCodegen
import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.mpp")
}

kotlin {
    explicitApiWarning()

    val nativeTarget = when (System.getProperty("os.name")) {
//        "Mac OS X" -> macosX64()
        "Linux" -> linuxX64()

        else -> {
            logger.warn("Current OS cannot build any of kmath-gsl targets.")
            return@kotlin
        }
    }

    val main by nativeTarget.compilations.getting {
        cinterops {
            val libgsl by creating
        }
    }

    val test by nativeTarget.compilations.getting

    sourceSets {
        val nativeMain by creating {
            val codegen by tasks.creating {
                matricesCodegen(kotlin.srcDirs.first().absolutePath + "/kscience/kmath/gsl/_Matrices.kt")
                vectorsCodegen(kotlin.srcDirs.first().absolutePath + "/kscience/kmath/gsl/_Vectors.kt")
            }

            kotlin.srcDirs(files().builtBy(codegen))

            dependencies {
                api(project(":kmath-complex"))
                api(project(":kmath-core"))
            }
        }

        val nativeTest by creating {
            dependsOn(nativeMain)
        }

        main.defaultSourceSet.dependsOn(nativeMain)
        test.defaultSourceSet.dependsOn(nativeTest)
    }
}

readme {
    description = "Linear Algebra classes implemented with GNU Scientific Library"
    maturity = Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "matrix-contexts",
        description = "Matrix Contexts over Double, Float and Complex implemented with GSL",
        ref = "src/nativeMain/kotlin/kscience/kmath/gsl/GslMatrixContext.kt"
    )
}
