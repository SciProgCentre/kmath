/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.memory.MemoryReader
import space.kscience.kmath.memory.MemorySpec
import space.kscience.kmath.memory.MemoryWriter
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MemoryBuffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableMemoryBuffer
import kotlin.math.*

/**
 * Represents `double`-based quaternion.
 *
 * @property w The first component.
 * @property x The second component.
 * @property y The third component.
 * @property z The fourth component.
 */
public class Quaternion(
    public val w: Double,
    public val x: Double,
    public val y: Double,
    public val z: Double,
) : Buffer<Double> {
    init {
        require(!w.isNaN()) { "w-component of quaternion is not-a-number" }
        require(!x.isNaN()) { "x-component of quaternion is not-a-number" }
        require(!y.isNaN()) { "y-component of quaternion is not-a-number" }
        require(!z.isNaN()) { "z-component of quaternion is not-a-number" }
    }

    /**
     * Returns a string representation of this quaternion.
     */
    override fun toString(): String = "($w + $x * i + $y * j + $z * k)"

    override val size: Int get() = 4

    override fun get(index: Int): Double = when (index) {
        0 -> w
        1 -> x
        2 -> y
        3 -> z
        else -> error("Index $index out of bounds [0,3]")
    }

    override fun iterator(): Iterator<Double> = listOf(w, x, y, z).iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Quaternion

        if (w != other.w) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = w.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    public companion object : MemorySpec<Quaternion> {
        override val objectSize: Int get() = 32

        override fun MemoryReader.read(offset: Int): Quaternion = Quaternion(
            readDouble(offset),
            readDouble(offset + 8),
            readDouble(offset + 16),
            readDouble(offset + 24)
        )

        override fun MemoryWriter.write(offset: Int, value: Quaternion) {
            writeDouble(offset, value.w)
            writeDouble(offset + 8, value.x)
            writeDouble(offset + 16, value.y)
            writeDouble(offset + 24, value.z)
        }
    }
}

public fun Quaternion(w: Number, x: Number = 0.0, y: Number = 0.0, z: Number = 0.0): Quaternion = Quaternion(
    w.toDouble(),
    x.toDouble(),
    y.toDouble(),
    z.toDouble(),
)

/**
 * This quaternion's conjugate.
 */
public val Quaternion.conjugate: Quaternion
    get() = Quaternion(w, -x, -y, -z)

/**
 * This quaternion's reciprocal.
 */
public val Quaternion.reciprocal: Quaternion
    get() {
        val norm2 = (w * w + x * x + y * y + z * z)
        return Quaternion(w / norm2, -x / norm2, -y / norm2, -z / norm2)
    }


//TODO consider adding a-priory normalized quaternions
/**
 * Produce a normalized version of this quaternion
 */
public fun Quaternion.normalized(): Quaternion = with(QuaternionField){ this@normalized / norm(this@normalized) }

/**
 * A field of [Quaternion].
 */
@OptIn(UnstableKMathAPI::class)
public object QuaternionField : Field<Quaternion>, Norm<Quaternion, Double>, PowerOperations<Quaternion>,
    ExponentialOperations<Quaternion>, NumbersAddOps<Quaternion>, ScaleOperations<Quaternion> {
    override val zero: Quaternion = Quaternion(0.0)
    override val one: Quaternion = Quaternion(1.0)

    /**
     * The `i` quaternion unit.
     */
    public val i: Quaternion = Quaternion(0.0, 1.0, 0.0, 0.0)

    /**
     * The `j` quaternion unit.
     */
    public val j: Quaternion = Quaternion(0.0, 0.0, 1.0, 0.0)

    /**
     * The `k` quaternion unit.
     */
    public val k: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)

    override fun add(left: Quaternion, right: Quaternion): Quaternion =
        Quaternion(left.w + right.w, left.x + right.x, left.y + right.y, left.z + right.z)

    override fun scale(a: Quaternion, value: Double): Quaternion =
        Quaternion(a.w * value, a.x * value, a.y * value, a.z * value)

    override fun multiply(left: Quaternion, right: Quaternion): Quaternion = Quaternion(
        left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z,
        left.w * right.x + left.x * right.w + left.y * right.z - left.z * right.y,
        left.w * right.y - left.x * right.z + left.y * right.w + left.z * right.x,
        left.w * right.z + left.x * right.y - left.y * right.x + left.z * right.w,
    )

    override fun divide(left: Quaternion, right: Quaternion): Quaternion {
        val s = right.w * right.w + right.x * right.x + right.y * right.y + right.z * right.z

        return Quaternion(
            (right.w * left.w + right.x * left.x + right.y * left.y + right.z * left.z) / s,
            (right.w * left.x - right.x * left.w - right.y * left.z + right.z * left.y) / s,
            (right.w * left.y + right.x * left.z - right.y * left.w - right.z * left.x) / s,
            (right.w * left.z - right.x * left.y + right.y * left.x - right.z * left.w) / s,
        )
    }

    override fun power(arg: Quaternion, pow: Number): Quaternion {
        if (pow is Int) return pwr(arg, pow)
        if (floor(pow.toDouble()) == pow.toDouble()) return pwr(arg, pow.toInt())
        return exp(pow * ln(arg))
    }

    private fun pwr(x: Quaternion, a: Int): Quaternion = when {
        a < 0 -> -(pwr(x, -a))
        a == 0 -> one
        a == 1 -> x
        a == 2 -> pwr2(x)
        a == 3 -> pwr3(x)
        a == 4 -> pwr4(x)

        else -> {
            val x4 = pwr4(x)
            var y = x4
            repeat((1 until a / 4).count()) { y *= x4 }
            if (a % 4 == 3) y *= pwr3(x)
            if (a % 4 == 2) y *= pwr2(x)
            if (a % 4 == 1) y *= x
            y
        }
    }

    private fun pwr2(x: Quaternion): Quaternion {
        val aa = 2 * x.w
        return Quaternion(x.w * x.w - (x.x * x.x + x.y * x.y + x.z * x.z), aa * x.x, aa * x.y, aa * x.z)
    }

    private fun pwr3(x: Quaternion): Quaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 3.0 * a2 - n1
        return Quaternion(x.w * (a2 - 3 * n1), x.x * n2, x.y * n2, x.z * n2)
    }

    private fun pwr4(x: Quaternion): Quaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 4 * x.w * (a2 - n1)
        return Quaternion(a2 * a2 - 6 * a2 * n1 + n1 * n1, x.x * n2, x.y * n2, x.z * n2)
    }

    override fun exp(arg: Quaternion): Quaternion {
        val un = arg.x * arg.x + arg.y * arg.y + arg.z * arg.z
        if (un == 0.0) return Quaternion(exp(arg.w))
        val n1 = sqrt(un)
        val ea = exp(arg.w)
        val n2 = ea * sin(n1) / n1
        return Quaternion(ea * cos(n1), n2 * arg.x, n2 * arg.y, n2 * arg.z)
    }

    override fun ln(arg: Quaternion): Quaternion {
        val nu2 = arg.x * arg.x + arg.y * arg.y + arg.z * arg.z

        if (nu2 == 0.0)
            return if (arg.w > 0)
                Quaternion(ln(arg.w), 0, 0, 0)
            else {
                val l = ComplexField { ln(arg.w.toComplex()) }
                Quaternion(l.re, l.im, 0, 0)
            }

        val a = arg.w
        check(nu2 > 0)
        val n = sqrt(a * a + nu2)
        val th = acos(a / n) / sqrt(nu2)
        return Quaternion(ln(n), th * arg.x, th * arg.y, th * arg.z)
    }

    override operator fun Number.plus(other: Quaternion): Quaternion =
        Quaternion(toDouble() + other.w, other.x, other.y, other.z)

    override operator fun Number.minus(other: Quaternion): Quaternion =
        Quaternion(toDouble() - other.w, -other.x, -other.y, -other.z)

    override operator fun Quaternion.plus(other: Number): Quaternion = Quaternion(w + other.toDouble(), x, y, z)
    override operator fun Quaternion.minus(other: Number): Quaternion = Quaternion(w - other.toDouble(), x, y, z)

    override operator fun Number.times(arg: Quaternion): Quaternion =
        Quaternion(toDouble() * arg.w, toDouble() * arg.x, toDouble() * arg.y, toDouble() * arg.z)

    override fun Quaternion.unaryMinus(): Quaternion = Quaternion(-w, -x, -y, -z)
    override fun norm(arg: Quaternion): Double = sqrt(
        arg.w.pow(2) +
                arg.x.pow(2) +
                arg.y.pow(2) +
                arg.z.pow(2)
    )

    override fun bindSymbolOrNull(value: String): Quaternion? = when (value) {
        "i" -> i
        "j" -> j
        "k" -> k
        else -> null
    }

    override fun number(value: Number): Quaternion = Quaternion(value)

    override fun sinh(arg: Quaternion): Quaternion = (exp(arg) - exp(-arg)) / 2.0
    override fun cosh(arg: Quaternion): Quaternion = (exp(arg) + exp(-arg)) / 2.0
    override fun tanh(arg: Quaternion): Quaternion = (exp(arg) - exp(-arg)) / (exp(-arg) + exp(arg))
    override fun asinh(arg: Quaternion): Quaternion = ln(sqrt(arg * arg + one) + arg)
    override fun acosh(arg: Quaternion): Quaternion = ln(arg + sqrt((arg - one) * (arg + one)))
    override fun atanh(arg: Quaternion): Quaternion = (ln(arg + one) - ln(one - arg)) / 2.0
}

/**
 * Creates a new buffer of quaternions with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public inline fun Buffer.Companion.quaternion(size: Int, init: (Int) -> Quaternion): Buffer<Quaternion> =
    MemoryBuffer.create(Quaternion, size, init)

/**
 * Creates a new buffer of quaternions with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public inline fun MutableBuffer.Companion.quaternion(size: Int, init: (Int) -> Quaternion): MutableBuffer<Quaternion> =
    MutableMemoryBuffer.create(Quaternion, size, init)
