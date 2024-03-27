/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.linear.symmetric
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.tensors.core.symEigJacobi
import space.kscience.kmath.tensors.core.symEigSvd
import space.kscience.kmath.tensors.core.tensorAlgebra
import kotlin.random.Random

@State(Scope.Benchmark)
internal class TensorAlgebraBenchmark {
    companion object {
        private val random = Random(12224)
        private const val dim = 30

        private val matrix = Float64Field.linearSpace.matrix(dim, dim).symmetric { _, _ -> random.nextDouble() }
    }

    @Benchmark
    fun tensorSymEigSvd(blackhole: Blackhole) = with(Double.tensorAlgebra) {
        blackhole.consume(symEigSvd(matrix, 1e-10))
    }

    @Benchmark
    fun tensorSymEigJacobi(blackhole: Blackhole) = with(Double.tensorAlgebra) {
        blackhole.consume(symEigJacobi(matrix, 50, 1e-10))
    }
}