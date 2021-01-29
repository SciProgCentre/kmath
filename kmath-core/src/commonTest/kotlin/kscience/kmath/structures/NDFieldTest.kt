package kscience.kmath.structures

import kscience.kmath.nd.NDAlgebra
import kscience.kmath.nd.get
import kscience.kmath.nd.real
import kscience.kmath.testutils.FieldVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kscience.kmath.operations.invoke

internal class NDFieldTest {
    @Test
    fun verify() {
        (NDAlgebra.real(12, 32)) { FieldVerifier(this, one + 3, one - 23, one * 12, 6.66) }
    }

    @Test
    fun testStrides() {
        val ndArray = NDAlgebra.real(10, 10).produce { (it[0] + it[1]).toDouble() }
        assertEquals(ndArray[5, 5], 10.0)
    }
}
