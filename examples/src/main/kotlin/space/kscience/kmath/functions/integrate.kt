package space.kscience.kmath.functions

import space.kscience.kmath.integration.GaussIntegrator
import space.kscience.kmath.integration.value
import kotlin.math.pow

fun main() {
    //Define a function
    val function: UnivariateFunction<Double> = { x -> 3 * x.pow(2) + 2 * x + 1 }

    //get the result of the integration
    val result = GaussIntegrator.legendre(0.0..10.0, function = function)

    //the value is nullable because in some cases the integration could not succeed
    println(result.value)
}