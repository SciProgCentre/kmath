package space.kscience.kmath.operations

import space.kscience.kmath.testutils.FieldVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DoubleFieldTest {
    @Test
    fun verify() = FieldVerifier(DoubleField, 42.0, 66.0, 2.0, 5).verify()

    @Test
    fun testSqrt() {
        val sqrt = DoubleField { sqrt(25 * one) }
        assertEquals(5.0, sqrt)
    }
}
