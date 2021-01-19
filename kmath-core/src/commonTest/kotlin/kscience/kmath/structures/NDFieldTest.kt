package kscience.kmath.structures

import kscience.kmath.operations.internal.FieldVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NDFieldTest {
    @Test
    fun verify() {
        NDField.real(12, 32).run { FieldVerifier(this, one + 3, one - 23, one * 12, 6.66) }
    }

    @Test
    fun testStrides() {
        val ndArray = NDElement.real(intArrayOf(10, 10)) { (it[0] + it[1]).toDouble() }
        assertEquals(ndArray[5, 5], 10.0)
    }
}
