package kscience.kmath.testutils

import kscience.kmath.operations.Space
import kscience.kmath.operations.invoke
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal open class SpaceVerifier<T>(
    override val algebra: Space<T>,
    val a: T,
    val b: T,
    val c: T,
    val x: Number
) :
    AlgebraicVerifier<T, Space<T>> {
    override fun verify() {
        algebra {
            assertEquals(a + b + c, a + (b + c), "Addition in $algebra is not associative.")
            assertEquals(x * (a + b), x * a + x * b, "Addition in $algebra is not distributive.")
            assertEquals((a + b) * x, a * x + b * x, "Addition in $algebra is not distributive.")
            assertEquals(a + zero, zero + a, "$zero in $algebra is not a neutral addition element.")
            assertEquals(a, a + zero, "$zero in $algebra is not a neutral addition element.")
            assertEquals(a, zero + a, "$zero in $algebra is not a neutral addition element.")
            assertEquals(a - b, -(b - a), "Subtraction in $algebra is not anti-commutative.")
            assertNotEquals(a - b - c, a - (b - c), "Subtraction in $algebra is associative.")
            assertEquals(x * (a - b), x * a - x * b, "Subtraction in $algebra is not distributive.")
            assertEquals(a, a - zero, "$zero in $algebra is not a neutral addition element.")
            assertEquals(a * x, x * a, "Multiplication by scalar in $algebra is not commutative.")
            assertEquals(x * (a + b), (x * a) + (x * b), "Multiplication by scalar in $algebra is not distributive.")
        }
    }
}
