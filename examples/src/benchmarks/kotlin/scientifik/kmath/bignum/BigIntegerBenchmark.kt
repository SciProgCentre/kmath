package scientifik.kmath.bignum

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import scientifik.kmath.operations.BigIntField
import scientifik.kmath.operations.JBigIntegerField
import scientifik.kmath.operations.invoke
import kotlin.random.Random

@State(Scope.Benchmark)
class BigIntegerBenchmark {
    var times: Int = 1000000

    @Benchmark
    fun java() {
        val random = Random(0)
        var sum = JBigIntegerField.zero
        repeat(times) { sum += JBigIntegerField { number(random.nextInt()) * random.nextInt() } }
        println("java:$sum")
    }

    @Benchmark
    fun bignum() {
        val random = Random(0)
        var sum = BigIntegerRing.zero
        repeat(times) { sum += BigIntegerRing { number(random.nextInt()) * random.nextInt() } }
        println("bignum:$sum")
    }

    @Benchmark
    fun bigint() {
        val random = Random(0)
        var sum = BigIntField.zero
        repeat(times) { sum += BigIntField { number(random.nextInt()) * random.nextInt() } }
        println("bigint:$sum")
    }
}
