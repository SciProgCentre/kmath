/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Point

@UnstableKMathAPI
public class UnconstrainedDomain(override val dimension: Int) : DoubleDomain {
    override operator fun contains(point: Point<Double>): Boolean = true

    override fun getLowerBound(num: Int): Double = Double.NEGATIVE_INFINITY

    override fun getUpperBound(num: Int): Double = Double.POSITIVE_INFINITY

    override fun volume(): Double = Double.POSITIVE_INFINITY
}
