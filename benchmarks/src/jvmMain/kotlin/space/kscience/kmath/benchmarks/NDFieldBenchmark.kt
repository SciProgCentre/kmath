/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ones
import org.jetbrains.kotlinx.multik.ndarray.data.DN
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.nd4j.nd4j
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.one
import space.kscience.kmath.tensors.core.tensorAlgebra
import space.kscience.kmath.viktor.viktorAlgebra

@State(Scope.Benchmark)
internal class NDFieldBenchmark {

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
    fun multikAdd(blackhole: Blackhole) = with(multikAlgebra) {
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

    @OptIn(UnsafeKMathAPI::class)
    @Benchmark
    fun multikInPlaceAdd(blackhole: Blackhole) = with(multikAlgebra) {
        val res = Multik.ones<Double, DN>(shape.asArray(), DataType.DoubleDataType).wrap()
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
        private val shape = ShapeND(dim, dim)
        private val specializedField = DoubleField.ndAlgebra
        private val genericField = BufferedFieldOpsND(DoubleField)
        private val nd4jField = DoubleField.nd4j
        private val viktorField = DoubleField.viktorAlgebra
    }
}
