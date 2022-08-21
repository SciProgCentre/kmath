/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.operations.invoke
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ComplexTest {
    @Test
    fun conjugate() = ComplexField { assertEquals(Complex(0, 42), Complex(0, -42).conjugate) }

    @Test
    fun reciprocal() = ComplexField { assertTrue((Complex(0.5, -0.0) - 2.toComplex().reciprocal).r < 1e-10) }

    @Test
    fun r() = ComplexField { assertEquals(sqrt(2.0), (i + 1.0.toComplex()).r) }

    @Test
    fun theta() = assertEquals(0.0, 1.toComplex().theta)

    @Test
    fun toComplex() {
        assertEquals(Complex(42), 42.toComplex())
        assertEquals(Complex(42.0), 42.0.toComplex())
        assertEquals(Complex(42f), 42f.toComplex())
        assertEquals(Complex(42.0), 42.0.toComplex())
        assertEquals(Complex(42.toByte()), 42.toByte().toComplex())
        assertEquals(Complex(42.toShort()), 42.toShort().toComplex())
    }
}
