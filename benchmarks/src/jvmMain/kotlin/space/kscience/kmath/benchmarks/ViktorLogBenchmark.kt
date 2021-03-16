/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.auto
import space.kscience.kmath.nd.double
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.viktor.ViktorFieldND

@State(Scope.Benchmark)
internal class ViktorLogBenchmark {
    @Benchmark
    fun realFieldLog(blackhole: Blackhole) {
        with(realNdField) {
            val fortyTwo = produce { 42.0 }
            var res = one
            repeat(n) { res = ln(fortyTwo) }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun viktorFieldLog(blackhole: Blackhole) {
        with(viktorField) {
            val fortyTwo = produce { 42.0 }
            var res = one
            repeat(n) { res = ln(fortyTwo) }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun rawViktorLog(blackhole: Blackhole) {
        val fortyTwo = F64Array.full(dim, dim, init = 42.0)
        lateinit var res: F64Array
        repeat(n) { res = fortyTwo.log() }
        blackhole.consume(res)
    }

    private companion object {
        private const val dim = 1000
        private const val n = 100

        // automatically build context most suited for given type.
        private val autoField = AlgebraND.auto(DoubleField, dim, dim)
        private val realNdField = AlgebraND.double(dim, dim)
        private val viktorField = ViktorFieldND(intArrayOf(dim, dim))
    }
}
