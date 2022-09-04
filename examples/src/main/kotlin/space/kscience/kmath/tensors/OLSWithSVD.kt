/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra

import kotlin.math.abs

// OLS estimator using SVD

@OptIn(PerformancePitfall::class)
fun main() {
    //seed for random
    val randSeed = 100500L

    // work in context with linear operations
    DoubleTensorAlgebra {
        // take coefficient vector from normal distribution
        val alpha = randomNormal(
            intArrayOf(5),
            randSeed
        ) + fromArray(
            intArrayOf(5),
            doubleArrayOf(1.0, 2.5, 3.4, 5.0, 10.1)
        )

        println("Real alpha:\n$alpha")

        // also take sample of size 20 from normal distribution for x
        val x = randomNormal(
            intArrayOf(20, 5),
            randSeed
        )

        // calculate y and add gaussian noise (N(0, 0.05))
        val y = x dot alpha
        y += y.randomNormalLike(randSeed) * 0.05

        // now restore the coefficient vector with OSL estimator with SVD
        val (u, singValues, v) = x.svd()

        // we have to make sure the singular values of the matrix are not close to zero
        println("Singular values:\n$singValues")


        // inverse Sigma matrix can be restored from singular values with diagonalEmbedding function
        val sigma = diagonalEmbedding(singValues.map{ if (abs(it) < 1e-3) 0.0 else 1.0/it })

        val alphaOLS = v dot sigma dot u.transpose() dot y
        println("Estimated alpha:\n" +
                "$alphaOLS")

        // figure out MSE of approximation
        fun mse(yTrue: DoubleTensor, yPred: DoubleTensor): Double {
            require(yTrue.shape.size == 1)
            require(yTrue.shape contentEquals yPred.shape)

            val diff = yTrue - yPred
            return diff.dot(diff).sqrt().value()
        }

        println("MSE: ${mse(alpha, alphaOLS)}")
    }
}