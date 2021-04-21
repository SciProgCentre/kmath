/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.integration.integrate
import space.kscience.kmath.integration.value
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.nd
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

fun main(): Unit = DoubleField {
    nd(2, 2) {

        //Produce a diagonal StructureND
        fun diagonal(v: Double) = produce { (i, j) ->
            if (i == j) v else 0.0
        }

        //Define a function in a nd space
        val function: (Double) -> StructureND<Double> = { x: Double -> 3 * number(x).pow(2) + 2 * diagonal(x) + 1 }

        //get the result of the integration
        val result = integrate(0.0..10.0, function = function)

        //the value is nullable because in some cases the integration could not succeed
        println(result.value)
    }
}