package scientifik.kmath.interpolation

import scientifik.kmath.functions.MathFunction

interface Interpolator<X, Y> {
    fun interpolate(points: Collection<Pair<X, Y>>): MathFunction<X, *, Y>
}