package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.commons.linear.CMMatrixContext
import space.kscience.kmath.commons.linear.CMMatrixContext.dot
import space.kscience.kmath.commons.linear.inverse
import space.kscience.kmath.ejml.EjmlMatrixContext
import space.kscience.kmath.ejml.inverse
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.MatrixContext
import space.kscience.kmath.linear.inverseWithLup
import space.kscience.kmath.linear.real
import kotlin.random.Random

@State(Scope.Benchmark)
internal class LinearAlgebraBenchmark {
    companion object {
        val random = Random(1224)
        val dim = 100

        //creating invertible matrix
        val u = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val l = Matrix.real(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        val matrix = l dot u
    }

    @Benchmark
    fun kmathLupInversion() {
        MatrixContext.real.inverseWithLup(matrix)
    }

    @Benchmark
    fun cmLUPInversion() {
        with(CMMatrixContext) {
            inverse(matrix)
        }
    }

    @Benchmark
    fun ejmlInverse() {
        with(EjmlMatrixContext) {
            inverse(matrix)
        }
    }
}
