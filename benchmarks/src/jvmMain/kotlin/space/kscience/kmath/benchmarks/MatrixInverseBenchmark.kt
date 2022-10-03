/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.commons.linear.lupSolver
import space.kscience.kmath.ejml.EjmlLinearSpaceDDRM
import space.kscience.kmath.linear.invoke
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.lupSolver
import space.kscience.kmath.operations.algebra
import kotlin.random.Random

@State(Scope.Benchmark)
internal class MatrixInverseBenchmark {
    private companion object {
        private val random = Random(1224)
        private const val dim = 100

        private val space = Double.algebra.linearSpace

        //creating invertible matrix
        private val u = space.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        private val l = space.buildMatrix(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        private val matrix = space { l dot u }
    }

    @Benchmark
    fun kmathLupInversion(blackhole: Blackhole) {
        blackhole.consume(Double.algebra.linearSpace.lupSolver().inverse(matrix))
    }

    @Benchmark
    fun cmLUPInversion(blackhole: Blackhole) {
        CMLinearSpace {
            blackhole.consume(lupSolver().inverse(matrix))
        }
    }

    @Benchmark
    fun ejmlInverse(blackhole: Blackhole) {
        EjmlLinearSpaceDDRM {
            blackhole.consume(matrix.toEjml().inverse())
        }
    }
}
