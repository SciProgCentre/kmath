import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.benchmark.gradle.BenchmarksExtension
import java.util.*

plugins {
    kotlin("multiplatform")
    alias(spclibs.plugins.kotlin.plugin.allopen)
    alias(spclibs.plugins.kotlinx.benchmark)
}

allOpen.annotation("org.openjdk.jmh.annotations.State")
//sourceSets.register("benchmarks")

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        optIn.addAll(
            "space.kscience.kmath.UnstableKMathAPI"
        )
    }

    jvm()

    js(IR) {
        nodejs()
    }

    sourceSets {
        all {
            languageSettings {
                progressiveMode = true
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("space.kscience.kmath.UnstableKMathAPI")
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
                implementation(libs.multik.default)
                implementation(spclibs.kotlinx.benchmark.runtime)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(projects.kmathCommons)
                implementation(projects.kmathEjml)
                implementation(projects.kmathNd4j)
                implementation(projects.kmathKotlingrad)
                implementation(projects.kmathViktor)
                implementation(projects.kmathOjalgo)
                implementation(projects.kmath.kmathTensorflow)
                implementation(projects.kmathMultik)
                implementation(libs.tensorflow.core.platform)
//                implementation(libs.nd4j.native.platform)
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

    configurations.register("integration") {
        commonConfiguration()
        include("IntegrationBenchmark")
    }
}


private data class JmhReport(
    val jmhVersion: String,
    val benchmark: String,
    val mode: String,
    val threads: Int,
    val forks: Int,
    val jvm: String,
    val jvmArgs: List<String>,
    val jdkVersion: String,
    val vmName: String,
    val vmVersion: String,
    val warmupIterations: Int,
    val warmupTime: String,
    val warmupBatchSize: Int,
    val measurementIterations: Int,
    val measurementTime: String,
    val measurementBatchSize: Int,
    val params: Map<String, String> = emptyMap(),
    val primaryMetric: PrimaryMetric,
    val secondaryMetrics: Map<String, SecondaryMetric>,
) {
    interface Metric {
        val score: Double
        val scoreError: Double
        val scoreConfidence: List<Double>
        val scorePercentiles: Map<Double, Double>
        val scoreUnit: String
    }

    data class PrimaryMetric(
        override val score: Double,
        override val scoreError: Double,
        override val scoreConfidence: List<Double>,
        override val scorePercentiles: Map<Double, Double>,
        override val scoreUnit: String,
        val rawDataHistogram: List<List<List<List<Double>>>>? = null,
        val rawData: List<List<Double>>? = null,
    ) : Metric

    data class SecondaryMetric(
        override val score: Double,
        override val scoreError: Double,
        override val scoreConfidence: List<Double>,
        override val scorePercentiles: Map<Double, Double>,
        override val scoreUnit: String,
        val rawData: List<List<Double>>,
    ) : Metric
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL

    val jsonMapper = jacksonObjectMapper()

    fun noun(number: Number, singular: String, plural: String) = if (number.toLong() == 1L) singular else plural

    extensions.findByType(BenchmarksExtension::class.java)?.configurations?.forEach { cfg ->
        val propertyName =
            "benchmark${cfg.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"

        logger.info("Processing benchmark data from benchmark ${cfg.name} into readme property $propertyName")

        val launches = layout.buildDirectory.dir("reports/benchmarks/${cfg.name}").get().asFile
        if (!launches.exists()) return@forEach

        property(propertyName) {
            val resDirectory = launches.listFiles()?.maxByOrNull {
                it.nameWithoutExtension
            }

            if (resDirectory == null || !(resDirectory.resolve("jvm.json")).exists()) {
                "> **Can't find appropriate benchmark data. Try generating readme files after running benchmarks**."
            } else {
                val reports: List<JmhReport> =
                    jsonMapper.readValue<List<JmhReport>>(resDirectory.resolve("jvm.json"))

                buildString {
                    appendLine("## Report for benchmark configuration <code>${cfg.name}</code>")
                    appendLine()
                    val first = reports.first()

                    appendLine("* Run on ${first.vmName} (build ${first.vmVersion}) with Java process:")
                    appendLine()
                    appendLine("```")
                    appendLine(
                        "${first.jvm} ${
                            first.jvmArgs.joinToString(" ")
                        }"
                    )
                    appendLine("```")

                    appendLine(
                        "* JMH ${first.jmhVersion} was used in `${first.mode}` mode with ${first.warmupIterations} warmup ${
                            noun(first.warmupIterations, "iteration", "iterations")
                        } by ${first.warmupTime} and ${first.measurementIterations} measurement ${
                            noun(first.measurementIterations, "iteration", "iterations")
                        } by ${first.measurementTime}."
                    )

                    reports.groupBy { it.benchmark.substringBeforeLast(".") }.forEach { (cl, compare) ->
                        appendLine("### [${cl.substringAfterLast(".")}](src/jvmMain/kotlin/${cl.replace(".","/")}.kt)")
                        appendLine()
                        appendLine("| Benchmark | Score |")
                        appendLine("|:---------:|:-----:|")
                        compare.forEach { report ->
                            val benchmarkName = report.benchmark.substringAfterLast(".")
                            val score = String.format("%.2G", report.primaryMetric.score)
                            val error = String.format("%.2G", report.primaryMetric.scoreError)

                            appendLine("|`$benchmarkName`|$score &plusmn; $error ${report.primaryMetric.scoreUnit}|")
                        }
                    }
                }
            }
        }
    }
}