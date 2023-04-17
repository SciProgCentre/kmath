/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall


public class PermutedStructureND<T>(
    public val origin: StructureND<T>,
    public val permutation: (IntArray) -> IntArray,
) : StructureND<T> {

    override val shape: ShapeND
        get() = origin.shape

    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): T {
        return origin[permutation(index)]
    }
}

public fun <T> StructureND<T>.permute(
    permutation: (IntArray) -> IntArray,
): PermutedStructureND<T> = PermutedStructureND(this, permutation)

public class PermutedMutableStructureND<T>(
    public val origin: MutableStructureND<T>,
    override val shape: ShapeND = origin.shape,
    public val permutation: (IntArray) -> IntArray,
) : MutableStructureND<T> {


    @OptIn(PerformancePitfall::class)
    override fun set(index: IntArray, value: T) {
        origin[permutation(index)] = value
    }

    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): T {
        return origin[permutation(index)]
    }
}

public fun <T> MutableStructureND<T>.permute(
    newShape: ShapeND = shape,
    permutation: (IntArray) -> IntArray,
): PermutedMutableStructureND<T> = PermutedMutableStructureND(this, newShape, permutation)