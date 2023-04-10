/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.UnstableKMathAPI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@UnstableKMathAPI
class GridTest {
    @Test
    fun testStepGrid() {
        val grid = 0.0..1.0 step 0.2
        assertEquals(6, grid.size)
        assertTrue { (grid - DoubleVector(0.0, 0.2, 0.4, 0.6, 0.8, 1.0)).norm < 1e-4 }
    }

    @Test
    fun testIterateGrid(){
        var res = 0.0
        for(d in 0.0..1.0 step 0.2){
            res = d
        }
        assertEquals(1.0, res)
    }
}