/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.slice
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSeries {

    @Test
    fun zip() = with(Double.algebra.bufferAlgebra.seriesAlgebra()){
        val s1 = series(100) { sin(2 * PI * it / 100) + 1.0 }

        val s2 = s1.slice(20..50).moveTo(40)

        val s3: Buffer<Double> = s1.zip(s2) { l, r -> l + r } //s1 + s2

        assertEquals(s3.getByOffset(40),s1.getByOffset(40) + s1.getByOffset(20))
    }
}