package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals


class SimpleNDFieldTest{
    @Test
    fun testStrides(){
        val ndArray = simpleNDArray(DoubleField, listOf(10,10)){(it[0]+it[1]).toDouble()}
        assertEquals(ndArray[5,5], 10.0)
    }

}