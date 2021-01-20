import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("kotlinx.benchmark")
}

allOpen.annotation("org.openjdk.jmh.annotations.State")
sourceSets.register("benchmarks")

repositories {
    jcenter()
    maven("https://clojars.org/repo")
    maven("https://dl.bintray.com/egor-bogomolov/astminer/")
    maven("https://dl.bintray.com/hotkeytlt/maven")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://dl.bintray.com/mipt-npm/dev")
    maven("https://dl.bintray.com/mipt-npm/kscience")
    maven("https://jitpack.io")
    maven("http://logicrunch.research.it.uu.se/maven/")
    mavenCentral()
}

dependencies {
    implementation(project(":kmath-ast"))
    implementation(project(":kmath-kotlingrad"))
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-stat"))
    implementation(project(":kmath-viktor"))
    implementation(project(":kmath-dimensions"))
    implementation(project(":kmath-ejml"))
    implementation(project(":kmath-nd4j"))

    implementation(project(":kmath-for-real"))

    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-beta7")
    implementation("org.nd4j:nd4j-native:1.0.0-beta7")

//    uncomment if your system supports AVX2
//    val os = System.getProperty("os.name")
//
//    if (System.getProperty("os.arch") in arrayOf("x86_64", "amd64")) when {
//        os.startsWith("Windows") -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:windows-x86_64-avx2")
//        os == "Linux" -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:linux-x86_64-avx2")
//        os == "Mac OS X" -> implementation("org.nd4j:nd4j-native:1.0.0-beta7:macosx-x86_64-avx2")
//    } else
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")

    implementation("org.jetbrains.kotlinx:kotlinx-io:0.2.0-npm-dev-11")
    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0-dev-20")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    // plotting
    implementation("kscience.plotlykt:plotlykt-server:0.3.1-dev")

    "benchmarksImplementation"("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:0.2.0-dev-20")
    "benchmarksImplementation"(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}

// Configure benchmark
benchmark {
    // Setup configurations
    targets.register("benchmarks")
    // This one matches sourceSet name above

    configurations.register("fast") {
        warmups = 1 // number of warmup iterations
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
