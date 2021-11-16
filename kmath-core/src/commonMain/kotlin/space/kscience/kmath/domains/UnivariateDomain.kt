/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public class UnivariateDomain(public val range: ClosedFloatingPointRange<Double>) : DoubleDomain {
    override val dimension: Int get() = 1

    public operator fun contains(d: Double): Boolean = range.contains(d)

    override operator fun contains(point: Point<Double>): Boolean {
        require(point.size == 0)
        return contains(point[0])
    }

    override fun getLowerBound(num: Int): Double {
        require(num == 0)
        return range.start
    }

    override fun getUpperBound(num: Int): Double {
        require(num == 0)
        return range.endInclusive
    }

    override fun volume(): Double = range.endInclusive - range.start
}
