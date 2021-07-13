/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertTrue

internal fun NoaFloat.testingAutoGrad(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val dim = 3
    val tensorX = randNormal(shape = intArrayOf(dim), device = device)
    val randFeatures = randNormal(shape = intArrayOf(dim, dim), device = device)
    val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
    val tensorMu = randNormal(shape = intArrayOf(dim), device = device)

    val expressionAtX = withGradAt(tensorX) { x ->
        0.5f * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9f
    }

    val gradientAtX = expressionAtX.autoGradient(tensorX, retainGraph = true)
    val hessianAtX = expressionAtX.autoHessian(tensorX)
    val expectedGradientAtX = (tensorSigma dot tensorX) + tensorMu

    val error = (gradientAtX - expectedGradientAtX).abs().sum() +
            (hessianAtX - tensorSigma).abs().sum()
    assertTrue(error < TOLERANCE)
}

internal fun NoaFloat.testingBatchedAutoGrad(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val batch = intArrayOf(2)
    val dim = 2
    val tensorX = randNormal(shape = batch + intArrayOf(1, dim), device = device)
    val randFeatures = randNormal(shape = batch + intArrayOf(dim, dim), device = device)
    val tensorSigma = randFeatures + randFeatures.transpose(-2, -1)
    val tensorMu = randNormal(shape = batch + intArrayOf(1, dim), device = device)

    val expressionAtX = withGradAt(tensorX) { x ->
        val xt = x.transpose(-1, -2)
        (0.5f * (x dot (tensorSigma dot xt)) + (tensorMu dot xt) + 58.2f).sumAll()
    }


    val gradientAtX = expressionAtX.autoGradient(tensorX)
    val expectedGradientAtX = (tensorX dot tensorSigma) + tensorMu

    val error = (gradientAtX - expectedGradientAtX).abs().sum()
    assertTrue(error < TOLERANCE)
}

class TestAutoGrad {

    @Test
    fun testAutoGrad() = NoaFloat {
        withCuda { device ->
            testingAutoGrad(device)
        }
    }!!

    @Test
    fun testBatchedAutoGrad() = NoaFloat {
        withCuda { device ->
            testingBatchedAutoGrad(device)
        }
    }!!
}
