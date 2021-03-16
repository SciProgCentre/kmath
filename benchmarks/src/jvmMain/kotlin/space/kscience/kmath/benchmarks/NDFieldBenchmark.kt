/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.Buffer

@State(Scope.Benchmark)
internal class NDFieldBenchmark {
    @Benchmark
    fun autoFieldAdd(blackhole: Blackhole) {
        with(autoField) {
            var res: StructureND<Double> = one
            repeat(n) { res += one }
            blackhole.consume(res)
        }
    }

    @Benchmark
    fun specializedFieldAdd(blackhole: Blackhole) {
        with(specializedField) {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }


    @Benchmark
    fun boxingFieldAdd(blackhole: Blackhole) {
        with(genericField) {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
            blackhole.consume(res)
        }
    }

    private companion object {
        private const val dim = 1000
        private const val n = 100
        private val autoField = AlgebraND.auto(DoubleField, dim, dim)
        private val specializedField = AlgebraND.double(dim, dim)
        private val genericField = AlgebraND.field(DoubleField, Buffer.Companion::boxing, dim, dim)
    }
}
