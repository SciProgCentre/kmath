/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
