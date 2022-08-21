/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import java.nio.IntBuffer

@State(Scope.Benchmark)
internal class ArrayBenchmark {
    @Benchmark
    fun benchmarkArrayRead(blackhole: Blackhole) {
        var res = 0
        for (i in 1..size) res += array[size - i]
        blackhole.consume(res)
    }

    @Benchmark
    fun benchmarkBufferRead(blackhole: Blackhole) {
        var res = 0
        for (i in 1..size) res += arrayBuffer[size - i]
        blackhole.consume(res)
    }

    @Benchmark
    fun nativeBufferRead(blackhole: Blackhole) {
        var res = 0
        for (i in 1..size) res += nativeBuffer[size - i]
        blackhole.consume(res)
    }

    private companion object {
        private const val size = 1000
        private val array = IntArray(size) { it }
        private val arrayBuffer = IntBuffer.wrap(array)
        private val nativeBuffer = IntBuffer.allocate(size).also { for (i in 0 until size) it.put(i, i) }
    }
}
