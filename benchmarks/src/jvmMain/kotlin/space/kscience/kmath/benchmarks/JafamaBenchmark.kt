/*
 * Copyright 2018-2022 KMath contributors.
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
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random

@State(Scope.Benchmark)
internal class JafamaBenchmark {
    @Benchmark
    fun jafama(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        JafamaDoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }

    @Benchmark
    fun core(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        DoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }

    @Benchmark
    fun strictJafama(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        StrictJafamaDoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }
}

private inline fun invokeBenchmarks(blackhole: Blackhole, expr: (Double) -> Double) {
    contract { callsInPlace(expr, InvocationKind.AT_LEAST_ONCE) }
    val rng = Random(0)
    repeat(1000000) { blackhole.consume(expr(rng.nextDouble())) }
}
