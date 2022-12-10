/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import kotlin.math.*

private abstract class ContinuedFraction protected constructor() {
    protected abstract fun getA(n: Int, x: Double): Double
    protected abstract fun getB(n: Int, x: Double): Double

    fun evaluate(x: Double, maxIterations: Int): Double {
        val small = 1e-50
        var hPrev = getA(0, x)
        if (hPrev == 0.0 || abs(0.0 - hPrev) <= small) hPrev = small
        var n = 1
        var dPrev = 0.0
        var cPrev = hPrev
        var hN = hPrev

        while (n < maxIterations) {
            val a = getA(n, x)
            val b = getB(n, x)
            var dN = a + b * dPrev
            if (dN == 0.0 || abs(0.0 - dN) <= small) dN = small
            var cN = a + b / cPrev
            if (cN == 0.0 || abs(0.0 - cN) <= small) cN = small
            dN = 1 / dN
            val deltaN = cN * dN
            hN = hPrev * deltaN
            check(!hN.isInfinite()) { "hN is infinite" }
            check(!hN.isNaN()) { "hN is NaN" }
            if (abs(deltaN - 1.0) < 10e-9) break
            dPrev = dN
            cPrev = cN
            hPrev = hN
            n++
        }

        check(n < maxIterations) { "n is more than maxIterations" }
        return hN
    }
}

internal object InternalGamma {
    const val LANCZOS_G = 607.0 / 128.0

    private val LANCZOS = doubleArrayOf(
        0.99999999999999709182,
        57.156235665862923517,
        -59.597960355475491248,
        14.136097974741747174,
        -0.49191381609762019978,
        .33994649984811888699e-4,
        .46523628927048575665e-4,
        -.98374475304879564677e-4,
        .15808870322491248884e-3,
        -.21026444172410488319e-3,
        .21743961811521264320e-3,
        -.16431810653676389022e-3,
        .84418223983852743293e-4,
        -.26190838401581408670e-4,
        .36899182659531622704e-5
    )

    private val HALF_LOG_2_PI = 0.5 * ln(2.0 * PI)
    private const val INV_GAMMA1P_M1_A0 = .611609510448141581788E-08
    private const val INV_GAMMA1P_M1_A1 = .624730830116465516210E-08
    private const val INV_GAMMA1P_M1_B1 = .203610414066806987300E+00
    private const val INV_GAMMA1P_M1_B2 = .266205348428949217746E-01
    private const val INV_GAMMA1P_M1_B3 = .493944979382446875238E-03
    private const val INV_GAMMA1P_M1_B4 = -.851419432440314906588E-05
    private const val INV_GAMMA1P_M1_B5 = -.643045481779353022248E-05
    private const val INV_GAMMA1P_M1_B6 = .992641840672773722196E-06
    private const val INV_GAMMA1P_M1_B7 = -.607761895722825260739E-07
    private const val INV_GAMMA1P_M1_B8 = .195755836614639731882E-09
    private const val INV_GAMMA1P_M1_P0 = .6116095104481415817861E-08
    private const val INV_GAMMA1P_M1_P1 = .6871674113067198736152E-08
    private const val INV_GAMMA1P_M1_P2 = .6820161668496170657918E-09
    private const val INV_GAMMA1P_M1_P3 = .4686843322948848031080E-10
    private const val INV_GAMMA1P_M1_P4 = .1572833027710446286995E-11
    private const val INV_GAMMA1P_M1_P5 = -.1249441572276366213222E-12
    private const val INV_GAMMA1P_M1_P6 = .4343529937408594255178E-14
    private const val INV_GAMMA1P_M1_Q1 = .3056961078365221025009E+00
    private const val INV_GAMMA1P_M1_Q2 = .5464213086042296536016E-01
    private const val INV_GAMMA1P_M1_Q3 = .4956830093825887312020E-02
    private const val INV_GAMMA1P_M1_Q4 = .2692369466186361192876E-03
    private const val INV_GAMMA1P_M1_C = -.422784335098467139393487909917598E+00
    private const val INV_GAMMA1P_M1_C0 = .577215664901532860606512090082402E+00
    private const val INV_GAMMA1P_M1_C1 = -.655878071520253881077019515145390E+00
    private const val INV_GAMMA1P_M1_C2 = -.420026350340952355290039348754298E-01
    private const val INV_GAMMA1P_M1_C3 = .166538611382291489501700795102105E+00
    private const val INV_GAMMA1P_M1_C4 = -.421977345555443367482083012891874E-01
    private const val INV_GAMMA1P_M1_C5 = -.962197152787697356211492167234820E-02
    private const val INV_GAMMA1P_M1_C6 = .721894324666309954239501034044657E-02
    private const val INV_GAMMA1P_M1_C7 = -.116516759185906511211397108401839E-02
    private const val INV_GAMMA1P_M1_C8 = -.215241674114950972815729963053648E-03
    private const val INV_GAMMA1P_M1_C9 = .128050282388116186153198626328164E-03
    private const val INV_GAMMA1P_M1_C10 = -.201348547807882386556893914210218E-04
    private const val INV_GAMMA1P_M1_C11 = -.125049348214267065734535947383309E-05
    private const val INV_GAMMA1P_M1_C12 = .113302723198169588237412962033074E-05
    private const val INV_GAMMA1P_M1_C13 = -.205633841697760710345015413002057E-06

    fun logGamma(x: Double): Double = when {
        x.isNaN() || x <= 0.0 -> Double.NaN
        x < 0.5 -> logGamma1p(x) - ln(x)
        x <= 2.5 -> logGamma1p(x - 0.5 - 0.5)

        x <= 8.0 -> {
            val n = floor(x - 1.5).toInt()
            val prod = (1..n).fold(1.0) { prod, i -> prod * (x - i) }
            logGamma1p(x - (n + 1)) + ln(prod)
        }

        else -> {
            val tmp = x + LANCZOS_G + .5
            (x + .5) * ln(tmp) - tmp + HALF_LOG_2_PI + ln(lanczos(x) / x)
        }
    }

    private fun regularizedGammaP(
        a: Double,
        x: Double,
        maxIterations: Int = Int.MAX_VALUE
    ): Double = when {
        a.isNaN() || x.isNaN() || a <= 0.0 || x < 0.0 -> Double.NaN
        x == 0.0 -> 0.0
        x >= a + 1 -> 1.0 - regularizedGammaQ(a, x, maxIterations)

        else -> {
            // calculate series
            var n = 0.0 // current element index
            var an = 1.0 / a // n-th element in the series
            var sum = an // partial sum

            while (abs(an / sum) > 10e-15 && n < maxIterations && sum < Double.POSITIVE_INFINITY) {
                // compute next element in the series
                n += 1.0
                an *= x / (a + n)

                // update partial sum
                sum += an
            }

            when {
                n >= maxIterations -> error("Maximal iterations is exceeded $maxIterations")
                sum.isInfinite() -> 1.0
                else -> exp(-x + a * ln(x) - logGamma(a)) * sum
            }
        }
    }

    fun regularizedGammaQ(
        a: Double,
        x: Double,
        maxIterations: Int = Int.MAX_VALUE
    ): Double = when {
        a.isNaN() || x.isNaN() || a <= 0.0 || x < 0.0 -> Double.NaN
        x == 0.0 -> 1.0
        x < a + 1.0 -> 1.0 - regularizedGammaP(a, x, maxIterations)

        else -> 1.0 / object : ContinuedFraction() {
            override fun getA(n: Int, x: Double): Double = 2.0 * n + 1.0 - a + x
            override fun getB(n: Int, x: Double): Double = n * (a - n)
        }.evaluate(x, maxIterations) * exp(-x + a * ln(x) - logGamma(a))
    }

    private fun lanczos(x: Double): Double =
        (LANCZOS.size - 1 downTo 1).sumOf { LANCZOS[it] / (x + it) } + LANCZOS[0]

    private fun invGamma1pm1(x: Double): Double {
        require(x >= -0.5)
        require(x <= 1.5)
        val ret: Double
        val t = if (x <= 0.5) x else x - 0.5 - 0.5

        if (t < 0.0) {
            val a = INV_GAMMA1P_M1_A0 + t * INV_GAMMA1P_M1_A1
            var b = INV_GAMMA1P_M1_B8
            b = INV_GAMMA1P_M1_B7 + t * b
            b = INV_GAMMA1P_M1_B6 + t * b
            b = INV_GAMMA1P_M1_B5 + t * b
            b = INV_GAMMA1P_M1_B4 + t * b
            b = INV_GAMMA1P_M1_B3 + t * b
            b = INV_GAMMA1P_M1_B2 + t * b
            b = INV_GAMMA1P_M1_B1 + t * b
            b = 1.0 + t * b
            var c = INV_GAMMA1P_M1_C13 + t * (a / b)
            c = INV_GAMMA1P_M1_C12 + t * c
            c = INV_GAMMA1P_M1_C11 + t * c
            c = INV_GAMMA1P_M1_C10 + t * c
            c = INV_GAMMA1P_M1_C9 + t * c
            c = INV_GAMMA1P_M1_C8 + t * c
            c = INV_GAMMA1P_M1_C7 + t * c
            c = INV_GAMMA1P_M1_C6 + t * c
            c = INV_GAMMA1P_M1_C5 + t * c
            c = INV_GAMMA1P_M1_C4 + t * c
            c = INV_GAMMA1P_M1_C3 + t * c
            c = INV_GAMMA1P_M1_C2 + t * c
            c = INV_GAMMA1P_M1_C1 + t * c
            c = INV_GAMMA1P_M1_C + t * c
            ret = (if (x > 0.5) t * c / x else x * (c + 0.5 + 0.5))
        } else {
            var p = INV_GAMMA1P_M1_P6
            p = INV_GAMMA1P_M1_P5 + t * p
            p = INV_GAMMA1P_M1_P4 + t * p
            p = INV_GAMMA1P_M1_P3 + t * p
            p = INV_GAMMA1P_M1_P2 + t * p
            p = INV_GAMMA1P_M1_P1 + t * p
            p = INV_GAMMA1P_M1_P0 + t * p
            var q = INV_GAMMA1P_M1_Q4
            q = INV_GAMMA1P_M1_Q3 + t * q
            q = INV_GAMMA1P_M1_Q2 + t * q
            q = INV_GAMMA1P_M1_Q1 + t * q
            q = 1.0 + t * q
            var c = INV_GAMMA1P_M1_C13 + p / q * t
            c = INV_GAMMA1P_M1_C12 + t * c
            c = INV_GAMMA1P_M1_C11 + t * c
            c = INV_GAMMA1P_M1_C10 + t * c
            c = INV_GAMMA1P_M1_C9 + t * c
            c = INV_GAMMA1P_M1_C8 + t * c
            c = INV_GAMMA1P_M1_C7 + t * c
            c = INV_GAMMA1P_M1_C6 + t * c
            c = INV_GAMMA1P_M1_C5 + t * c
            c = INV_GAMMA1P_M1_C4 + t * c
            c = INV_GAMMA1P_M1_C3 + t * c
            c = INV_GAMMA1P_M1_C2 + t * c
            c = INV_GAMMA1P_M1_C1 + t * c
            c = INV_GAMMA1P_M1_C0 + t * c
            ret = (if (x > 0.5) t / x * (c - 0.5 - 0.5) else x * c)
        }

        return ret
    }

    private fun logGamma1p(x: Double): Double {
        require(x >= -0.5)
        require(x <= 1.5)
        return -ln1p(invGamma1pm1(x))
    }
}
