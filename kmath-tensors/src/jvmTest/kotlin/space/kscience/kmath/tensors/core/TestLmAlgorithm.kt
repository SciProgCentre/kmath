/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Float64Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

@PerformancePitfall
class TestLmAlgorithm {
    companion object {
        fun funcEasyForLm(
            t: MutableStructure2D<Double>,
            p: MutableStructure2D<Double>,
            exampleNumber: Int,
        ): MutableStructure2D<Double> = with(DoubleTensorAlgebra) {
            val m = t.shape.component1()
            val yHat = when (exampleNumber) {
                1 -> exp((t * (-1.0 / p[1, 0]))) * p[0, 0] + (t * p[2, 0]) * exp((t * (-1.0 / p[3, 0])))

                2 -> {
                    val mt = t.max()
                    (t * (1.0 / mt)) * p[0, 0] +
                            (t * (1.0 / mt)).pow(2) * p[1, 0] +
                            (t * (1.0 / mt)).pow(3) * p[2, 0] +
                            (t * (1.0 / mt)).pow(4) * p[3, 0]
                }

                3 -> exp(t * (-1.0 / p[1, 0])) * p[0, 0] +
                        sin((t * (1.0 / p[3, 0]))) * p[2, 0]

                else -> zeros(ShapeND(intArrayOf(m, 1)))
            }

            return yHat.as2D()
        }

        fun funcMiddleForLm(
            t: MutableStructure2D<Double>,
            p: MutableStructure2D<Double>,
            exampleNumber: Int,
        ): MutableStructure2D<Double> = with(DoubleTensorAlgebra) {
            val m = t.shape.component1()
            var yHat = zeros(ShapeND(intArrayOf(m, 1)))

            val mt = t.max()
            for (i in 0 until p.shape.component1()) {
                yHat.plusAssign(t * (1.0 / mt) * p[i, 0])
            }

            for (i in 0 until 5) {
                yHat = funcEasyForLm(yHat.as2D(), p, exampleNumber).asDoubleTensor()
            }

            return yHat.as2D()
        }

        fun funcDifficultForLm(
            t: MutableStructure2D<Double>,
            p: MutableStructure2D<Double>,
            exampleNumber: Int,
        ): MutableStructure2D<Double> = with(DoubleTensorAlgebra) {
            val m = t.shape.component1()
            var yHat = zeros(ShapeND(intArrayOf(m, 1)))

            val mt = t.max()
            for (i in 0 until p.shape.component1()) {
                yHat = yHat + (t * (1.0 / mt)) * p[i, 0]
            }

            for (i in 0 until 4) {
                yHat = funcEasyForLm((yHat.as2D() + t).as2D(), p, exampleNumber).asDoubleTensor()
            }

            return yHat.as2D()
        }
    }

    @Test
    fun testLMEasy() = DoubleTensorAlgebra {
        val lmMatxYDat = doubleArrayOf(
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

        val exampleNumber = 1
        val pInit = fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(5.0, 2.0, 0.2, 10.0)
        ).as2D()

        val t = ones(ShapeND(intArrayOf(100, 1))).as2D()
        for (i in 0 until 100) {
            t[i, 0] = t[i, 0] * (i + 1)
        }

        val yDat = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(100, 1)), lmMatxYDat
        ).as2D()

        val weight = 4.0

        val dp = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
        ).as2D()

        val pMin = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(-50.0, -20.0, -2.0, -100.0)
        ).as2D()

        val pMax = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(50.0, 20.0, 2.0, 100.0)
        ).as2D()

        val inputData = LMInput(
            Companion::funcEasyForLm, pInit, t, yDat, weight, dp, pMin, pMax, 100,
            doubleArrayOf(1e-3, 1e-3, 1e-1, 1e-1), doubleArrayOf(1e-2, 11.0, 9.0), 1, 10, exampleNumber
        )

        val result = levenbergMarquardt(inputData)
        assertEquals(13, result.iterations)
        assertEquals(31, result.funcCalls)
        assertEquals(0.9131368192633, result.resultChiSq, 1e-13)
        assertEquals(3.7790980 * 1e-7, result.resultLambda, 1e-13)
        assertEquals(result.typeOfConvergence, TypeOfConvergence.InParameters)
        val expectedParameters = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(20.527230909086, 9.833627103230, 0.997571256572, 50.174445822506)
        ).as2D()
        result.resultParameters = result.resultParameters.map { x -> (x * 1e12).toLong() / 1e12 }.as2D()
        val receivedParameters = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(4, 1)), doubleArrayOf(
                result.resultParameters[0, 0], result.resultParameters[1, 0],
                result.resultParameters[2, 0], result.resultParameters[3, 0]
            )
        ).as2D()
        assertEquals(expectedParameters[0, 0], receivedParameters[0, 0])
        assertEquals(expectedParameters[1, 0], receivedParameters[1, 0])
        assertEquals(expectedParameters[2, 0], receivedParameters[2, 0])
        assertEquals(expectedParameters[3, 0], receivedParameters[3, 0])
    }

    @Test
    fun testLMMiddle() = DoubleTensorAlgebra {
        val nData = 100
        val tExample = one(nData, 1).as2D()
        for (i in 0 until nData) {
            tExample[i, 0] = tExample[i, 0] * (i + 1)
        }

        val nParams = 20
        val pExample = one(nParams, 1).as2D()
        for (i in 0 until nParams) {
            pExample[i, 0] = pExample[i, 0] + i - 25
        }

        val exampleNumber = 1

        val yHat = funcMiddleForLm(tExample, pExample, exampleNumber)

        val pInit = zeros(ShapeND(intArrayOf(nParams, 1))).as2D()
        for (i in 0 until nParams) {
            pInit[i, 0] = (pExample[i, 0] + 0.9)
        }

        val t = tExample
        val yDat = yHat
        val weight = 1.0
        val dp = BroadcastDoubleTensorAlgebra.fromArray(
            ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
        ).as2D()
        var pMin = ones(ShapeND(intArrayOf(nParams, 1)))
        pMin = pMin * (-50.0)
        val pMax = ones(ShapeND(intArrayOf(nParams, 1)))
        pMin = pMin * 50.0
        val opts = doubleArrayOf(3.0, 7000.0, 1e-5, 1e-5, 1e-5, 1e-5, 1e-5, 11.0, 9.0, 1.0)

        val inputData = LMInput(
            Companion::funcMiddleForLm,
            pInit.as2D(),
            t,
            yDat,
            weight,
            dp,
            pMin.as2D(),
            pMax.as2D(),
            opts[1].toInt(),
            doubleArrayOf(opts[2], opts[3], opts[4], opts[5]),
            doubleArrayOf(opts[6], opts[7], opts[8]),
            opts[9].toInt(),
            10,
            1
        )

        val result = DoubleTensorAlgebra.levenbergMarquardt(inputData)

        assertEquals(46, result.iterations)
        assertEquals(113, result.funcCalls)
        assertEquals(0.000005977, result.resultChiSq, 1e-9)
        assertEquals(1.0 * 1e-7, result.resultLambda, 1e-13)
        assertEquals(result.typeOfConvergence, TypeOfConvergence.InReducedChiSquare)
        val expectedParameters = fromArray(
            ShapeND(intArrayOf(nParams, 1)), doubleArrayOf(
                -23.9717, -18.6686, -21.7971,
                -20.9681, -22.086, -20.5859, -19.0384, -17.4957, -15.9991, -14.576, -13.2441, -
                12.0201, -10.9256, -9.9878, -9.2309, -8.6589, -8.2365, -7.8783, -7.4598, -6.8511
            )
        )
        val receivedParameters = zero(nParams, 1)
        for (i in 0 until nParams) {
            receivedParameters[i, 0] = result.resultParameters[i, 0]
            assertEquals(expectedParameters[i, 0], result.resultParameters[i, 0], 1e-2)
        }
    }

    @Test
    fun TestLMDifficult() = DoubleTensorAlgebra {
        val nData = 200
        val tExample = ones(ShapeND(intArrayOf(nData, 1))).as2D()
        for (i in 0 until nData) {
            tExample[i, 0] = tExample[i, 0] * (i + 1) - 104
        }

        val nParams = 15
        val pExample = ones(ShapeND(intArrayOf(nParams, 1))).as2D()
        for (i in 0 until nParams) {
            pExample[i, 0] = pExample[i, 0] + i - 25
        }

        val exampleNumber = 1

        val yHat = funcDifficultForLm(tExample, pExample, exampleNumber)

        val pInit = zeros(ShapeND(intArrayOf(nParams, 1))).as2D()
        for (i in 0 until nParams) {
            pInit[i, 0] = (pExample[i, 0] + 0.9)
        }

        val t = tExample
        val yDat = yHat
        val weight = 1.0 / nParams * 1.0 - 0.085
        val dp = fromArray(
            ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
        ).as2D()
        var pMin = ones(ShapeND(intArrayOf(nParams, 1)))
        pMin = pMin * (-50.0)
        val pMax = ones(ShapeND(intArrayOf(nParams, 1)))
        pMin = pMin * (50.0)
        val opts = doubleArrayOf(3.0, 7000.0, 1e-2, 1e-3, 1e-2, 1e-2, 1e-2, 11.0, 9.0, 1.0)

        val inputData = LMInput(
            Companion::funcDifficultForLm,
            pInit.as2D(),
            t,
            yDat,
            weight,
            dp,
            pMin.as2D(),
            pMax.as2D(),
            opts[1].toInt(),
            doubleArrayOf(opts[2], opts[3], opts[4], opts[5]),
            doubleArrayOf(opts[6], opts[7], opts[8]),
            opts[9].toInt(),
            10,
            1
        )

        val result = DoubleTensorAlgebra.levenbergMarquardt(inputData)

        assertEquals(2375, result.iterations)
        assertEquals(4858, result.funcCalls)
        assertEquals(5.14347, result.resultLambda, 1e-5)
        assertEquals(result.typeOfConvergence, TypeOfConvergence.InParameters)
        val expectedParameters = Float64Buffer(
            -23.6412,
            -16.7402,
            -21.5705,
            -21.0464,
            -17.2852,
            -17.2959,
            -17.298,
            0.9999,
            -17.2885,
            -17.3008,
            -17.2941,
            -17.2923,
            -17.2976,
            -17.3028,
            -17.2891
        )

        val receivedParameters = zeros(ShapeND(intArrayOf(nParams, 1))).as2D()
        for (i in 0 until nParams) {
            receivedParameters[i, 0] = result.resultParameters[i, 0]
            assertEquals(expectedParameters[i], result.resultParameters[i, 0], 1e-2)
        }
    }
}