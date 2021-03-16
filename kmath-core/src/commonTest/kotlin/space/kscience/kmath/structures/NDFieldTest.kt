/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.get
import space.kscience.kmath.nd.double
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.testutils.FieldVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NDFieldTest {
    @Test
    fun verify() {
        (AlgebraND.double(12, 32)) { FieldVerifier(this, one + 3, one - 23, one * 12, 6.66) }
    }

    @Test
    fun testStrides() {
        val ndArray = AlgebraND.double(10, 10).produce { (it[0] + it[1]).toDouble() }
        assertEquals(ndArray[5, 5], 10.0)
    }
}
