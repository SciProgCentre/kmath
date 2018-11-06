package scientifik.kmath.structures

import scientifik.kmath.structures.NDArrays.produceReal
import scientifik.kmath.structures.NDArrays.real2DArray
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class RealNDFieldTest {
    val array1 = real2DArray(3, 3) { i, j -> (i + j).toDouble() }
    val array2 = real2DArray(3, 3) { i, j -> (i - j).toDouble() }

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

        val array = real2DArray(3, 3) { i, j -> (i * 10 + j).toDouble() }

        for (i in 0..2) {
            for (j in 0..2) {
                val expected = (i * 10 + j).toDouble()
                assertEquals(expected, array[i, j],"Error at index [$i, $j]")
            }
        }
    }

    @Test
    fun testExternalFunction() {
        val function: (Double) -> Double = { x -> x.pow(2) + 2 * x + 1 }
        val result = function(array1) + 1.0
        assertEquals(10.0, result[1,1])
    }

    @Test
    fun testLibraryFunction() {
        val abs: (Double) -> Double = ::abs
        val result = abs(array1)
        assertEquals(10.0, result[1,1])
    }

    @Test
    fun testAbs(){
        val res = produceReal(array1.shape){
           1 + abs(array1) + exp(array2)
        }
    }
}
