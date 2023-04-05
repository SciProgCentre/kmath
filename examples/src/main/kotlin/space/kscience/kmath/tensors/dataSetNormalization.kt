/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.tensors.core.randomNormal
import space.kscience.kmath.tensors.core.tensorAlgebra
import space.kscience.kmath.tensors.core.withBroadcast


// Dataset normalization

fun main() = Double.tensorAlgebra.withBroadcast {  // work in context with broadcast methods
    // take dataset of 5-element vectors from normal distribution
    val dataset = randomNormal(ShapeND(100, 5)) * 1.5 // all elements from N(0, 1.5)

    dataset += fromArray(
        ShapeND(5),
        doubleArrayOf(0.0, 1.0, 1.5, 3.0, 5.0) // row means
    )


    // find out mean and standard deviation of each column
    val mean = mean(dataset, 0, false)
    val std = std(dataset, 0, false)

    println("Mean:\n$mean")
    println("Standard deviation:\n$std")

    // also, we can calculate other statistic as minimum and maximum of rows
    println("Minimum:\n${dataset.min(0, false)}")
    println("Maximum:\n${dataset.max(0, false)}")

    // now we can scale dataset with mean normalization
    val datasetScaled = (dataset - mean) / std

    // find out mean and standardDiviation of scaled dataset

    println("Mean of scaled:\n${mean(datasetScaled, 0, false)}")
    println("Mean of scaled:\n${std(datasetScaled, 0, false)}")
}