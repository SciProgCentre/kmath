package space.kscience.kmath.benchmarks

import org.jetbrains.bio.viktor.F64Array
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.viktor.ViktorNDField

@State(Scope.Benchmark)
internal class ViktorLogBenchmark {
    final val dim: Int = 1000
    final val n: Int = 100

    // automatically build context most suited for given type.
    final val autoField: NDField<Double, RealField> = NDAlgebra.auto(RealField, dim, dim)
    final val realField: RealNDField = NDAlgebra.real(dim, dim)
    final val viktorField: ViktorNDField = ViktorNDField(intArrayOf(dim, dim))


    @Benchmark
    fun realFieldLog() {
        with(realField) {
            val fortyTwo = produce { 42.0 }
            var res = one
            repeat(n) { res = ln(fortyTwo) }
        }
    }

    @Benchmark
    fun viktorFieldLog() {
        with(viktorField) {
            val fortyTwo = produce { 42.0 }
            var res = one
            repeat(n) { res = ln(fortyTwo) }
        }
    }

    @Benchmark
    fun rawViktorLog() {
        val fortyTwo = F64Array.full(dim, dim, init = 42.0)
        var res: F64Array
        repeat(n) {
            res = fortyTwo.log()
        }
    }
}
