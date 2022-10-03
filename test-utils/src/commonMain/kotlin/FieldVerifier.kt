/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.invoke
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

public class FieldVerifier<T, out A : Field<T>>(
    algebra: A, a: T, b: T, c: T, x: Number,
) : RingVerifier<T, A>(algebra, a, b, c, x) {

    override fun verify() {
        super.verify()

        algebra {
            assertEquals(a + b, b + a, "Addition in $algebra is not commutative.")
            assertEquals(a * b, b * a, "Multiplication in $algebra is not commutative.")
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
