/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertEquals

class AngleTest {
    @Test
    fun normalization() {
        assertEquals(30.degrees, 390.degrees.normalized())
        assertEquals(30.degrees, (-330).degrees.normalized())
        assertEquals(200.degrees, 200.degrees.normalized())
        assertEquals(30.degrees, 390.degrees.normalized(Angle.zero))
        assertEquals(30.degrees, (-330).degrees.normalized(Angle.zero))
        assertEquals((-160).degrees, 200.degrees.normalized(Angle.zero))
    }
}