/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.integration.gaussIntegrator
import space.kscience.kmath.integration.integrate
import space.kscience.kmath.integration.value
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.structureND
import space.kscience.kmath.nd.withNdAlgebra
import space.kscience.kmath.operations.algebra
import kotlin.math.pow

fun main(): Unit = Double.algebra.withNdAlgebra(2, 2) {

    //Produce a diagonal StructureND
    fun diagonal(v: Double) = structureND { (i, j) ->
        if (i == j) v else 0.0
    }

    //Define a function in a nd space
    val function: (Double) -> StructureND<Double> = { x: Double -> 3 * x.pow(2) + 2 * diagonal(x) + 1 }

    //get the result of the integration
    val result = gaussIntegrator.integrate(0.0..10.0, function = function)

    //the value is nullable because in some cases the integration could not succeed
    println(result.value)
}
