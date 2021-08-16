/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.IntBuffer

/**
 * Default [BufferedTensor] implementation for [Int] values
 */
public class IntTensor internal constructor(
    shape: IntArray,
    buffer: IntArray,
    offset: Int = 0
) : BufferedTensor<Int>(shape, IntBuffer(buffer), offset)
