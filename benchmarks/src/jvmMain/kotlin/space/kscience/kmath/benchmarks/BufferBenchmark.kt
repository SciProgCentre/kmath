/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.complex.complex
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer

@State(Scope.Benchmark)
internal class BufferBenchmark {
    @Benchmark
    fun genericDoubleBufferReadWrite(blackhole: Blackhole) {
        val buffer = DoubleBuffer(size) { it.toDouble() }
        var res = 0.0
        (0 until size).forEach {
            res += buffer[it]
        }
        blackhole.consume(res)
    }

    @Benchmark
    fun complexBufferReadWrite(blackhole: Blackhole) = ComplexField {
        val buffer = Buffer.complex(size / 2) { Complex(it.toDouble(), -it.toDouble()) }

        var res = zero
        (0 until size / 2).forEach {
            res += buffer[it]
        }

        blackhole.consume(res)
    }

    private companion object {
        private const val size = 100
    }
}
