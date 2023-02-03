/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.algebra
import space.kscience.kmath.integration.gaussIntegrator
import space.kscience.kmath.integration.integrate
import space.kscience.kmath.integration.value
import space.kscience.kmath.operations.algebra


@State(Scope.Benchmark)
internal class IntegrationBenchmark {

    @Benchmark
    fun doubleIntegration(blackhole: Blackhole) {
        val res = Double.algebra.gaussIntegrator.integrate(0.0..1.0, intervals = 1000) { x: Double ->
            //sin(1 / x)
            1/x
        }.value
        blackhole.consume(res)
    }

    @Benchmark
    fun complexIntegration(blackhole: Blackhole) = with(Complex.algebra) {
        val res = gaussIntegrator.integrate(0.0..1.0, intervals = 1000) { x: Double ->
//            sin(1 / x) + i * cos(1 / x)
            1/x - i/x
        }.value
        blackhole.consume(res)
    }
}