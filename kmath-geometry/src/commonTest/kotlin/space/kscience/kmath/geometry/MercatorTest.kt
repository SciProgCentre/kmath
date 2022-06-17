/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MercatorTest {

    @Test
    fun boundaries() {
        assertEquals(GeodeticCoordinates.ofDegrees(45.0, 20.0), GeodeticCoordinates.ofDegrees(45.0, 200.0))
    }

    @Test
    fun mercatorConversion() = with(MercatorAlgebra) {
        val mskCoordinates = GeodeticCoordinates.ofDegrees(55.7558, 37.6173)
        val m = mskCoordinates.toMercator()
        val r = m.toGeodetic()

        assertEquals(mskCoordinates.longitude, r.longitude, 1e-4)
        assertEquals(mskCoordinates.latitude, r.latitude, 1e-4)
    }

    @Test
    fun webMercatorConversion() = with(WebMercatorAlgebra) {
        val mskCoordinates = GeodeticCoordinates.ofDegrees(55.7558, 37.6173)
        val m = mskCoordinates.toMercator(2.0)
        val r = m.toGeodetic()

        assertEquals(mskCoordinates.longitude, r.longitude, 1e-4)
        assertEquals(mskCoordinates.latitude, r.latitude, 1e-4)
    }

    @Test
    fun webMercatorOffset() {
        val mskCoordinates = GeodeticCoordinates.ofDegrees(55.7558, 37.6173)
        val spbCoordinates = GeodeticCoordinates.ofDegrees(59.9311, 30.3609)

        val offset = WebMercatorAlgebra.computeOffset(mskCoordinates, spbCoordinates)

        assertTrue { offset.x in -127.0..128.0 }
        assertTrue { offset.y in -127.0..128.0 }
        assertTrue { offset.zoom > 0.0 }
    }

    @Test
    fun webMercatorAbsolute() {
        val mskCoordinates = GeodeticCoordinates.ofDegrees(55.7558, 37.6173)
        val wmc = with(WebMercatorAlgebra) { mskCoordinates.toMercator(13.0) }
        assertEquals(wmc.x, 1267712.0, 50.0)
        assertEquals(wmc.y, 655616.0, 50.0)
    }
}