/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

data class JmhReport(
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
