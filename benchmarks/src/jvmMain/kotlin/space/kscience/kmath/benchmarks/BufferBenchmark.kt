/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.complex
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.MutableBuffer

@State(Scope.Benchmark)
internal class BufferBenchmark {
    @Benchmark
    fun genericDoubleBufferReadWrite() {
        val buffer = DoubleBuffer(size, Int::toDouble)

        (0 until size).forEach {
            buffer[it]
        }
    }

    @Benchmark
    fun complexBufferReadWrite() {
        val buffer =
            MutableBuffer.complex(MutableBuffer.Companion::double, size / 2) { Complex(it.toDouble(), -it.toDouble()) }

        (0 until size / 2).forEach {
            buffer[it]
        }
    }

    private companion object {
        private const val size = 100
    }
}
