/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.indices

/**
 *
 * HyperSquareDomain class.
 *
 * @author Alexander Nozik
 */
@UnstableKMathAPI
public class HyperSquareDomain(public val lower: Buffer<Double>, public val upper: Buffer<Double>) : DoubleDomain {
    init {
        require(lower.size == upper.size) {
            "Domain borders size mismatch. Lower borders size is ${lower.size}, but upper borders size is ${upper.size}"
        }
    }

    override val dimension: Int get() = lower.size

    public val center: DoubleBuffer get() = DoubleBuffer(dimension) { (lower[it] + upper[it]) / 2.0 }

    override operator fun contains(point: Point<Double>): Boolean = point.indices.all { i ->
        point[i] in lower[i]..upper[i]
    }

    override fun getLowerBound(num: Int): Double = lower[num]

    override fun getUpperBound(num: Int): Double = upper[num]

    override fun volume(): Double {
        var res = 1.0

        for (i in 0 until dimension) {
            if (lower[i].isInfinite() || upper[i].isInfinite()) return Double.POSITIVE_INFINITY
            if (upper[i] > lower[i]) res *= upper[i] - lower[i]
        }

        return res
    }
}
