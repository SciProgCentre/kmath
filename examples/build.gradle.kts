import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("kotlinx.benchmark")
}

allOpen.annotation("org.openjdk.jmh.annotations.State")

repositories {
    maven("https://dl.bintray.com/mipt-npm/kscience")
    maven("https://dl.bintray.com/mipt-npm/dev")
    maven("https://dl.bintray.com/kotlin/kotlin-dev/")
    mavenCentral()
}

sourceSets.register("benchmarks")

dependencies {
//    implementation(project(":kmath-ast"))
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-prob"))
    implementation(project(":kmath-viktor"))
    implementation(project(":kmath-dimensions"))
    implementation(project(":kmath-ejml"))
    implementation("org.jetbrains.kotlinx:kotlinx-io:0.2.0-npm-dev-11")
    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0-dev-20")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    "benchmarksImplementation"("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:0.2.0-dev-8")
    "benchmarksImplementation"(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}

// Configure benchmark
benchmark {
    // Setup configurations
    targets.register("benchmarks")
    // This one matches sourceSet name above

    configurations.register("fast") {
        warmups = 5 // number of warmup iterations
        iterations = 3 // number of iterations
        iterationTime = 500 // time in seconds per iteration
        iterationTimeUnit = "ms" // time unity for iterationTime, default is seconds
    }
}

kotlin.sourceSets.all {
    with(languageSettings) {
        useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
        useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
    }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }
