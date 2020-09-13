package scientifik.kmath.bignum

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import scientifik.kmath.operations.JBigDecimalField
import scientifik.kmath.operations.invoke
import kotlin.random.Random

@State(Scope.Benchmark)
class BigDecimalBenchmark {
    final var times: Int = 10000

    @Benchmark
    fun java() {
        val random = Random(0)
        var sum = JBigDecimalField.zero

        repeat(times) {
            sum += JBigDecimalField {
                number(random.nextDouble(-1000000000.0, 1000000000.0)) / random.nextDouble(-1000000.0, 1000000.0)
            }
        }

        println("java:$sum")
    }

    @Benchmark
    fun bignum() {
        val random = Random(0)
        var sum = BigDecimalField.zero

        repeat(times) {
            sum += BigDecimalField {
                number(random.nextDouble(-1000000000.0, 1000000000.0)) / random.nextDouble(-1000000.0, 1000000.0)
            }
        }

        println("bignum:$sum")
    }
}
