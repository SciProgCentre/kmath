/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI

public open class VirtualStructureND<T>(
    override val shape: Shape,
    public val producer: (IntArray) -> T,
) : StructureND<T> {
    override fun get(index: IntArray): T {
        requireIndexInShape(index, shape)
        return producer(index)
    }
}

@UnstableKMathAPI
public class VirtualDoubleStructureND(
    shape: Shape,
    producer: (IntArray) -> Double,
) : VirtualStructureND<Double>(shape, producer)

@UnstableKMathAPI
public class VirtualIntStructureND(
    shape: Shape,
    producer: (IntArray) -> Int,
) : VirtualStructureND<Int>(shape, producer)