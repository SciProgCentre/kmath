/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.JBigDecimalField
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class JBigTest {

    @Test
    fun testExact() = with(JBigDecimalField) {
        assertNotEquals(0.3, 0.1 + 0.2)
        assertEquals(one * 0.3, one * 0.1 + one * 0.2)
    }
}