package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.commons.linear.inverse
import space.kscience.kmath.ejml.EjmlLinearSpace
import space.kscience.kmath.ejml.inverse
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.inverseWithLup
import space.kscience.kmath.linear.real
import kotlin.random.Random

@State(Scope.Benchmark)
internal class LinearAlgebraBenchmark {
    companion object {
        val random = Random(1224)
        const val dim = 100

        //creating invertible matrix
        val u = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val l = Matrix.real(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        val matrix = l dot u
    }

    @Benchmark
    fun kmathLupInversion(blackhole: Blackhole) {
        blackhole.consume(LinearSpace.real.inverseWithLup(matrix))
    }

    @Benchmark
    fun cmLUPInversion(blackhole: Blackhole) {
        with(CMLinearSpace) {
            blackhole.consume(inverse(matrix))
        }
    }

    @Benchmark
    fun ejmlInverse(blackhole: Blackhole) {
        with(EjmlLinearSpace) {
            blackhole.consume(inverse(matrix))
        }
    }
}
