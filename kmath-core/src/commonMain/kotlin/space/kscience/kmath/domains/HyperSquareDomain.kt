/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 *
 * HyperSquareDomain class.
 *
 * @author Alexander Nozik
 */
@UnstableKMathAPI
public class HyperSquareDomain(private val lower: Buffer<Double>, private val upper: Buffer<Double>) : DoubleDomain {
    public override val dimension: Int get() = lower.size

    public override operator fun contains(point: Point<Double>): Boolean = point.indices.all { i ->
        point[i] in lower[i]..upper[i]
    }

    public override fun getLowerBound(num: Int): Double = lower[num]

    public override fun getUpperBound(num: Int): Double = upper[num]

    public override fun volume(): Double {
        var res = 1.0

        for (i in 0 until dimension) {
            if (lower[i].isInfinite() || upper[i].isInfinite()) return Double.POSITIVE_INFINITY
            if (upper[i] > lower[i]) res *= upper[i] - lower[i]
        }

        return res
    }
}
