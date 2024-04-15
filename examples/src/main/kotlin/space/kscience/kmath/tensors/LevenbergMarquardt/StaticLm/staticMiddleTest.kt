/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StaticLm

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.component1
import space.kscience.kmath.tensors.LevenbergMarquardt.funcMiddleForLm
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.div
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.LMInput
import space.kscience.kmath.tensors.core.levenbergMarquardt
import kotlin.math.roundToInt

fun main() {
    val NData = 100
    var t_example = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(NData, 1))).as2D()
    for (i in 0 until NData) {
        t_example[i, 0] = t_example[i, 0] * (i + 1)
    }

    val Nparams = 20
    var p_example = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1))).as2D()
    for (i in 0 until Nparams) {
        p_example[i, 0] = p_example[i, 0] + i - 25
    }

    val exampleNumber = 1

    var y_hat = funcMiddleForLm(t_example, p_example, exampleNumber)

    var p_init = DoubleTensorAlgebra.zeros(ShapeND(intArrayOf(Nparams, 1))).as2D()
    for (i in 0 until Nparams) {
        p_init[i, 0] = (p_example[i, 0] + 0.9)
    }

    var t = t_example
    val y_dat = y_hat
    val weight = 1.0
    val dp = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
    ).as2D()
    var p_min = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1)))
    p_min = p_min.div(1.0 / -50.0)
    val p_max = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1)))
    p_min = p_min.div(1.0 / 50.0)
    val opts = doubleArrayOf(3.0, 7000.0, 1e-5, 1e-5, 1e-5, 1e-5, 1e-5, 11.0, 9.0, 1.0)

    val inputData = LMInput(
        ::funcMiddleForLm,
        p_init.as2D(),
        t,
        y_dat,
        weight,
        dp,
        p_min.as2D(),
        p_max.as2D(),
        opts[1].toInt(),
        doubleArrayOf(opts[2], opts[3], opts[4], opts[5]),
        doubleArrayOf(opts[6], opts[7], opts[8]),
        opts[9].toInt(),
        10,
        1
    )

    val result = DoubleTensorAlgebra.levenbergMarquardt(inputData)

    println("Parameters:")
    for (i in 0 until result.resultParameters.shape.component1()) {
        val x = (result.resultParameters[i, 0] * 10000).roundToInt() / 10000.0
        print("$x ")
    }
    println()


    var y_hat_after = funcMiddleForLm(t_example, result.resultParameters, exampleNumber)
    for (i in 0 until y_hat.shape.component1()) {
        val x = (y_hat[i, 0] * 10000).roundToInt() / 10000.0
        val y = (y_hat_after[i, 0] * 10000).roundToInt() / 10000.0
        println("$x $y")
    }

    println("Сhi_sq:")
    println(result.resultChiSq)
    println("Number of iterations:")
    println(result.iterations)
}