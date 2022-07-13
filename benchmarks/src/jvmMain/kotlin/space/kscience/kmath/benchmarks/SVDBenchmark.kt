/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.svdGolabKahan
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.svd

@State(Scope.Benchmark)
class SVDBenchmark {
    companion object {
        val tensor = DoubleTensorAlgebra.randomNormal(intArrayOf(10, 10, 10), 0)
    }

    @Benchmark
    fun svdPowerMethod(blackhole: Blackhole) {
        blackhole.consume(
            tensor.svd()
        )
    }

    @Benchmark
    fun svdGolabKahan(blackhole: Blackhole) {
        blackhole.consume(
            tensor.svdGolabKahan()
        )
    }
}