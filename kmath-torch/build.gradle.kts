import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget


plugins {
    id("ru.mipt.npm.mpp")
    id("de.undercouch.download")
}


val home = System.getProperty("user.home")
val thirdPartyDir = "$home/.konan/third-party/kmath-torch-${project.property("version")}"
val cppBuildDir = "$thirdPartyDir/cpp-build"

val cudaHome: String? = System.getenv("CUDA_HOME")
val cudaDefault = file("/usr/local/cuda").exists()
val cudaFound = cudaHome?.isNotEmpty() ?: false or cudaDefault

val cmakeArchive = "cmake-3.19.2-Linux-x86_64"
val torchArchive = "libtorch"

val cmakeCmd = "$thirdPartyDir/$cmakeArchive/bin/cmake"
val ninjaCmd = "$thirdPartyDir/ninja"

val downloadCMake by tasks.registering(Download::class) {
    val tarFile = "$cmakeArchive.tar.gz"
    src("https://github.com/Kitware/CMake/releases/download/v3.19.2/$tarFile")
    dest(File(thirdPartyDir, tarFile))
    overwrite(false)
}

val downloadNinja by tasks.registering(Download::class) {
    src("https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-linux.zip")
    dest(File(thirdPartyDir, "ninja-linux.zip"))
    overwrite(false)
}

val downloadTorch by tasks.registering(Download::class) {
    val abiMeta = "$torchArchive-cxx11-abi-shared-with-deps-1.7.1%2B"
    val cudaUrl = "https://download.pytorch.org/libtorch/cu110/${abiMeta}cu110.zip"
    val cpuUrl = "https://download.pytorch.org/libtorch/cpu/${abiMeta}cpu.zip"
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

val extractTorch by tasks.registering(Copy::class) {
    dependsOn(downloadTorch)
    from(zipTree(downloadTorch.get().dest))
    into(thirdPartyDir)
}

val extractNinja by tasks.registering(Copy::class) {
    dependsOn(downloadNinja)
    from(zipTree(downloadNinja.get().dest))
    into(thirdPartyDir)
}

val configureCpp by tasks.registering {
    dependsOn(extractCMake)
    dependsOn(extractNinja)
    dependsOn(extractTorch)
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
                projectDir.resolve("ctorch"),
                "-GNinja",
                "-DCMAKE_MAKE_PROGRAM=$ninjaCmd",
                "-DCMAKE_PREFIX_PATH=$thirdPartyDir/$torchArchive",
                "-DCMAKE_BUILD_TYPE=Release"
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

kotlin {
    explicitApiWarning()

    val nativeTarget = linuxX64("torch")
    nativeTarget.apply {
        binaries {
            all {
                linkerOpts(
                    "-L$cppBuildDir",
                    "-Wl,-rpath=$cppBuildDir",
                    "-lctorch"
                )
            }
        }
    }

    val main by nativeTarget.compilations.getting {
        cinterops {
            val libctorch by creating {
                includeDirs(projectDir.resolve("ctorch/include"))
            }
        }
    }

    val test by nativeTarget.compilations.getting


    sourceSets {
        val nativeMain by creating {
            dependencies {
                api(project(":kmath-core"))
            }
        }
        val nativeTest by creating {
            dependsOn(nativeMain)
        }
        val nativeGPUTest by creating {
            dependsOn(nativeMain)
        }


        main.defaultSourceSet.dependsOn(nativeMain)
        test.defaultSourceSet.dependsOn(nativeTest)
        if(cudaFound) {
            test.defaultSourceSet.dependsOn(nativeGPUTest)
        }

    }
}

val torch: KotlinNativeTarget by kotlin.targets
tasks[torch.compilations["main"].cinterops["libctorch"].interopProcessingTaskName]
    .dependsOn(buildCpp)
