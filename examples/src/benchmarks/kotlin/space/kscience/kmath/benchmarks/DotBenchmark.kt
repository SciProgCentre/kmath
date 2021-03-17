package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.ejml.EjmlLinearSpace
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.invoke
import space.kscience.kmath.operations.DoubleField
import kotlin.random.Random

@State(Scope.Benchmark)
internal class DotBenchmark {
    companion object {
        val random = Random(12224)
        const val dim = 1000

        //creating invertible matrix
        val matrix1 = LinearSpace.real.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val matrix2 = LinearSpace.real.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }

        val cmMatrix1 = CMLinearSpace { matrix1.toCM() }
        val cmMatrix2 = CMLinearSpace { matrix2.toCM() }

        val ejmlMatrix1 = EjmlLinearSpace { matrix1.toEjml() }
        val ejmlMatrix2 = EjmlLinearSpace { matrix2.toEjml() }
    }

    @Benchmark
    fun cmDot(blackhole: Blackhole) {
        CMLinearSpace.run {
            blackhole.consume(cmMatrix1 dot cmMatrix2)
        }
    }

    @Benchmark
    fun ejmlDot(blackhole: Blackhole) {
        EjmlLinearSpace {
            blackhole.consume(ejmlMatrix1 dot ejmlMatrix2)
        }
    }

    @Benchmark
    fun ejmlDotWithConversion(blackhole: Blackhole) {
        EjmlLinearSpace {
            blackhole.consume(matrix1 dot matrix2)
        }
    }

    @Benchmark
    fun bufferedDot(blackhole: Blackhole) {
        LinearSpace.auto(DoubleField).invoke {
            blackhole.consume(matrix1 dot matrix2)
        }
    }

    @Benchmark
    fun realDot(blackhole: Blackhole) {
        LinearSpace.real {
            blackhole.consume(matrix1 dot matrix2)
        }
    }
}
