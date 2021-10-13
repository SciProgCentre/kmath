/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public class UnconstrainedDomain(override val dimension: Int) : DoubleDomain {
    override operator fun contains(point: Point<Double>): Boolean = true

    override fun getLowerBound(num: Int): Double = Double.NEGATIVE_INFINITY

    override fun getUpperBound(num: Int): Double = Double.POSITIVE_INFINITY

    override fun volume(): Double = Double.POSITIVE_INFINITY
}
