/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.integration.gaussIntegrator
import space.kscience.kmath.integration.integrate
import space.kscience.kmath.integration.value
import space.kscience.kmath.operations.DoubleField
import kotlin.math.pow

fun main() {
    //Define a function
    val function: Function1D<Double> = { x -> 3 * x.pow(2) + 2 * x + 1 }

    //get the result of the integration
    val result = DoubleField.gaussIntegrator.integrate(0.0..10.0, function = function)

    //the value is nullable because in some cases the integration could not succeed
    println(result.value)
}