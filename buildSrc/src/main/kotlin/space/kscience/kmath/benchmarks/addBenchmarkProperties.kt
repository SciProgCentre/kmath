/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.gradle.api.Project
import ru.mipt.npm.gradle.KScienceReadmeExtension
import java.time.*
import java.time.format.*
import java.time.temporal.ChronoField.*

private val ISO_DATE_TIME: DateTimeFormatter = DateTimeFormatterBuilder().run {
    parseCaseInsensitive()
    appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
    appendLiteral('-')
    appendValue(MONTH_OF_YEAR, 2)
    appendLiteral('-')
    appendValue(DAY_OF_MONTH, 2)
    appendLiteral('T')
    appendValue(HOUR_OF_DAY, 2)
    appendLiteral('.')
    appendValue(MINUTE_OF_HOUR, 2)
    optionalStart()
    appendLiteral('.')
    appendValue(SECOND_OF_MINUTE, 2)
    optionalStart()
    appendFraction(NANO_OF_SECOND, 0, 9, true)
    optionalStart()
    appendOffsetId()
    optionalStart()
    appendLiteral('[')
    parseCaseSensitive()
    appendZoneRegionId()
    appendLiteral(']')
    toFormatter()
}

private fun noun(number: Number, singular: String, plural: String) = if (number.toLong() == 1L) singular else plural

fun Project.addBenchmarkProperties() {
    val benchmarksProject = this
    rootProject.subprojects.forEach { p ->
        p.extensions.findByType(KScienceReadmeExtension::class.java)?.run {
            benchmarksProject.extensions.findByType(BenchmarksExtension::class.java)?.configurations?.forEach { cfg ->
                property("benchmark${cfg.name.replaceFirstChar(Char::uppercase)}") {
                    val launches = benchmarksProject.buildDir.resolve("reports/benchmarks/${cfg.name}")

                    val resDirectory = launches.listFiles()?.maxByOrNull {
                        LocalDateTime.parse(it.name, ISO_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant()
                    }

                    if (resDirectory == null || !(resDirectory.resolve("jvm.json")).exists()) {
                        "> **Can't find appropriate benchmark data. Try generating readme files after running benchmarks**."
                    } else {
                        val reports =
                            Json.decodeFromString<List<JmhReport>>(resDirectory.resolve("jvm.json").readText())

                        buildString {
                            appendLine("<details>")
                            appendLine("<summary>")
                            appendLine("Report for benchmark configuration <code>${cfg.name}</code>")
                            appendLine("</summary>")
                            appendLine()
                            val first = reports.first()

                            appendLine("* Run on ${first.vmName} (build ${first.vmVersion}) with Java process:")
                            appendLine()
                            appendLine("```")
                            appendLine("${first.jvm} ${
                                first.jvmArgs.joinToString(" ")
                            }")
                            appendLine("```")

                            appendLine("* JMH ${first.jmhVersion} was used in `${first.mode}` mode with ${first.warmupIterations} warmup ${
                                noun(first.warmupIterations, "iteration", "iterations")
                            } by ${first.warmupTime} and ${first.measurementIterations} measurement ${
                                noun(first.measurementIterations, "iteration", "iterations")
                            } by ${first.measurementTime}.")

                            appendLine()
                            appendLine("| Benchmark | Score |")
                            appendLine("|:---------:|:-----:|")

                            reports.forEach { report ->
                                appendLine("|`${report.benchmark}`|${report.primaryMetric.score} &plusmn; ${report.primaryMetric.scoreError} ${report.primaryMetric.scoreUnit}|")
                            }

                            appendLine("</details>")
                        }
                    }
                }
            }
        }
    }
}
