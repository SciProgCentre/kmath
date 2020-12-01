package kscience.kmath.linear


import kotlinx.benchmark.Benchmark
import kscience.kmath.commons.linear.CMMatrixContext
import kscience.kmath.commons.linear.CMMatrixContext.dot
import kscience.kmath.commons.linear.inverse
import kscience.kmath.commons.linear.toCM
import kscience.kmath.ejml.EjmlMatrixContext
import kscience.kmath.ejml.inverse
import kscience.kmath.ejml.toEjml
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Benchmark)
class LinearAlgebraBenchmark {
    companion object {
        val random = Random(1224)
        val dim = 100

        //creating invertible matrix
        val u = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val l = Matrix.real(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        val matrix = l dot u
    }

    @Benchmark
    fun kmathLUPInversion() {
        MatrixContext.real.inverseWithLUP(matrix)
    }

    @Benchmark
    fun cmLUPInversion() {
        CMMatrixContext {
            val cm = matrix.toCM()             //avoid overhead on conversion
            inverse(cm)
        }
    }

    @Benchmark
    fun ejmlInverse() {
        EjmlMatrixContext {
            val km = matrix.toEjml()      //avoid overhead on conversion
            inverse(km)
        }
    }
}