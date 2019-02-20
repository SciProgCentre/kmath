package scientifik.kmath.structures

import org.openjdk.jmh.annotations.Benchmark
import scientifik.kmath.operations.RealField

open class NDFieldBenchmark {

    @Benchmark
    fun autoFieldAdd() {
        bufferedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += one
            }
        }
    }

    @Benchmark
    fun autoElementAdd() {
        var res = genericField.one
        repeat(n) {
            res += 1.0
        }
    }

    @Benchmark
    fun specializedFieldAdd() {
        specializedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }


    @Benchmark
    fun lazyFieldAdd() {
        lazyNDField.run {
            var res = one
            repeat(n) {
                res += one
            }

            res.elements().sumByDouble { it.second }
        }
    }


    @Benchmark
    fun boxingFieldAdd() {
        genericField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += one
            }
        }
    }

    companion object {
        val dim = 1000
        val n = 100

        val bufferedField = NDField.auto(RealField, intArrayOf(dim, dim))
        val specializedField = NDField.real(intArrayOf(dim, dim))
        val genericField = NDField.buffered(intArrayOf(dim, dim), RealField)
        val lazyNDField = NDField.lazy(intArrayOf(dim, dim), RealField)
    }
}