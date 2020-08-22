package scientifik.kmath.structures

import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.NDElement.Companion.real2D
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberNDFieldTest {
    val array1: RealNDElement = real2D(3, 3) { i, j -> (i + j).toDouble() }
    val array2: RealNDElement = real2D(3, 3) { i, j -> (i - j).toDouble() }

    @Test
    fun testSum() {
        val sum = array1 + array2
        assertEquals(4.0, sum[2, 2])
    }

    @Test
    fun testProduct() {
        val product = array1 * array2
        assertEquals(0.0, product[2, 2])
    }

    @Test
    fun testGeneration() {

        val array = real2D(3, 3) { i, j -> (i * 10 + j).toDouble() }

        for (i in 0..2) {
            for (j in 0..2) {
                val expected = (i * 10 + j).toDouble()
                assertEquals(expected, array[i, j], "Error at index [$i, $j]")
            }
        }
    }

    @Test
    fun testExternalFunction() {
        val function: (Double) -> Double = { x -> x.pow(2) + 2 * x + 1 }
        val result = function(array1) + 1.0
        assertEquals(10.0, result[1, 1])
    }

    @Test
    fun testLibraryFunction() {
        val abs: (Double) -> Double = ::abs
        val result = abs(array2)
        assertEquals(2.0, result[0, 2])
    }

    @Test
    fun combineTest() {
        val division = array1.combine(array2, Double::div)
    }

    object L2Norm : Norm<NDStructure<out Number>, Double> {
        override fun norm(arg: NDStructure<out Number>): Double =
            kotlin.math.sqrt(arg.elements().sumByDouble { it.second.toDouble() })
    }

    @Test
    fun testInternalContext() {
        (NDField.real(*array1.shape)) { with(L2Norm) { 1 + norm(array1) + exp(array2) } }
    }
}
