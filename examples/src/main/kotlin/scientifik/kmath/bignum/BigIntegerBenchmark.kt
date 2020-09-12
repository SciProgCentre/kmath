package scientifik.kmath.bignum

import com.ionspin.kotlin.bignum.integer.BigInteger
import scientifik.kmath.operations.BigIntField
import scientifik.kmath.operations.invoke
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private class BigIntegerBenchmark {
    fun java() {
        invokeAndSum { l, l2 -> (l.toBigInteger() * l2.toBigInteger()).toLong() }
    }

    fun bignum() {
        invokeAndSum { l, l2 -> (BigInteger.fromLong(l) * BigInteger.fromLong(l2)).longValue() }
    }

    fun bigint() {
        invokeAndSum { l, l2 -> BigIntField { number(l) * number(l2) }.toString().toLong() }
    }

    fun long() {
        invokeAndSum { l, l2 -> l * l2 }
    }

    private fun invokeAndSum(op: (Long, Long) -> Long) {
        val random = Random(0)
        var sum = 0.0

        repeat(1000000) {
            sum += op(random.nextInt().toLong(), random.nextInt().toLong())
        }

        println(sum)
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

    thread {
        val long = measureTimeMillis(benchmark::long)
        println("long=$long")
    }
}
