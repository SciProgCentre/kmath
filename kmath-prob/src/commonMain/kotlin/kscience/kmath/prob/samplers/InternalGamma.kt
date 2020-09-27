package kscience.kmath.prob.samplers

import kotlin.math.PI
import kotlin.math.ln

internal object InternalGamma {
    private const val LANCZOS_G = 607.0 / 128.0

    private val LANCZOS_COEFFICIENTS = doubleArrayOf(
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

    private val HALF_LOG_2_PI: Double = 0.5 * ln(2.0 * PI)

    fun logGamma(x: Double): Double {
        // Stripped-down version of the same method defined in "Commons Math":
        // Unused "if" branches (for when x < 8) have been removed here since
        // this method is only used (by class "InternalUtils") in order to
        // compute log(n!) for x > 20.
        val sum = lanczos(x)
        val tmp = x + LANCZOS_G + 0.5
        return (x + 0.5) * ln(tmp) - tmp + HALF_LOG_2_PI + ln(sum / x)
    }

    private fun lanczos(x: Double): Double {
        val sum = (LANCZOS_COEFFICIENTS.size - 1 downTo 1).sumByDouble { LANCZOS_COEFFICIENTS[it] / (x + it) }
        return sum + LANCZOS_COEFFICIENTS[0]
    }
}
