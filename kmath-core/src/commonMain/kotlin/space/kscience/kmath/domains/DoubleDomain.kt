/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.UnstableKMathAPI

/**
 * n-dimensional volume
 *
 * @author Alexander Nozik
 */
@UnstableKMathAPI
public interface DoubleDomain : Domain<Double> {
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
