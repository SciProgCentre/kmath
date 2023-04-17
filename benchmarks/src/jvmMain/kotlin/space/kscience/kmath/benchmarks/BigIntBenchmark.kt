/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks


import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.BigIntField
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.parseBigInteger
import java.math.BigInteger


@UnstableKMathAPI
@State(Scope.Benchmark)
internal class BigIntBenchmark {

    val kmSmallNumber = BigIntField.number(100)
    val jvmSmallNumber = JBigIntegerField.number(100)
    val kmNumber = BigIntField.number(Int.MAX_VALUE)
    val jvmNumber = JBigIntegerField.number(Int.MAX_VALUE)
    val kmLargeNumber = BigIntField { number(11).pow(100_000U) }
    val jvmLargeNumber: BigInteger = JBigIntegerField { number(11).pow(100_000) }
    val bigExponent = 50_000

    @Benchmark
    fun kmSmallAdd(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmSmallNumber + kmSmallNumber + kmSmallNumber)
    }

    @Benchmark
    fun jvmSmallAdd(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmSmallNumber + jvmSmallNumber + jvmSmallNumber)
    }

    @Benchmark
    fun kmAdd(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber + kmNumber + kmNumber)
    }

    @Benchmark
    fun jvmAdd(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber + jvmNumber + jvmNumber)
    }

    @Benchmark
    fun kmAddLarge(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmLargeNumber + kmLargeNumber + kmLargeNumber)
    }

    @Benchmark
    fun jvmAddLarge(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmLargeNumber + jvmLargeNumber + jvmLargeNumber)
    }

    @Benchmark
    fun kmMultiply(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber * kmNumber * kmNumber)
    }

    @Benchmark
    fun kmMultiplyLarge(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmLargeNumber*kmLargeNumber)
    }

    @Benchmark
    fun jvmMultiply(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber * jvmNumber * jvmNumber)
    }

    @Benchmark
    fun jvmMultiplyLarge(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmLargeNumber*jvmLargeNumber)
    }

    @Benchmark
    fun kmPower(blackhole: Blackhole) = BigIntField {
        blackhole.consume(kmNumber.pow(bigExponent.toUInt()))
    }

    @Benchmark
    fun jvmPower(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber.pow(bigExponent))
    }

    @Benchmark
    fun kmParsing16(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume("0x7f57ed8b89c29a3b9a85c7a5b84ca3929c7b7488593".parseBigInteger())
    }

    @Benchmark
    fun kmParsing10(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume("236656783929183747565738292847574838922010".parseBigInteger())
    }

    @Benchmark
    fun jvmParsing10(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume("236656783929183747565738292847574838922010".toBigInteger(10))
    }

    @Benchmark
    fun jvmParsing16(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume("7f57ed8b89c29a3b9a85c7a5b84ca3929c7b7488593".toBigInteger(16))
    }
}
