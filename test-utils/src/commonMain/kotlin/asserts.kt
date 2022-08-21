/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices
import kotlin.test.assertEquals
import kotlin.test.fail

public fun assertBufferEquals(expected: Buffer<Double>, result: Buffer<Double>, tolerance: Double = 1e-4) {
    if (expected.size != result.size) {
        fail("Expected size is ${expected.size}, but the result size is ${result.size}")
    }
    expected.indices.forEach {
        assertEquals(expected[it], result[it], tolerance)
    }
}