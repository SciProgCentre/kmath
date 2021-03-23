/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

/**
 * Performance optimized chain for integer values
 */
public abstract class BlockingIntChain : Chain<Int> {
    public abstract fun nextInt(): Int

    override suspend fun next(): Int = nextInt()

    public fun nextBlock(size: Int): IntArray = IntArray(size) { nextInt() }
}
