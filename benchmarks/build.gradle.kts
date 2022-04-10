@file:Suppress("UNUSED_VARIABLE")

import space.kscience.kmath.benchmarks.addBenchmarkProperties

plugins {
    kotlin("multiplatform")
    kotlin("plugin.allopen")
    id("org.jetbrains.kotlinx.benchmark")
}

allOpen.annotation("org.openjdk.jmh.annotations.State")
sourceSets.register("benchmarks")

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        nodejs()
    }

    sourceSets {
        all {
            languageSettings {
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(project(":kmath-ast"))
                implementation(project(":kmath-core"))
                implementation(project(":kmath-coroutines"))
                implementation(project(":kmath-complex"))
                implementation(project(":kmath-stat"))
                implementation(project(":kmath-dimensions"))
                implementation(project(":kmath-for-real"))
                implementation(project(":kmath-tensors"))
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(project(":kmath-commons"))
                implementation(project(":kmath-ejml"))
                implementation(project(":kmath-nd4j"))
                implementation(project(":kmath-kotlingrad"))
                implementation(project(":kmath-viktor"))
                implementation(project(":kmath-jafama"))
                implementation(project(":kmath-multik"))
                implementation(projects.kmath.kmathTensorflow)
                implementation("org.tensorflow:tensorflow-core-platform:0.4.0")
                implementation("org.nd4j:nd4j-native:1.0.0-M1")
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
        register("js")
    }

    fun kotlinx.benchmark.gradle.BenchmarkConfiguration.commonConfiguration() {
        warmups = 2
        iterations = 5
        iterationTime = 2000
        iterationTimeUnit = "ms"
    }

    configurations.register("buffer") {
        commonConfiguration()
        include("BufferBenchmark")
    }

    configurations.register("nd") {
        commonConfiguration()
        include("NDFieldBenchmark")
    }

    configurations.register("dot") {
        commonConfiguration()
        include("DotBenchmark")
    }

    configurations.register("expressions") {
        // Some extra precision
        warmups = 2
        iterations = 10
        iterationTime = 10
        iterationTimeUnit = "s"
        outputTimeUnit = "s"
        include("ExpressionsInterpretersBenchmark")
    }

    configurations.register("matrixInverse") {
        commonConfiguration()
        include("MatrixInverseBenchmark")
    }

    configurations.register("bigInt") {
        commonConfiguration()
        include("BigIntBenchmark")
    }

    configurations.register("jafamaDouble") {
        commonConfiguration()
        include("JafamaBenchmark")
    }

    configurations.register("tensorAlgebra") {
        commonConfiguration()
        include("TensorAlgebraBenchmark")
    }

    configurations.register("viktor") {
        commonConfiguration()
        include("ViktorBenchmark")
    }

    configurations.register("viktorLog") {
        commonConfiguration()
        include("ViktorLogBenchmark")
    }
}

// Fix kotlinx-benchmarks bug
afterEvaluate {
    val jvmBenchmarkJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

kotlin.sourceSets.all {
    with(languageSettings) {
        optIn("kotlin.contracts.ExperimentalContracts")
        optIn("kotlin.ExperimentalUnsignedTypes")
        optIn("space.kscience.kmath.misc.UnstableKMathAPI")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all" + "-Xlambdas=indy"
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}

addBenchmarkProperties()
