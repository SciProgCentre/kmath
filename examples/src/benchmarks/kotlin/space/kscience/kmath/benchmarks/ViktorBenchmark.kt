package space.kscience.kmath.benchmarks

import org.jetbrains.bio.viktor.F64Array
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.viktor.ViktorNDField

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
        with(autoField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    @Benchmark
    fun realFieldAddition() {
        with(realField) {
            var res: NDStructure<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    @Benchmark
    fun viktorFieldAddition() {
        with(viktorField) {
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
