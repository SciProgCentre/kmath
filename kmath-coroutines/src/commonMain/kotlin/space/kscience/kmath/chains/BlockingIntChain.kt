/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

import space.kscience.kmath.structures.Int32Buffer

/**
 * Performance optimized chain for integer values
 */
public interface BlockingIntChain : BlockingBufferChain<Int> {
    override fun nextBufferBlocking(size: Int): Int32Buffer

    override suspend fun fork(): BlockingIntChain
}