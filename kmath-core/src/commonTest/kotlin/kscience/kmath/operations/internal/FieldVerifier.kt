package kscience.kmath.operations.internal

import kscience.kmath.operations.Field
import kscience.kmath.operations.invoke
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class FieldVerifier<T>(override val algebra: Field<T>, a: T, b: T, c: T, x: Number) :
    RingVerifier<T>(algebra, a, b, c, x) {

    override fun verify() {
        super.verify()

        algebra {
            assertNotEquals(a / b, b / a, "Division in $algebra is not anti-commutative.")
            assertNotEquals((a / b) / c, a / (b / c), "Division in $algebra is associative.")
            assertEquals((a + b) / c, (a / c) + (b / c), "Division in $algebra is not right-distributive.")
            assertEquals(a, a / one, "$one in $algebra is not neutral division element.")
            assertEquals(one, one / a * a, "$algebra does not provide single reciprocal element.")
            assertEquals(zero / a, zero, "$zero in $algebra is not left neutral element for division.")
            assertEquals(-one, a / (-a), "Division by sign reversal element in $algebra does not give ${-one}.")
        }
    }
}
