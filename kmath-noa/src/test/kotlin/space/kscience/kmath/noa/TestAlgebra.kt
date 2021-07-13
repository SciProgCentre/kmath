/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertTrue


internal fun NoaDouble.testingLinearStructure(device: Device = Device.CPU): Unit {

    val shape = intArrayOf(3)
    val tensorA = full(value = -4.5, shape = shape, device = device)
    val tensorB = full(value = 10.9, shape = shape, device = device)
    val tensorC = full(value = 789.3, shape = shape, device = device)
    val tensorD = full(value = -72.9, shape = shape, device = device)
    val tensorE = full(value = 553.1, shape = shape, device = device)
    val result = 15.8 * tensorA - 1.5 * tensorB * (-tensorD) + 0.02 * tensorC / tensorE - 39.4
    val expected = copyFromArray(
        array = (1..3).map {
            15.8 * (-4.5) - 1.5 * 10.9 * 72.9 + 0.02 * 789.3 / 553.1 - 39.4
        }.toDoubleArray(),
        shape = shape,
        device = device
    )

    val assignResult = full(value = 0.0, shape = shape, device = device)
    tensorA *= 15.8
    tensorB *= 1.5
    tensorB *= -tensorD
    tensorC *= 0.02
    tensorC /= tensorE
    assignResult += tensorA
    assignResult -= tensorB
    assignResult += tensorC
    assignResult += -39.4

    val error = (expected - result).abs().sum() +
            (expected - assignResult).abs().sum()
    assertTrue(error < TOLERANCE)

}

internal fun NoaDouble.testingBatchedSVD(device: Device = Device.CPU): Unit {
    val tensor = randNormal(shape = intArrayOf(7, 5, 3), device = device)
    val (tensorU, tensorS, tensorV) = tensor.svd()
    val error = tensor - (tensorU dot (diagonalEmbedding(tensorS) dot tensorV.transpose(-2, -1)))
    assertTrue(error.abs().sum() < TOLERANCE)
}

internal fun NoaDouble.testingBatchedSymEig(device: Device = Device.CPU): Unit {
    val tensor = randNormal(shape = intArrayOf(5, 5), device = device)
    val tensorSigma = tensor + tensor.transpose(-2, -1)
    val (tensorS, tensorV) = tensorSigma.symEig()
    val error = tensorSigma - (tensorV dot (diagonalEmbedding(tensorS) dot tensorV.transpose(-2, -1)))
    assertTrue(error.abs().sum() < TOLERANCE)
}


class TestAlgebra {

    @Test
    fun testLinearStructure() = NoaDouble {
        withCuda { device ->
            testingLinearStructure(device)
        }
    }!!

    @Test
    fun testBatchedSVD() = NoaDouble {
        withCuda { device ->
            testingBatchedSVD(device)
        }
    }!!

    @Test
    fun testBatchedSymEig() = NoaDouble {
        withCuda { device ->
            testingBatchedSymEig(device)
        }
    }!!
}
