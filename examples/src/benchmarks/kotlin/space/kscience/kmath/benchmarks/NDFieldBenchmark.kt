package space.kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.structures.Buffer

@State(Scope.Benchmark)
internal class NDFieldBenchmark {
    @Benchmark
    fun autoFieldAdd() {
        with(autoField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun specializedFieldAdd() {
        with(specializedField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }


    @Benchmark
    fun boxingFieldAdd() {
        with(genericField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    companion object {
        const val dim: Int = 1000
        const val n: Int = 100
        val autoField = NDAlgebra.auto(RealField, dim, dim)
        val specializedField: RealNDField = NDAlgebra.real(dim, dim)
        val genericField = NDAlgebra.field(RealField, Buffer.Companion::boxing, dim, dim)
    }
}
