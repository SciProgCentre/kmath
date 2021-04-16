/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kaceince.kmath.real

import space.kscience.kmath.real.step
import kotlin.test.Test
import kotlin.test.assertEquals

class GridTest {
    @Test
    fun testStepGrid(){
        val grid = 0.0..1.0 step 0.2
        assertEquals(6, grid.size)
    }
}