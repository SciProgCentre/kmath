/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public abstract class Domain1D<T : Comparable<T>>(public val range: ClosedRange<T>) : Domain<T> {
    override val dimension: Int get() = 1

    public operator fun contains(value: T): Boolean = range.contains(value)

    override operator fun contains(point: Point<T>): Boolean {
        require(point.size == 0)
        return contains(point[0])
    }
}

@UnstableKMathAPI
public class DoubleDomain1D(
    @Suppress("CanBeParameter") public val doubleRange: ClosedFloatingPointRange<Double>,
) : Domain1D<Double>(doubleRange), DoubleDomain {
    override fun getLowerBound(num: Int): Double {
        require(num == 0)
        return range.start
    }

    override fun getUpperBound(num: Int): Double {
        require(num == 0)
        return range.endInclusive
    }

    override fun volume(): Double = range.endInclusive - range.start

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DoubleDomain1D

        if (doubleRange != other.doubleRange) return false

        return true
    }

    override fun hashCode(): Int = doubleRange.hashCode()

    override fun toString(): String = doubleRange.toString()


}

@UnstableKMathAPI
public val Domain1D<Double>.center: Double
    get() = (range.endInclusive + range.start) / 2
