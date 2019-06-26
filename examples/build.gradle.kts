import org.jetbrains.gradle.benchmarks.JvmBenchmarkTarget
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen") version "1.3.31"
    id("org.jetbrains.gradle.benchmarks.plugin") version "0.1.7-dev-24"
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("http://dl.bintray.com/kyonifer/maven")
    maven("https://dl.bintray.com/orangy/maven")
    mavenCentral()
}

sourceSets {
    register("benchmarks")
}

dependencies {
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-koma"))
    implementation("com.kyonifer:koma-core-ejml:0.12")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.5")

    implementation("org.jetbrains.gradle.benchmarks:runtime:0.1.7-dev-24")


    "benchmarksCompile"(sourceSets.main.get().compileClasspath)
}

// Configure benchmark
benchmark {
    // Setup configurations
    targets {
        // This one matches sourceSet name above
        register("benchmarks") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }

    configurations {
        register("fast") {
            warmups = 5 // number of warmup iterations
            iterations = 3 // number of iterations
            iterationTime = 500 // time in seconds per iteration
            iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
        }
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}