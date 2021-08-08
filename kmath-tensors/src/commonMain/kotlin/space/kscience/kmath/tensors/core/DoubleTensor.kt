/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.tensors.core.internal.toPrettyString

/**
 * Default [BufferedTensor] implementation for [Double] values
 */
public class DoubleTensor @PublishedApi internal constructor(
    shape: IntArray,
    buffer: DoubleArray,
    offset: Int = 0
) : BufferedTensor<Double>(shape, DoubleBuffer(buffer), offset) {
    override fun toString(): String = toPrettyString()
}
