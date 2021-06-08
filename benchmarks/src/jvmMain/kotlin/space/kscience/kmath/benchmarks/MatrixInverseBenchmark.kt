/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.commons.linear.inverse
import space.kscience.kmath.ejml.EjmlLinearSpaceDDRM
import space.kscience.kmath.linear.InverseMatrixFeature
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.inverseWithLup
import space.kscience.kmath.linear.invoke
import space.kscience.kmath.nd.getFeature
import kotlin.random.Random

@State(Scope.Benchmark)
internal class MatrixInverseBenchmark {
    private companion object {
        private val random = Random(1224)
        private const val dim = 100

        private val space = LinearSpace.double

        //creating invertible matrix
        private val u = space.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        private val l = space.buildMatrix(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        private val matrix = space { l dot u }
    }

    @Benchmark
    fun kmathLupInversion(blackhole: Blackhole) {
        blackhole.consume(LinearSpace.double.inverseWithLup(matrix))
    }

    @Benchmark
    fun cmLUPInversion(blackhole: Blackhole) {
        with(CMLinearSpace) {
            blackhole.consume(inverse(matrix))
        }
    }

    @Benchmark
    fun ejmlInverse(blackhole: Blackhole) {
        with(EjmlLinearSpaceDDRM) {
            blackhole.consume(matrix.getFeature<InverseMatrixFeature<Double>>()?.inverse)
        }
    }
}
