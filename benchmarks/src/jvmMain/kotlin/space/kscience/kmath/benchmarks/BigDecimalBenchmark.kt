/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import java.math.BigDecimal as JBigDecimal

@UnstableKMathAPI
@State(Scope.Benchmark)
internal class BigDecimalBenchmark {

    val kmSmallNumber = BigDecimalField.number(100)
    val jvmSmallNumber: JBigDecimal = JBigDecimalField.number(100)
    val kmNumber = BigDecimalField.number(Int.MAX_VALUE)
    val jvmNumber: JBigDecimal = JBigDecimalField.number(Int.MAX_VALUE)
    val kmLargeNumber = BigDecimalField { number(11).pow(100_000) }
    val jvmLargeNumber: JBigDecimal = JBigDecimalField { number(11).pow(100_000) }
    val bigExponent = 50_000

    @Benchmark
    fun kmSmallAdd(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmSmallNumber + kmSmallNumber + kmSmallNumber)
    }

    @Benchmark
    fun jvmSmallAdd(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmSmallNumber + jvmSmallNumber + jvmSmallNumber)
    }

    @Benchmark
    fun kmAdd(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmNumber + kmNumber + kmNumber)
    }

    @Benchmark
    fun jvmAdd(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmNumber + jvmNumber + jvmNumber)
    }

    @Benchmark
    fun kmAddLarge(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmLargeNumber + kmLargeNumber + kmLargeNumber)
    }

    @Benchmark
    fun jvmAddLarge(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmLargeNumber + jvmLargeNumber + jvmLargeNumber)
    }

    @Benchmark
    fun kmMultiply(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmNumber * kmNumber * kmNumber)
    }

    @Benchmark
    fun kmMultiplyLarge(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmLargeNumber * kmLargeNumber)
    }

    @Benchmark
    fun jvmMultiply(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmNumber * jvmNumber * jvmNumber)
    }

    @Benchmark
    fun jvmMultiplyLarge(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmLargeNumber * jvmLargeNumber)
    }

    @Benchmark
    fun kmDivide(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmNumber / kmSmallNumber)
    }

    @Benchmark
    fun jvmDivide(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmNumber / jvmSmallNumber)
    }

    @Benchmark
    fun kmPower(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume(kmNumber.pow(bigExponent))
    }

    @Benchmark
    fun jvmPower(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume(jvmNumber.pow(bigExponent))
    }

    @Benchmark
    fun kmParsing10(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume("236656783929183747565738292847574838922010".parseBigDecimal())
    }

    @Benchmark
    fun jvmParsing10(blackhole: Blackhole) = JBigDecimalField {
        blackhole.consume("236656783929183747565738292847574838922010".toBigDecimal())
    }

    @Benchmark
    fun kmParsingDecimal(blackhole: Blackhole) = BigDecimalField {
        blackhole.consume("236656783929183747565738292847574838922010.123456789".parseBigDecimal())
    }

    @Benchmark
    fun jvmParsingDecimal(blackhole: Blackhole) {
        blackhole.consume("236656783929183747565738292847574838922010.123456789".toBigDecimal())
    }
}

fun main() {
    val number = BigDecimal.valueOf(100.0)
    var res = BigDecimal.ZERO
    repeat(10_000_000){
        res += number
    }
    println(res)
}
