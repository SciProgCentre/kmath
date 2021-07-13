/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import de.undercouch.gradle.tasks.download.Download


plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
    id("de.undercouch.download")
}

description = "Wrapper for the Bayesian Computation library NOA on top of LibTorch"

dependencies {
    implementation(project(":kmath-tensors"))
}

val home: String = System.getProperty("user.home")
val javaHome: String = System.getProperty("java.home")
val thirdPartyDir = "$home/.konan/third-party/noa-v0.0.1"
val cppBuildDir = "$thirdPartyDir/cpp-build"
val jNoaDir = "$thirdPartyDir/jnoa/noa-kmath"

val cudaHome: String? = System.getenv("CUDA_HOME")
val cudaDefault = file("/usr/local/cuda").exists()
val cudaFound = cudaHome?.isNotEmpty() ?: false or cudaDefault

val cmakeArchive = "cmake-3.20.5-linux-x86_64"
val torchArchive = "libtorch"

val cmakeCmd = "$thirdPartyDir/cmake/$cmakeArchive/bin/cmake"
val ninjaCmd = "$thirdPartyDir/ninja/ninja"

val generateJNIHeader by tasks.registering {
    doLast {
        exec {
            workingDir(projectDir.resolve("src/main/java/space/kscience/kmath/noa"))
            commandLine(
                "$javaHome/bin/javac", "-h",
                projectDir.resolve("src/main/resources"), "JNoa.java"
            )
        }
    }
}

val downloadCMake by tasks.registering(Download::class) {
    val tarFile = "$cmakeArchive.tar.gz"
    src("https://github.com/Kitware/CMake/releases/download/v3.20.5/$tarFile")
    dest(File("$thirdPartyDir/cmake", tarFile))
    overwrite(false)
}


val downloadNinja by tasks.registering(Download::class) {
    src("https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-linux.zip")
    dest(File("$thirdPartyDir/ninja", "ninja-linux.zip"))
    overwrite(false)
}

val downloadTorch by tasks.registering(Download::class) {
    val torchVersion = "$torchArchive-shared-with-deps-1.9.0%2B"
    val cudaUrl = "https://download.pytorch.org/libtorch/cu111/${torchVersion}cu111.zip"
    val cpuUrl = "https://download.pytorch.org/libtorch/cpu/${torchVersion}cpu.zip"
    val url = if (cudaFound) cudaUrl else cpuUrl
    src(url)
    dest(File("$thirdPartyDir/torch", "$torchArchive.zip"))
    overwrite(false)
}

fun downloadJNoaHelper(update: Boolean) = tasks.registering(Download::class) {
    src("https://github.com/grinisrit/noa/archive/refs/heads/kmath.zip")
    dest(File("$thirdPartyDir/jnoa", "kmath.zip"))
    overwrite(update)
}

val downloadJNoa by downloadJNoaHelper(false)

val reDownloadJNoa by downloadJNoaHelper(true)


val extractCMake by tasks.registering(Copy::class) {
    dependsOn(downloadCMake)
    from(tarTree(resources.gzip(downloadCMake.get().dest)))
    into("$thirdPartyDir/cmake")
}

val extractNinja by tasks.registering(Copy::class) {
    dependsOn(downloadNinja)
    from(zipTree(downloadNinja.get().dest))
    into("$thirdPartyDir/ninja")
}

val extractTorch by tasks.registering(Copy::class) {
    dependsOn(downloadTorch)
    from(zipTree(downloadTorch.get().dest))
    into("$thirdPartyDir/torch")
}

val extractJNoa by tasks.registering(Copy::class) {
    dependsOn(downloadJNoa)
    from(zipTree(downloadJNoa.get().dest))
    into("$thirdPartyDir/jnoa")
}

val configureCpp by tasks.registering {
    dependsOn(extractCMake)
    dependsOn(extractNinja)
    dependsOn(extractTorch)
    dependsOn(extractJNoa)
    onlyIf { !file(cppBuildDir).exists() }
    doLast {
        exec {
            workingDir(thirdPartyDir)
            commandLine("mkdir", "-p", cppBuildDir)
        }
        exec {
            workingDir(cppBuildDir)
            commandLine(
                cmakeCmd,
                jNoaDir,
                "-GNinja",
                "-DCMAKE_MAKE_PROGRAM=$ninjaCmd",
                "-DCMAKE_PREFIX_PATH=$thirdPartyDir/torch/$torchArchive",
                "-DJAVA_HOME=$javaHome",
                "-DBUILD_NOA_KMATH=ON",
                "-DCMAKE_BUILD_TYPE=Release",
                "-DBUILD_NOA_CUDA=${if (!cudaFound) "ON" else "OFF"}",
                "-DBUILD_NOA_TESTS=OFF",
                "-DBUILD_NOA_BENCHMARKS=OFF",
                "-DINSTALL_NOA=OFF"
            )
        }
    }
}

val cleanCppBuild by tasks.registering {
    onlyIf { file(cppBuildDir).exists() }
    doLast {
        exec {
            workingDir(thirdPartyDir)
            commandLine("rm", "-rf", cppBuildDir)
        }
    }
}

val buildCpp by tasks.registering {
    dependsOn(configureCpp)
    doLast {
        exec {
            workingDir(cppBuildDir)
            commandLine(cmakeCmd, "--build", ".", "--config", "Release")
        }
    }
}

tasks["compileJava"].dependsOn(buildCpp)

tasks {
    withType<Test>{
        systemProperty("java.library.path", "$cppBuildDir/kmath")
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
