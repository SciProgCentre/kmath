/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.operations.BigIntField
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.invoke

@State(Scope.Benchmark)
internal class BigIntBenchmark {

    val kmNumber = BigIntField.number(Int.MAX_VALUE)
    val jvmNumber = JBigIntegerField.number(Int.MAX_VALUE)

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
    fun jvmMultiply(blackhole: Blackhole) = JBigIntegerField {
        blackhole.consume(jvmNumber * jvmNumber * jvmNumber)
    }
}