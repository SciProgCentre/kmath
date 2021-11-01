/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.tensors.core.tensorAlgebra
import space.kscience.kmath.tensors.core.withBroadcast


// simple PCA

fun main(): Unit = Double.tensorAlgebra.withBroadcast {  // work in context with broadcast methods
    val seed = 100500L

    // assume x is range from 0 until 10
    val x = fromArray(
        intArrayOf(10),
        DoubleArray(10) { it.toDouble() }
    )

    // take y dependent on x with noise
    val y = 2.0 * x + (3.0 + x.randomNormalLike(seed) * 1.5)

    println("x:\n$x")
    println("y:\n$y")

    // stack them into single dataset
    val dataset = stack(listOf(x, y)).transpose()

    // normalize both x and y
    val xMean = x.mean()
    val yMean = y.mean()

    val xStd = x.std()
    val yStd = y.std()

    val xScaled = (x - xMean) / xStd
    val yScaled = (y - yMean) / yStd

    // save means ans standard deviations for further recovery
    val mean = fromArray(
        intArrayOf(2),
        doubleArrayOf(xMean, yMean)
    )
    println("Means:\n$mean")

    val std = fromArray(
        intArrayOf(2),
        doubleArrayOf(xStd, yStd)
    )
    println("Standard deviations:\n$std")

    // calculate the covariance matrix of scaled x and y
    val covMatrix = cov(listOf(xScaled, yScaled))
    println("Covariance matrix:\n$covMatrix")

    // and find out eigenvector of it
    val (_, evecs) = covMatrix.symEig()
    val v = evecs[0]
    println("Eigenvector:\n$v")

    // reduce dimension of dataset
    val datasetReduced = v dot stack(listOf(xScaled, yScaled))
    println("Reduced data:\n$datasetReduced")

    // we can restore original data from reduced data;
    // for example, find 7th element of dataset.
    val n = 7
    val restored = (datasetReduced[n] dot v.view(intArrayOf(1, 2))) * std + mean
    println("Original value:\n${dataset[n]}")
    println("Restored value:\n$restored")
}
