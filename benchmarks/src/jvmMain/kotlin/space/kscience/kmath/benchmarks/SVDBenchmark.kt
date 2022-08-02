/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.ejml.UtilEjml.assertTrue
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.diagonalEmbedding
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.dot
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.eq
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.svdGolubKahan
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.transpose
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.svdPowerMethod

@State(Scope.Benchmark)
class SVDBenchmark {
    companion object {
        val tensorSmall = DoubleTensorAlgebra.randomNormal(intArrayOf(5, 5), 0)
        val tensorMedium = DoubleTensorAlgebra.randomNormal(intArrayOf(10, 10), 0)
        val tensorLarge = DoubleTensorAlgebra.randomNormal(intArrayOf(50, 50), 0)
        val tensorVeryLarge = DoubleTensorAlgebra.randomNormal(intArrayOf(100, 100), 0)
        val epsilon = 1e-9
    }

    @Benchmark
    fun svdPowerMethodSmall(blackhole: Blackhole) {
        val svd = tensorSmall.svdPowerMethod()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorSmall, epsilon))
        blackhole.consume(
            tensorSmall.svdPowerMethod()
        )
    }

    @Benchmark
    fun svdPowerMethodMedium(blackhole: Blackhole) {
        val svd = tensorMedium.svdPowerMethod()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorMedium, epsilon))
        blackhole.consume(
            tensorMedium.svdPowerMethod()
        )
    }

    @Benchmark
    fun svdPowerMethodLarge(blackhole: Blackhole) {
        val svd = tensorLarge.svdPowerMethod()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorLarge, epsilon))
        blackhole.consume(
            tensorLarge.svdPowerMethod()
        )
    }

    @Benchmark
    fun svdPowerMethodVeryLarge(blackhole: Blackhole) {
        val svd = tensorVeryLarge.svdPowerMethod()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorVeryLarge, epsilon))
        blackhole.consume(
            tensorVeryLarge.svdPowerMethod()
        )
    }

    @Benchmark
    fun svdGolubKahanSmall(blackhole: Blackhole) {
        val svd = tensorSmall.svdGolubKahan()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorSmall, epsilon))
        blackhole.consume(
            tensorSmall.svdGolubKahan()
        )
    }

    @Benchmark
    fun svdGolubKahanMedium(blackhole: Blackhole) {
        val svd = tensorMedium.svdGolubKahan()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorMedium, epsilon))
        blackhole.consume(
            tensorMedium.svdGolubKahan()
        )
    }

    @Benchmark
    fun svdGolubKahanLarge(blackhole: Blackhole) {
        val svd = tensorLarge.svdGolubKahan()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorLarge, epsilon))
        blackhole.consume(
            tensorLarge.svdGolubKahan()
        )
    }

    @Benchmark
    fun svdGolubKahanVeryLarge(blackhole: Blackhole) {
        val svd = tensorVeryLarge.svdGolubKahan()
        val tensorSVD = svd.first
            .dot(
                diagonalEmbedding(svd.second)
                    .dot(svd.third.transpose())
            )
        assertTrue(tensorSVD.eq(tensorVeryLarge, epsilon))
        blackhole.consume(
            tensorVeryLarge.svdGolubKahan()
        )
    }
}