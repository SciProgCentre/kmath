package kscience.kmath.benchmarks

import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kscience.kmath.structures.BufferedNDField
import kscience.kmath.structures.NDField
import kscience.kmath.structures.RealNDField
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
    final val autoField: BufferedNDField<Double, RealField> = NDField.auto(RealField, dim, dim)
    final val realField: RealNDField = NDField.real(dim, dim)
    final val viktorField: ViktorNDField = ViktorNDField(intArrayOf(dim, dim))

    @Benchmark
    fun automaticFieldAddition() {
        autoField {
            var res = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun viktorFieldAddition() {
        viktorField {
            var res = one
            repeat(n) { res += one }
        }
    }

    @Benchmark
    fun rawViktor() {
        val one = F64Array.full(init = 1.0, shape = intArrayOf(dim, dim))
        var res = one
        repeat(n) { res = res + one }
    }

    @Benchmark
    fun realFieldLog() {
        realField {
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