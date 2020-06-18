import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen") version "1.3.71"
    id("kotlinx.benchmark") version "0.2.0-dev-7"
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    maven("http://dl.bintray.com/kyonifer/maven")
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://dl.bintray.com/mipt-npm/dev")
    mavenCentral()
}

sourceSets {
    register("benchmarks")
}

dependencies {
    implementation(project(":kmath-ast"))
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-prob"))
    implementation(project(":kmath-koma"))
    implementation(project(":kmath-viktor"))
    implementation(project(":kmath-dimensions"))
    implementation("com.kyonifer:koma-core-ejml:0.12")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.2.0-npm-dev-6")
    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0-dev-7")
    "benchmarksCompile"(sourceSets.main.get().compileClasspath)
}

// Configure benchmark
benchmark {
    // Setup configurations
    targets {
        // This one matches sourceSet name above
        register("benchmarks")
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
        jvmTarget = Scientifik.JVM_TARGET.toString()
    }
}