/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.buffer
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.operations.withSize

inline fun <reified R : Any> MutableBuffer.Companion.same(
    n: Int,
    value: R,
): MutableBuffer<R> = MutableBuffer(n) { value }


fun main() {
    with(Float64Field.bufferAlgebra.withSize(5)) {
        println(number(2.0) + buffer(1, 2, 3, 4, 5))
    }
}
