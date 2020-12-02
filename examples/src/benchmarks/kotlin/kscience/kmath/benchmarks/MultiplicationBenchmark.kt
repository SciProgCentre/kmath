package kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kscience.kmath.commons.linear.CMMatrixContext
import kscience.kmath.commons.linear.CMMatrixContext.dot
import kscience.kmath.commons.linear.toCM
import kscience.kmath.ejml.EjmlMatrixContext
import kscience.kmath.ejml.toEjml
import kscience.kmath.linear.real
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Benchmark)
class MultiplicationBenchmark {
    companion object {
        val random = Random(12224)
        val dim = 1000

        //creating invertible matrix
        val matrix1 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val matrix2 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }

        val cmMatrix1 = matrix1.toCM()
        val cmMatrix2 = matrix2.toCM()

        val ejmlMatrix1 = matrix1.toEjml()
        val ejmlMatrix2 = matrix2.toEjml()
    }

    @Benchmark
    fun commonsMathMultiplication() {
        CMMatrixContext.invoke {
            cmMatrix1 dot cmMatrix2
        }
    }

    @Benchmark
    fun ejmlMultiplication() {
        EjmlMatrixContext.invoke {
            ejmlMatrix1 dot ejmlMatrix2
        }
    }

    @Benchmark
    fun ejmlMultiplicationwithConversion() {
        val ejmlMatrix1 = matrix1.toEjml()
        val ejmlMatrix2 = matrix2.toEjml()
        EjmlMatrixContext.invoke {
            ejmlMatrix1 dot ejmlMatrix2
        }
    }

    @Benchmark
    fun bufferedMultiplication() {
        matrix1 dot matrix2
    }
}