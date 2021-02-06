package kscience.kmath.benchmarks

import kscience.kmath.nd.*
import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kscience.kmath.viktor.ViktorNDField
import org.jetbrains.bio.viktor.F64Array
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
internal class ViktorBenchmark {
    final val dim: Int = 1000
    final val n: Int = 100

    // automatically build context most suited for given type.
    final val autoField: NDField<Double, RealField> = NDAlgebra.auto(RealField, dim, dim)
    final val realField: RealNDField = NDAlgebra.real(dim, dim)
    final val viktorField: ViktorNDField = ViktorNDField(dim, dim)

    @Benchmark
    fun automaticFieldAddition() {
        autoField {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    @Benchmark
    fun realFieldAddition() {
        realField {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    @Benchmark
    fun viktorFieldAddition() {
        viktorField {
            var res = one
            repeat(n) { res += 1.0 }
        }
    }

    @Benchmark
    fun rawViktor() {
        val one = F64Array.full(init = 1.0, shape = intArrayOf(dim, dim))
        var res = one
        repeat(n) { res = res + one }
    }
}