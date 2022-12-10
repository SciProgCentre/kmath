/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import kotlin.math.abs

/**
 * Based on Commons Math implementation.
 * See [https://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/special/Erf.html].
 */
internal object InternalErf {
    fun erfc(x: Double): Double {
        if (abs(x) > 40) return if (x > 0) 0.0 else 2.0
        val ret = InternalGamma.regularizedGammaQ(0.5, x * x, 10000)
        return if (x < 0) 2 - ret else ret
    }
}