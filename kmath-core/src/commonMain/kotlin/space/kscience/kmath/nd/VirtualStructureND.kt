/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

public open class VirtualStructureND<T>(
    override val shape: Shape,
    public val producer: (IntArray) -> T,
) : StructureND<T> {
    override fun get(index: IntArray): T {
        require(check that index is in the shape boundaries)
        return producer(index)
    }
}