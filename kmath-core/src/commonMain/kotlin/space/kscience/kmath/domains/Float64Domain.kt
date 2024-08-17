/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.structures.Float64

/**
 * n-dimensional volume
 *
 * @author Alexander Nozik
 */
@UnstableKMathAPI
public interface Float64Domain : Domain<Float64> {
    /**
     * Global lower edge
     * @param num axis number
     */
    public fun getLowerBound(num: Int): Double

    /**
     * Global upper edge
     * @param num axis number
     */
    public fun getUpperBound(num: Int): Double

    /**
     * Hyper volume
     * @return
     */
    public fun volume(): Double
}
