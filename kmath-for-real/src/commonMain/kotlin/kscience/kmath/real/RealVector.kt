package kscience.kmath.real

import kscience.kmath.linear.Point
import kscience.kmath.operations.Norm
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.asIterable
import kotlin.math.pow
import kotlin.math.sqrt

public typealias RealVector = Point<Double>

public object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double = sqrt(arg.asIterable().sumByDouble(Number::toDouble))
}

/**
 * Fill the vector of given [size] with given [value]
 */
public fun Buffer.Companion.same(size: Int, value: Number): RealVector = real(size) { value.toDouble() }

// Transformation methods

public inline fun RealVector.map(transform: (Double) -> Double): RealVector =
    Buffer.real(size) { transform(get(it)) }

public inline fun RealVector.mapIndexed(transform: (index: Int, value: Double) -> Double): RealVector =
    Buffer.real(size) { transform(it, get(it)) }

public fun RealVector.pow(p: Double): RealVector = map { it.pow(p) }

public fun RealVector.pow(p: Int): RealVector = map { it.pow(p) }

public fun exp(vector: RealVector): RealVector = vector.map { kotlin.math.exp(it) }

public operator fun RealVector.plus(other: RealVector): RealVector =
    mapIndexed { index, value -> value + other[index] }

public operator fun RealVector.plus(number: Number): RealVector = map { it + number.toDouble() }

public operator fun Number.plus(vector: RealVector): RealVector = vector + this

public operator fun RealVector.unaryMinus(): Buffer<Double> = map { -it }

public operator fun RealVector.minus(other: RealVector): RealVector =
    mapIndexed { index, value -> value - other[index] }

public operator fun RealVector.minus(number: Number): RealVector = map { it - number.toDouble() }

public operator fun Number.minus(vector: RealVector): RealVector = vector.map { toDouble() - it }

public operator fun RealVector.times(other: RealVector): RealVector =
    mapIndexed { index, value -> value * other[index] }

public operator fun RealVector.times(number: Number): RealVector = map { it * number.toDouble() }

public operator fun Number.times(vector: RealVector): RealVector = vector * this

public operator fun RealVector.div(other: RealVector): RealVector =
    mapIndexed { index, value -> value / other[index] }

public operator fun RealVector.div(number: Number): RealVector = map { it / number.toDouble() }

public operator fun Number.div(vector: RealVector): RealVector = vector.map { toDouble() / it }
