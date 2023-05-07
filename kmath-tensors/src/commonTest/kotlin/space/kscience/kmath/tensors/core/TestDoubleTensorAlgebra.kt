/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core


import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.internal.LMSettings
import space.kscience.kmath.testutils.assertBufferEquals
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TestDoubleTensorAlgebra {

    @Test
    fun testDoublePlus() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(2), doubleArrayOf(1.0, 2.0))
        val res = 10.0 + tensor
        assertTrue(res.source contentEquals doubleArrayOf(11.0, 12.0))
    }

    @Test
    fun testDoubleDiv() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(2), doubleArrayOf(2.0, 4.0))
        val res = 2.0 / tensor
        assertTrue(res.source contentEquals doubleArrayOf(1.0, 0.5))
    }

    @Test
    fun testDivDouble() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(2), doubleArrayOf(10.0, 5.0))
        val res = tensor / 2.5
        assertTrue(res.source contentEquals doubleArrayOf(4.0, 2.0))
    }

    @Test
    fun testTranspose1x1() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(1), doubleArrayOf(0.0))
        val res = tensor.transposed(0, 0)

        assertTrue(res.asDoubleTensor().source contentEquals doubleArrayOf(0.0))
        assertTrue(res.shape contentEquals ShapeND(1))
    }

    @Test
    fun testTranspose3x2() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res = tensor.transposed(1, 0)

        assertTrue(res.asDoubleTensor().source contentEquals doubleArrayOf(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        assertTrue(res.shape contentEquals ShapeND(2, 3))
    }

    @Test
    fun testTranspose1x2x3() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(1, 2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res01 = tensor.transposed(0, 1)
        val res02 = tensor.transposed(-3, 2)
        val res12 = tensor.transposed()

        assertTrue(res01.shape contentEquals ShapeND(2, 1, 3))
        assertTrue(res02.shape contentEquals ShapeND(3, 2, 1))
        assertTrue(res12.shape contentEquals ShapeND(1, 3, 2))

        assertTrue(res01.asDoubleTensor().source contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res02.asDoubleTensor().source contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(res12.asDoubleTensor().source contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
    }

    @Test
    fun testLinearStructure() = DoubleTensorAlgebra {
        val shape = ShapeND(3)
        val tensorA = full(value = -4.5, shape = shape)
        val tensorB = full(value = 10.9, shape = shape)
        val tensorC = full(value = 789.3, shape = shape)
        val tensorD = full(value = -72.9, shape = shape)
        val tensorE = full(value = 553.1, shape = shape)
        val result = 15.8 * tensorA - 1.5 * tensorB * (-tensorD) + 0.02 * tensorC / tensorE - 39.4

        val expected = fromArray(
            shape,
            (1..3).map {
                15.8 * (-4.5) - 1.5 * 10.9 * 72.9 + 0.02 * 789.3 / 553.1 - 39.4
            }.toDoubleArray()
        )

        val assignResult = zeros(shape)
        tensorA *= 15.8
        tensorB *= 1.5
        tensorB *= -tensorD
        tensorC *= 0.02
        tensorC /= tensorE
        assignResult += tensorA
        assignResult -= tensorB
        assignResult += tensorC
        assignResult += -39.4

        assertBufferEquals(expected.source, result.source)
        assertBufferEquals(expected.source, assignResult.source)
    }

    @Test
    fun testDot() = DoubleTensorAlgebra {
        val tensor1 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor11 = fromArray(ShapeND(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(ShapeND(3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(ShapeND(1, 1, 3), doubleArrayOf(-1.0, -2.0, -3.0))
        val tensor4 = fromArray(ShapeND(2, 3, 3), (1..18).map { it.toDouble() }.toDoubleArray())
        val tensor5 = fromArray(ShapeND(2, 3, 3), (1..18).map { 1 + it.toDouble() }.toDoubleArray())

        val res12 = tensor1.dot(tensor2)
        assertTrue(res12.source contentEquals doubleArrayOf(140.0, 320.0))
        assertTrue(res12.shape contentEquals ShapeND(2))

        val res32 = tensor3.matmul(tensor2)
        assertTrue(res32.source contentEquals doubleArrayOf(-140.0))
        assertTrue(res32.shape contentEquals ShapeND(1, 1))

        val res22 = tensor2.dot(tensor2)
        assertTrue(res22.source contentEquals doubleArrayOf(1400.0))
        assertTrue(res22.shape contentEquals ShapeND(1))

        val res11 = tensor1.dot(tensor11)
        assertTrue(res11.source contentEquals doubleArrayOf(22.0, 28.0, 49.0, 64.0))
        assertTrue(res11.shape contentEquals ShapeND(2, 2))

        val res45 = tensor4.matmul(tensor5)
        assertTrue(
            res45.source contentEquals doubleArrayOf(
                36.0, 42.0, 48.0, 81.0, 96.0, 111.0, 126.0, 150.0, 174.0,
                468.0, 501.0, 534.0, 594.0, 636.0, 678.0, 720.0, 771.0, 822.0
            )
        )
        assertTrue(res45.shape contentEquals ShapeND(2, 3, 3))
    }

    @Test
    fun testDiagonalEmbedding() = DoubleTensorAlgebra {
        val tensor1 = fromArray(ShapeND(3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor2 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor3 = zeros(ShapeND(2, 3, 4, 5))

        assertTrue(
            diagonalEmbedding(tensor3, 0, 3, 4).shape contentEquals
                    ShapeND(2, 3, 4, 5, 5)
        )
        assertTrue(
            diagonalEmbedding(tensor3, 1, 3, 4).shape contentEquals
                    ShapeND(2, 3, 4, 6, 6)
        )
        assertTrue(
            diagonalEmbedding(tensor3, 2, 0, 3).shape contentEquals
                    ShapeND(7, 2, 3, 7, 4)
        )

        val diagonal1 = diagonalEmbedding(tensor1, 0, 1, 0)
        assertTrue(diagonal1.shape contentEquals ShapeND(3, 3))
        assertTrue(
            diagonal1.source contentEquals
                    doubleArrayOf(10.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 30.0)
        )

        val diagonal1Offset = diagonalEmbedding(tensor1, 1, 1, 0)
        assertTrue(diagonal1Offset.shape contentEquals ShapeND(4, 4))
        assertTrue(
            diagonal1Offset.source contentEquals
                    doubleArrayOf(0.0, 0.0, 0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 0.0, 30.0, 0.0)
        )

        val diagonal2 = diagonalEmbedding(tensor2, 1, 0, 2)
        assertTrue(diagonal2.shape contentEquals ShapeND(4, 2, 4))
        assertTrue(
            diagonal2.source contentEquals
                    doubleArrayOf(
                        0.0, 1.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0,
                        0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 5.0, 0.0,
                        0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 6.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                    )
        )
    }

    @Test
    fun testEq() = DoubleTensorAlgebra {
        val tensor1 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor3 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 5.0))

        assertTrue(tensor1 eq tensor1)
        assertTrue(tensor1 eq tensor2)
        assertFalse(tensor1.eq(tensor3))

    }

    @Test
    fun testMap() = DoubleTensorAlgebra {
        val tensor = one(5, 5, 5)
        val l = tensor.getTensor(0).map { it + 1.0 }
        val r = tensor.getTensor(1).map { it - 1.0 }
        val res = l + r
        assertTrue { ShapeND(5, 5) contentEquals res.shape }
        assertEquals(2.0, res[4, 4])
    }

    @Test
    fun testLM() = DoubleTensorAlgebra {
        fun lm_func(t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, settings: LMSettings): MutableStructure2D<Double> {
            val m = t.shape.component1()
            var y_hat = DoubleTensorAlgebra.zeros(ShapeND(intArrayOf(m, 1)))

            if (settings.example_number == 1) {
                y_hat = DoubleTensorAlgebra.exp((t.times(-1.0 / p[1, 0]))).times(p[0, 0]) + t.times(p[2, 0]).times(
                    DoubleTensorAlgebra.exp((t.times(-1.0 / p[3, 0])))
                )
            }
            else if (settings.example_number == 2) {
                val mt = t.max()
                y_hat = (t.times(1.0 / mt)).times(p[0, 0]) +
                        (t.times(1.0 / mt)).pow(2).times(p[1, 0]) +
                        (t.times(1.0 / mt)).pow(3).times(p[2, 0]) +
                        (t.times(1.0 / mt)).pow(4).times(p[3, 0])
            }
            else if (settings.example_number == 3) {
                y_hat = DoubleTensorAlgebra.exp((t.times(-1.0 / p[1, 0])))
                    .times(p[0, 0]) + DoubleTensorAlgebra.sin((t.times(1.0 / p[3, 0]))).times(p[2, 0])
            }

            return y_hat.as2D()
        }

        val lm_matx_y_dat = doubleArrayOf(
            19.6594, 18.6096, 17.6792, 17.2747, 16.3065, 17.1458, 16.0467, 16.7023, 15.7809, 15.9807,
            14.7620, 15.1128, 16.0973, 15.1934, 15.8636, 15.4763, 15.6860, 15.1895, 15.3495, 16.6054,
            16.2247, 15.9854, 16.1421, 17.0960, 16.7769, 17.1997, 17.2767, 17.5882, 17.5378, 16.7894,
            17.7648, 18.2512, 18.1581, 16.7037, 17.8475, 17.9081, 18.3067, 17.9632, 18.2817, 19.1427,
            18.8130, 18.5658, 18.0056, 18.4607, 18.5918, 18.2544, 18.3731, 18.7511, 19.3181, 17.3066,
            17.9632, 19.0513, 18.7528, 18.2928, 18.5967, 17.8567, 17.7859, 18.4016, 18.9423, 18.4959,
            17.8000, 18.4251, 17.7829, 17.4645, 17.5221, 17.3517, 17.4637, 17.7563, 16.8471, 17.4558,
            17.7447, 17.1487, 17.3183, 16.8312, 17.7551, 17.0942, 15.6093, 16.4163, 15.3755, 16.6725,
            16.2332, 16.2316, 16.2236, 16.5361, 15.3721, 15.3347, 15.5815, 15.6319, 14.4538, 14.6044,
            14.7665, 13.3718, 15.0587, 13.8320, 14.7873, 13.6824, 14.2579, 14.2154, 13.5818, 13.8157
        )

        var example_number = 1
        val p_init = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(5.0, 2.0, 0.2, 10.0)
        ).as2D()

        var t = ones(ShapeND(intArrayOf(100, 1))).as2D()
        for (i in 0 until 100) {
            t[i, 0] = t[i, 0] * (i + 1)
        }

        val y_dat = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(100, 1)), lm_matx_y_dat
        ).as2D()

        val weight = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(1, 1)), DoubleArray(1) { 4.0 }
        ).as2D()

        val dp = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
        ).as2D()

        val p_min = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(-50.0, -20.0, -2.0, -100.0)
        ).as2D()

        val p_max = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(50.0, 20.0, 2.0, 100.0)
        ).as2D()

        val consts = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(1, 1)), doubleArrayOf(0.0)
        ).as2D()

        val opts = doubleArrayOf(3.0, 100.0, 1e-3, 1e-3, 1e-1, 1e-1, 1e-2, 11.0, 9.0, 1.0)

        val result = lm(::lm_func, p_init, t, y_dat, weight, dp, p_min, p_max, consts, opts, 10, example_number)
        assertEquals(13, result.iterations)
        assertEquals(31, result.func_calls)
        assertEquals(1, result.example_number)
        assertEquals(0.9131368192633, (result.result_chi_sq * 1e13).roundToLong() / 1e13)
        assertEquals(3.7790980 * 1e-7, (result.result_lambda * 1e13).roundToLong() / 1e13)
    }
}
