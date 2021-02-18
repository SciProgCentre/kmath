package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.commons.linear.CMMatrixContext
import space.kscience.kmath.ejml.EjmlMatrixContext
import space.kscience.kmath.linear.BufferMatrixContext
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.RealMatrixContext
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.random.Random

@State(Scope.Benchmark)
internal class DotBenchmark {
    companion object {
        val random = Random(12224)
        val dim = 1000

        //creating invertible matrix
        val matrix1 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val matrix2 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }

        val cmMatrix1 = CMMatrixContext { matrix1.toCM() }
        val cmMatrix2 = CMMatrixContext { matrix2.toCM() }

        val ejmlMatrix1 = EjmlMatrixContext { matrix1.toEjml() }
        val ejmlMatrix2 = EjmlMatrixContext { matrix2.toEjml() }
    }

    @Benchmark
    fun cmDot() {
        CMMatrixContext {
            cmMatrix1 dot cmMatrix2
        }
    }

    @Benchmark
    fun ejmlDot() {
        EjmlMatrixContext {
            ejmlMatrix1 dot ejmlMatrix2
        }
    }

    @Benchmark
    fun ejmlDotWithConversion() {
        EjmlMatrixContext {
            matrix1 dot matrix2
        }
    }

    @Benchmark
    fun bufferedDot() {
        BufferMatrixContext(RealField, Buffer.Companion::real).invoke {
            matrix1 dot matrix2
        }
    }

    @Benchmark
    fun realDot() {
        RealMatrixContext {
            matrix1 dot matrix2
        }
    }
}
