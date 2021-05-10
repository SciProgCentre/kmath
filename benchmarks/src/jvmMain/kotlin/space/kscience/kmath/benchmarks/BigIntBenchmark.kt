/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks


import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.operations.BigInt
import space.kscience.kmath.operations.BigIntField
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.invoke


@State(Scope.Benchmark)
internal class BigIntBenchmark {

    val kmNumber = BigIntField.number(Int.MAX_VALUE)
    val jvmNumber = JBigIntegerField.number(Int.MAX_VALUE)
    val largeKmNumber = BigIntField { number(11).pow(100_000UL) }
    val largeJvmNumber = JBigIntegerField { number(11).pow(100_000) }
    val bigExponent = 50_000

    @Benchmark
    fun kmAdd(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber + kmNumber + kmNumber)
    }

    @Benchmark
    fun jvmAdd(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber + jvmNumber + jvmNumber)
    }

    @Benchmark
    fun kmMultiply(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber * kmNumber * kmNumber)
    }

    @Benchmark
    fun kmMultiplyLarge(blackhole: Blackhole) = BigIntField {
        blackhole.consume(largeKmNumber*largeKmNumber)
    }

    @Benchmark
    fun jvmMultiply(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber * jvmNumber * jvmNumber)
    }

    @Benchmark
    fun jvmMultiplyLarge(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(largeJvmNumber*largeJvmNumber)
    }

    @Benchmark
    fun kmPower(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber.pow(bigExponent.toULong()))
    }

    @Benchmark
    fun jvmPower(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber.pow(bigExponent))
    }
}
