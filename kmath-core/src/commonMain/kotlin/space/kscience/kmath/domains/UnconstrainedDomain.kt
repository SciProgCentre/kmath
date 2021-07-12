/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI

@UnstableKMathAPI
public class UnconstrainedDomain(public override val dimension: Int) : DoubleDomain {
    public override operator fun contains(point: Point<Double>): Boolean = true

    public override fun getLowerBound(num: Int): Double = Double.NEGATIVE_INFINITY

    public override fun getUpperBound(num: Int): Double = Double.POSITIVE_INFINITY

    public override fun volume(): Double = Double.POSITIVE_INFINITY
}
