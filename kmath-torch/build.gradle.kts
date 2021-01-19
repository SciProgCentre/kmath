import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.gradle.api.JavaVersion.VERSION_11


plugins {
    id("ru.mipt.npm.mpp")
    id("de.undercouch.download")
}

java {
    sourceCompatibility = VERSION_11
    targetCompatibility = VERSION_11
}

val home = System.getProperty("user.home")
val javaHome = System.getProperty("java.home")
val thirdPartyDir = "$home/.konan/third-party/kmath-torch-${project.property("version")}"
val cppBuildDir = "$thirdPartyDir/cpp-build"
val cppSources = projectDir.resolve("src/cppMain")

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
                cppSources,
                "-GNinja",
                "-DCMAKE_MAKE_PROGRAM=$ninjaCmd",
                "-DCMAKE_PREFIX_PATH=$thirdPartyDir/$torchArchive",
                "-DJAVA_HOME=$javaHome",
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

val generateJNIHeader by tasks.registering {
    doLast {
        exec {
            workingDir(projectDir.resolve("src/jvmMain/java/kscience/kmath/torch"))
            commandLine("$javaHome/bin/javac", "-h", cppSources.resolve("include") , "JTorch.java")
        }
    }
}

kotlin {
    explicitApiWarning()

    jvm {
        withJava()
    }

    val nativeTarget = linuxX64("native")
    nativeTarget.apply {
        binaries {
            all {
                linkerOpts(
                    "-L$cppBuildDir",
                    "-Wl,-rpath=$cppBuildDir",
                    "-lctorch"
                )
                optimized = true
                debuggable = false
            }
        }
    }

    val main by nativeTarget.compilations.getting {
        cinterops {
            val libctorch by creating {
                includeDirs(cppSources.resolve("include"))
            }
        }
    }

    val test by nativeTarget.compilations.getting

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kmath-core"))
            }
        }

        val nativeMain by getting {
            dependencies {
                api(project(":kmath-core"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project(":kmath-core"))
            }
        }
    }
}

val native: KotlinNativeTarget by kotlin.targets
tasks[native.compilations["main"].cinterops["libctorch"].interopProcessingTaskName]
    .dependsOn(buildCpp)

tasks["jvmProcessResources"].dependsOn(buildCpp)

tasks {
    withType<Test>{
        systemProperty("java.library.path", cppBuildDir.toString())
    }
}