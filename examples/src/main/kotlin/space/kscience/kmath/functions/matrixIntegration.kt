package space.kscience.kmath.functions

import space.kscience.kmath.integration.GaussIntegrator
import space.kscience.kmath.integration.UnivariateIntegrand
import space.kscience.kmath.integration.value
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.nd
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

fun main(): Unit = DoubleField {
    nd(2, 2) {
        //Define a function in a nd space
        val function: UnivariateFunction<StructureND<Double>> = { x -> 3 * x.pow(2) + 2 * x + 1 }

        //get the result of the integration
        val result: UnivariateIntegrand<StructureND<Double>> = GaussIntegrator.legendre(this, 0.0..10.0, function = function)

        //the value is nullable because in some cases the integration could not succeed
        println(result.value)
    }
}