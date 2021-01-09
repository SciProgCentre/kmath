# LibTorch extension (`kmath-torch`)

This is a `Kotlin/Native` module, with only `linuxX64` supported so far. This library wraps some of the [PyTorch C++ API](https://pytorch.org/cppdocs), focusing on integrating `Aten` & `Autograd` with `KMath`. 

## Installation
To install the library, you have to build & publish locally `kmath-core`, `kmath-memory` with `kmath-torch`:
```
./gradlew -q :kmath-core:publishToMavenLocal :kmath-memory:publishToMavenLocal :kmath-torch:publishToMavenLocal
```

This builds `ctorch`, a C wrapper for `LibTorch` placed inside:

`~/.konan/third-party/kmath-torch-0.2.0-dev-4/cpp-build` 

You will have to link against it in your own project. Here is an example of build script for a standalone application:
```kotlin
//build.gradle.kts
plugins {
    id("ru.mipt.npm.mpp")
}

repositories {
    jcenter()
    mavenLocal()
}

val home = System.getProperty("user.home")
val kver = "0.2.0-dev-4"
val cppBuildDir = "$home/.konan/third-party/kmath-torch-$kver/cpp-build"

kotlin {
    explicitApiWarning()

    val nativeTarget = linuxX64("your.app")
    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "your.app.main"
            }
            all {
                linkerOpts(
                    "-L$cppBuildDir",
                    "-Wl,-rpath=$cppBuildDir",
                    "-lctorch"
                )
            }
        }
    }

    val main by nativeTarget.compilations.getting

    sourceSets {
        val nativeMain by creating {
            dependencies {
                implementation("kscience.kmath:kmath-torch:$kver")
            }
        }
        main.defaultSourceSet.dependsOn(nativeMain)
    }
}
```
```kotlin
//settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        maven("https://dl.bintray.com/mipt-npm/dev")
    }
    plugins {
        id("ru.mipt.npm.mpp") version "0.7.1"
        kotlin("jvm") version "1.4.21"
    }
}
```

## Usage

Tensors implement the buffer protocol over `MutableNDStructure`. They can only be instantiated through provided factory methods and require scoping:
```kotlin
TorchTensorRealAlgebra {

    val realTensor: TorchTensorReal = copyFromArray(
        array = (1..10).map { it + 50.0 }.toList().toDoubleArray(),
        shape = intArrayOf(2,5)
    )
    println(realTensor)

    val gpuRealTensor: TorchTensorReal = copyFromArray(
        array = (1..8).map { it * 2.5 }.toList().toDoubleArray(),
        shape = intArrayOf(2, 2, 2),
        device = TorchDevice.TorchCUDA(0)
    )
    println(gpuRealTensor)
}
```

