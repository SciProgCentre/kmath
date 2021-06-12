/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.jafama.JafamaDoubleField
import space.kscience.kmath.jafama.StrictJafamaDoubleField
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow


@State(Scope.Benchmark)
internal class JafamaBenchmark {
    @Benchmark
    fun jafamaBench(blackhole: Blackhole) = invokeBenchmarks(jafama, blackhole)

    @Benchmark
    fun coreBench(blackhole: Blackhole) = invokeBenchmarks(core,blackhole)

    @Benchmark
    fun strictJafamaBench(blackhole: Blackhole) = invokeBenchmarks(strictJafama,blackhole)

    @Benchmark
    fun kotlinMathBench(blackhole: Blackhole) = invokeBenchmarks(kotlinMath, blackhole)

    private fun invokeBenchmarks(expr: Double, blackhole: Blackhole) {
        blackhole.consume(expr)
    }

    private companion object {
        private val x: Double = Double.MAX_VALUE

        private val jafama = JafamaDoubleField{
            x * power(x, 1_000_000) * exp(x) / cos(x)
        }

        private val kotlinMath = x * x.pow(1_000_000) * exp(x) / cos(x)

        private val core = DoubleField {
            x * power(x, 1_000_000) * exp(x) / cos(x)
        }

        private val strictJafama = StrictJafamaDoubleField {
            x * power(x, 1_000_000) * exp(x) / cos(x)
        }
    }
}
