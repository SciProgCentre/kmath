package kscience.kmath.stat.internal

import kotlin.math.abs

internal object InternalErf {
    fun erfc(x: Double): Double {
        if (abs(x) > 40) return if (x > 0) 0.0 else 2.0
        val ret = InternalGamma.regularizedGammaQ(0.5, x * x, 10000)
        return if (x < 0) 2 - ret else ret
    }
}