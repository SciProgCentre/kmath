package scientifik.kmath.structures

import org.jetbrains.bio.viktor.F64Array
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import scientifik.kmath.operations.RealField
import scientifik.kmath.viktor.ViktorNDField


@State(Scope.Benchmark)
class ViktorBenchmark {
    final val dim = 1000
    final val n = 100

    // automatically build context most suited for given type.
    final val autoField = NDField.auto(RealField, dim, dim)
    final val realField = NDField.real(dim, dim)

    final val viktorField = ViktorNDField(intArrayOf(dim, dim))

    @Benchmark
    fun automaticFieldAddition() {
        autoField.run {
            var res = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun viktorFieldAddition() {
        viktorField.run {
            var res = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun rawViktor() {
        val one = F64Array.full(init = 1.0, shape = *intArrayOf(dim, dim))
        var res = one
        repeat(n) { res = res + one }
    }

    @Benchmark
    fun realdFieldLog() {
        realField.run {
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