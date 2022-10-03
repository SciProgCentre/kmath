/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

public open class SpaceVerifier<T, out S>(
    override val algebra: S,
    public val a: T,
    public val b: T,
    public val c: T,
    public val x: Number,
) : AlgebraicVerifier<T, Ring<T>> where S : Ring<T>, S : ScaleOperations<T> {
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
