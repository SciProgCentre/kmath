plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
    maven("https://clojars.org/repo")
    maven("https://jitpack.io")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("http://logicrunch.research.it.uu.se/maven") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation(project(":kmath-ast"))
    implementation(project(":kmath-kotlingrad"))
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-complex"))
    implementation(project(":kmath-optimization"))
    implementation(project(":kmath-stat"))
    implementation(project(":kmath-viktor"))
    implementation(project(":kmath-dimensions"))
    implementation(project(":kmath-ejml"))
    implementation(project(":kmath-nd4j"))
    implementation(project(":kmath-tensors"))
    implementation(project(":kmath-symja"))
    implementation(project(":kmath-for-real"))
    //jafama
    implementation(project(":kmath-jafama"))
    //multik
    implementation(projects.kmathMultik)


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

    // multik implementation
    implementation("org.jetbrains.kotlinx:multik-default:0.1.0")

    implementation("org.slf4j:slf4j-simple:1.7.32")
    // plotting
    implementation("space.kscience:plotlykt-server:0.5.0")
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
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all" + "-Xopt-in=kotlin.RequiresOptIn" + "-Xlambdas=indy"
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}
