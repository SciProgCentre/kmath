/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.multik.multikND
import space.kscience.kmath.nd.BufferedFieldOpsND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.ndAlgebra
import space.kscience.kmath.nd.one
import space.kscience.kmath.nd4j.nd4j
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.one
import space.kscience.kmath.tensors.core.tensorAlgebra
import space.kscience.kmath.viktor.viktorAlgebra

@State(Scope.Benchmark)
internal class NDFieldBenchmark {
    @Benchmark
    fun autoFieldAdd(blackhole: Blackhole) = with(autoField) {
        var res: StructureND<Double> = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun specializedFieldAdd(blackhole: Blackhole) = with(specializedField) {
        var res: StructureND<Double> = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun boxingFieldAdd(blackhole: Blackhole) = with(genericField) {
        var res: StructureND<Double> = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun multikAdd(blackhole: Blackhole) = with(multikField) {
        var res: StructureND<Double> = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun viktorAdd(blackhole: Blackhole) = with(viktorField) {
        var res: StructureND<Double> = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun tensorAdd(blackhole: Blackhole) = with(Double.tensorAlgebra) {
        var res: DoubleTensor = one(shape)
        repeat(n) { res = res + 1.0 }
        blackhole.consume(res)
    }

    @Benchmark
    fun tensorInPlaceAdd(blackhole: Blackhole) = with(Double.tensorAlgebra) {
        val res: DoubleTensor = one(shape)
        repeat(n) { res += 1.0 }
        blackhole.consume(res)
    }

//    @Benchmark
//    fun nd4jAdd(blackhole: Blackhole) = with(nd4jField) {
//        var res: StructureND<Double> = one(dim, dim)
//        repeat(n) { res += 1.0 }
//        blackhole.consume(res)
//    }

    private companion object {
        private const val dim = 1000
        private const val n = 100
        private val shape = intArrayOf(dim, dim)
        private val autoField = BufferedFieldOpsND(DoubleField, Buffer.Companion::auto)
        private val specializedField = DoubleField.ndAlgebra
        private val genericField = BufferedFieldOpsND(DoubleField, Buffer.Companion::boxing)
        private val nd4jField = DoubleField.nd4j
        private val multikField = DoubleField.multikND
        private val viktorField = DoubleField.viktorAlgebra
    }
}
