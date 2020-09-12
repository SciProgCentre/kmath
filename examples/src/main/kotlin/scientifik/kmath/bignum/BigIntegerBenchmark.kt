package scientifik.kmath.bignum

import scientifik.kmath.operations.BigIntField
import scientifik.kmath.operations.JBigIntegerField
import scientifik.kmath.operations.invoke
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private class BigIntegerBenchmark {
    fun java() {
        val random = Random(0)
        var sum = JBigIntegerField.zero
        repeat(1000000) {
            sum += JBigIntegerField { number(random.nextInt()) * random.nextInt() }
        }
        println("java:$sum")
    }

    fun bignum() {
        val random = Random(0)
        var sum = BigIntegerRing.zero
        repeat(1000000) { sum += BigIntegerRing { number(random.nextInt()) * random.nextInt() } }
        println("bignum:$sum")
    }

    fun bigint() {
        val random = Random(0)
        var sum = BigIntField.zero
        repeat(1000000) { sum += BigIntField { number(random.nextInt()) * random.nextInt() } }
        println("bigint:$sum")
    }
}

fun main() {
    val benchmark = BigIntegerBenchmark()

    thread {
        val java = measureTimeMillis(benchmark::java)
        println("java=$java")
    }

    thread {
        val bignum = measureTimeMillis(benchmark::bignum)
        println("bignum=$bignum")
    }

    thread {
        val bigint = measureTimeMillis(benchmark::bigint)
        println("bigint=$bigint")
    }
}
