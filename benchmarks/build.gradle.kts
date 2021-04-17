/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import ru.mipt.npm.gradle.Maturity

plugins {
    kotlin("multiplatform")
    kotlin("plugin.allopen")
    id("org.jetbrains.kotlinx.benchmark")
}

allOpen.annotation("org.openjdk.jmh.annotations.State")
sourceSets.register("benchmarks")



repositories {
    mavenCentral()
    jcenter()
    maven("https://repo.kotlin.link")
    maven("https://clojars.org/repo")
    maven("https://dl.bintray.com/egor-bogomolov/astminer/")
    maven("https://dl.bintray.com/hotkeytlt/maven")
    maven("https://jitpack.io")
    maven {
        setUrl("http://logicrunch.research.it.uu.se/maven/")
        isAllowInsecureProtocol = true
    }
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kmath-ast"))
                implementation(project(":kmath-core"))
                implementation(project(":kmath-coroutines"))
                implementation(project(":kmath-complex"))
                implementation(project(":kmath-stat"))
                implementation(project(":kmath-dimensions"))
                implementation(project(":kmath-for-real"))
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.3.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(project(":kmath-commons"))
                implementation(project(":kmath-ejml"))
                implementation(project(":kmath-nd4j"))
                implementation(project(":kmath-kotlingrad"))
                implementation(project(":kmath-viktor"))
                implementation("org.nd4j:nd4j-native:1.0.0-beta7")

                //    uncomment if your system supports AVX2
                //    val os = System.getProperty("os.name")
                //
                //    if (System.getProperty("os.arch") in arrayOf("x86_64", "amd64")) when {
                //        os.startsWith("Windows") -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:windows-x86_64-avx2")
                //        os == "Linux" -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:linux-x86_64-avx2")
                //        os == "Mac OS X" -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:macosx-x86_64-avx2")
                //    } else
                //    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
            }
        }
    }
}

// Configure benchmark
benchmark {
    // Setup configurations
    targets {
        register("jvm")
    }

    configurations.register("buffer") {
        warmups = 1 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        include("BufferBenchmark")
    }

    configurations.register("dot") {
        warmups = 1 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        include("DotBenchmark")
    }

    configurations.register("expressions") {
        warmups = 1 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        include("ExpressionsInterpretersBenchmark")
    }

    configurations.register("matrixInverse") {
        warmups = 1 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        include("MatrixInverseBenchmark")
    }

    configurations.register("bigInt") {
        warmups = 1 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        include("BigIntBenchmark")
    }
}

// Fix kotlinx-benchmarks bug
afterEvaluate {
    val jvmBenchmarkJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
    }
}


kotlin.sourceSets.all {
    with(languageSettings) {
        useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
        useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
    }
}


readme {
    maturity = Maturity.EXPERIMENTAL
}
