/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.ndAlgebra
import space.kscience.kmath.nd.one
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.viktor.ViktorFieldND

@State(Scope.Benchmark)
internal class ViktorBenchmark {

    @Benchmark
    fun doubleFieldAddition(blackhole: Blackhole) {
        with(doubleField) {
            var res: StructureND<Double> = one(shape)
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun viktorFieldAddition(blackhole: Blackhole) {
        with(viktorField) {
            var res = one(shape)
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun rawViktor(blackhole: Blackhole) {
        val one = F64Array.full(init = 1.0, shape = intArrayOf(dim, dim))
        var res = one
        repeat(n) { res = res + one }
        blackhole.consume(res)
    }

    private companion object {
        private const val dim = 1000
        private const val n = 100
        private val shape = ShapeND(dim, dim)

        // automatically build context most suited for given type.
        private val doubleField = DoubleField.ndAlgebra
        private val viktorField = ViktorFieldND(dim, dim)
    }
}
