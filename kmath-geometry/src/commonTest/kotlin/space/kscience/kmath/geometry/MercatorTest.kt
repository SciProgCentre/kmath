/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertTrue

class MercatorTest {

    @Test
    fun mercatorOffset(){
        val mskCoordinates = GeodeticCoordinates.ofDegrees(55.7558, 37.6173)
        val spbCoordinates = GeodeticCoordinates.ofDegrees(59.9311, 30.3609)

        val offset =  WebMercatorAlgebra.computeOffset(mskCoordinates, spbCoordinates)

        assertTrue { offset.x in -127.0..128.0 }
        assertTrue { offset.y in -127.0..128.0 }
        assertTrue { offset.zoom > 1.0 }
    }
}