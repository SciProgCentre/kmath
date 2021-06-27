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
    api(project(":kmath-tensors"))
}

val home: String = System.getProperty("user.home")
val javaHome: String = System.getProperty("java.home")
val thirdPartyDir = "$home/.konan/third-party/kmath-noa-${project.property("version")}"
val cppBuildDir = "$thirdPartyDir/cpp-build"
val cppSources = projectDir.resolve("src/main/cpp")

val cudaHome: String? = System.getenv("CUDA_HOME")
val cudaDefault = file("/usr/local/cuda").exists()
val cudaFound = cudaHome?.isNotEmpty() ?: false or cudaDefault

val cmakeArchive = "cmake-3.20.5-Linux-x86_64"
val torchArchive = "libtorch"

val cmakeCmd = "$thirdPartyDir/$cmakeArchive/bin/cmake"
val ninjaCmd = "$thirdPartyDir/ninja"

val downloadCMake by tasks.registering(Download::class) {
    val tarFile = "$cmakeArchive.tar.gz"
    src("https://github.com/Kitware/CMake/releases/download/v3.20.5/$tarFile")
    dest(File(thirdPartyDir, tarFile))
    overwrite(false)
}

val downloadNinja by tasks.registering(Download::class) {
    src("https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-linux.zip")
    dest(File(thirdPartyDir, "ninja-linux.zip"))
    overwrite(false)
}

val downloadTorch by tasks.registering(Download::class) {
    val torchVersion = "$torchArchive-shared-with-deps-1.9.0%2B"
    val cudaUrl = "https://download.pytorch.org/libtorch/cu111/${torchVersion}cu111.zip"
    val cpuUrl = "https://download.pytorch.org/libtorch/cpu/${torchVersion}cpu.zip"
    val url = if (cudaFound) cudaUrl else cpuUrl
    src(url)
    dest(File(thirdPartyDir, "$torchArchive.zip"))
    overwrite(false)
}

val extractCMake by tasks.registering(Copy::class) {
    dependsOn(downloadCMake)
    from(tarTree(resources.gzip(downloadCMake.get().dest)))
    into(thirdPartyDir)
}

val extractNinja by tasks.registering(Copy::class) {
    dependsOn(downloadNinja)
    from(zipTree(downloadNinja.get().dest))
    into(thirdPartyDir)
}

val extractTorch by tasks.registering(Copy::class) {
    dependsOn(downloadTorch)
    from(zipTree(downloadTorch.get().dest))
    into(thirdPartyDir)
}