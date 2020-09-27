package kscience.kmath.structures

import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
internal class NDFieldBenchmark {
    @Benchmark
    fun autoFieldAdd() {
        bufferedField {
            var res: NDBuffer<Double> = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun autoElementAdd() {
        var res = genericField.one
        repeat(n) { res += 1.0 }
    }

    @Benchmark
    fun specializedFieldAdd() {
        specializedField {
            var res: NDBuffer<Double> = one
            repeat(n) { res += 1.0 }
        }
    }


    @Benchmark
    fun boxingFieldAdd() {
        genericField {
            var res: NDBuffer<Double> = one
            repeat(n) { res += one }
        }
    }

    companion object {
        const val dim: Int = 1000
        const val n: Int = 100
        val bufferedField: BufferedNDField<Double, RealField> = NDField.auto(RealField, dim, dim)
        val specializedField: RealNDField = NDField.real(dim, dim)
        val genericField: BoxingNDField<Double, RealField> = NDField.boxing(RealField, dim, dim)
    }
}