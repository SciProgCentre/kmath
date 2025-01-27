/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Int32

public open class VirtualStructureND<T>(
    override val shape: ShapeND,
    public val producer: (IntArray) -> T,
) : StructureND<T> {

    @PerformancePitfall
    override fun get(index: IntArray): T {
        requireIndexInShape(index, shape)
        return producer(index)
    }
}

@UnstableKMathAPI
public class VirtualFloat64StructureND(
    shape: ShapeND,
    producer: (IntArray) -> Float64,
) : VirtualStructureND<Float64>(shape, producer)

@UnstableKMathAPI
public class VirtualInt32StructureND(
    shape: ShapeND,
    producer: (IntArray) -> Int32,
) : VirtualStructureND<Int>(shape, producer)