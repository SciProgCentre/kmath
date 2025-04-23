/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.coroutines.runBlocking
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.stat.min
import space.kscience.kmath.structures.*

@State(Scope.Benchmark)
internal class MinStatisticBenchmark {

    @Benchmark
    fun kotlinArrayMin(blackhole: Blackhole) {
        val array = DoubleArray(size) { it.toDouble() }
        var res = 0.0
        (0 until size).forEach {
            res += array.min()
        }
        blackhole.consume(res)
    }

    @Benchmark
    fun minBlocking(blackhole: Blackhole) {
        val buffer = Float64Buffer(size) { it.toDouble() }
        var res = 0.0
        (0 until size).forEach {
            res += Float64Field.min.evaluateBlocking(buffer)
        }
        blackhole.consume(res)
    }


    private companion object {
        private const val size = 1000
    }
}
