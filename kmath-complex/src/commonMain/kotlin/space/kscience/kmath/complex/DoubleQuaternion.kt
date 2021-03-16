/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.memory.MemoryReader
import space.kscience.kmath.memory.MemorySpec
import space.kscience.kmath.memory.MemoryWriter
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MemoryBuffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableMemoryBuffer
import kotlin.math.*

/**
 * This quaternion's conjugate.
 */
@UnstableKMathAPI
public val DoubleQuaternion.conjugate: DoubleQuaternion
    get() = DoubleQuaternionField { z - x * i - y * j - z * k }

/**
 * This quaternion's reciprocal.
 */
@UnstableKMathAPI
public val DoubleQuaternion.reciprocal: DoubleQuaternion
    get() = DoubleQuaternionField {
        val n = norm(this@reciprocal)
        return conjugate / (n * n)
    }

/**
 * Absolute value of the quaternion.
 */
@UnstableKMathAPI
public val DoubleQuaternion.r: Double
    get() = sqrt(w * w + x * x + y * y + z * z)

/**
 * A field of [DoubleQuaternion].
 */
@UnstableKMathAPI
public object DoubleQuaternionField : Field<DoubleQuaternion>, Norm<DoubleQuaternion, DoubleQuaternion>,
    PowerOperations<DoubleQuaternion>,
    ExponentialOperations<DoubleQuaternion>, NumbersAddOperations<DoubleQuaternion>, ScaleOperations<DoubleQuaternion> {
    override val zero: DoubleQuaternion = DoubleQuaternion(0)
    override val one: DoubleQuaternion = DoubleQuaternion(1)

    /**
     * The `i` quaternion unit.
     */
    public val i: DoubleQuaternion = DoubleQuaternion(0, 1)

    /**
     * The `j` quaternion unit.
     */
    public val j: DoubleQuaternion = DoubleQuaternion(0, 0, 1)

    /**
     * The `k` quaternion unit.
     */
    public val k: DoubleQuaternion = DoubleQuaternion(0, 0, 0, 1)

    override fun add(a: DoubleQuaternion, b: DoubleQuaternion): DoubleQuaternion =
        DoubleQuaternion(a.w + b.w, a.x + b.x, a.y + b.y, a.z + b.z)

    override fun scale(a: DoubleQuaternion, value: Double): DoubleQuaternion =
        DoubleQuaternion(a.w * value, a.x * value, a.y * value, a.z * value)

    override fun multiply(a: DoubleQuaternion, b: DoubleQuaternion): DoubleQuaternion = DoubleQuaternion(
        a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z,
        a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
        a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
        a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w,
    )

    override fun divide(a: DoubleQuaternion, b: DoubleQuaternion): DoubleQuaternion {
        val s = b.w * b.w + b.x * b.x + b.y * b.y + b.z * b.z

        return DoubleQuaternion(
            (b.w * a.w + b.x * a.x + b.y * a.y + b.z * a.z) / s,
            (b.w * a.x - b.x * a.w - b.y * a.z + b.z * a.y) / s,
            (b.w * a.y + b.x * a.z - b.y * a.w - b.z * a.x) / s,
            (b.w * a.z - b.x * a.y + b.y * a.x - b.z * a.w) / s,
        )
    }

    override fun power(arg: DoubleQuaternion, pow: Number): DoubleQuaternion {
        if (pow is Int) return pwr(arg, pow)
        if (floor(pow.toDouble()) == pow.toDouble()) return pwr(arg, pow.toInt())
        return exp(pow * ln(arg))
    }

    private fun pwr(x: DoubleQuaternion, a: Int): DoubleQuaternion = when {
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

    private fun pwr2(x: DoubleQuaternion): DoubleQuaternion {
        val aa = 2 * x.w
        return DoubleQuaternion(x.w * x.w - (x.x * x.x + x.y * x.y + x.z * x.z), aa * x.x, aa * x.y, aa * x.z)
    }

    private fun pwr3(x: DoubleQuaternion): DoubleQuaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 3.0 * a2 - n1
        return DoubleQuaternion(x.w * (a2 - 3 * n1), x.x * n2, x.y * n2, x.z * n2)
    }

    private fun pwr4(x: DoubleQuaternion): DoubleQuaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 4 * x.w * (a2 - n1)
        return DoubleQuaternion(a2 * a2 - 6 * a2 * n1 + n1 * n1, x.x * n2, x.y * n2, x.z * n2)
    }

    override fun exp(arg: DoubleQuaternion): DoubleQuaternion {
        val un = arg.x * arg.x + arg.y * arg.y + arg.z * arg.z
        if (un == 0.0) return DoubleQuaternion(exp(arg.w))
        val n1 = sqrt(un)
        val ea = exp(arg.w)
        val n2 = ea * sin(n1) / n1
        return DoubleQuaternion(ea * cos(n1), n2 * arg.x, n2 * arg.y, n2 * arg.z)
    }

    override fun ln(arg: DoubleQuaternion): DoubleQuaternion {
        val nu2 = arg.x * arg.x + arg.y * arg.y + arg.z * arg.z

        if (nu2 == 0.0)
            return if (arg.w > 0)
                DoubleQuaternion(ln(arg.w), 0, 0, 0)
            else {
                val l = ComplexDoubleField { ln(arg.w) }
                DoubleQuaternion(l.re, l.im, 0, 0)
            }

        val a = arg.w
        check(nu2 > 0)
        val n = sqrt(a * a + nu2)
        val th = acos(a / n) / sqrt(nu2)
        return DoubleQuaternion(ln(n), th * arg.x, th * arg.y, th * arg.z)
    }

    override operator fun Number.plus(b: DoubleQuaternion): DoubleQuaternion =
        DoubleQuaternion(toDouble() + b.w, b.x, b.y, b.z)

    override operator fun Number.minus(b: DoubleQuaternion): DoubleQuaternion =
        DoubleQuaternion(toDouble() - b.w, -b.x, -b.y, -b.z)

    override operator fun DoubleQuaternion.plus(b: Number): DoubleQuaternion =
        DoubleQuaternion(w + b.toDouble(), x, y, z)

    override operator fun DoubleQuaternion.minus(b: Number): DoubleQuaternion =
        DoubleQuaternion(w - b.toDouble(), x, y, z)

    override operator fun Number.times(b: DoubleQuaternion): DoubleQuaternion =
        DoubleQuaternion(toDouble() * b.w, toDouble() * b.x, toDouble() * b.y, toDouble() * b.z)

    override fun DoubleQuaternion.unaryMinus(): DoubleQuaternion = DoubleQuaternion(-w, -x, -y, -z)
    override fun norm(arg: DoubleQuaternion): DoubleQuaternion = sqrt(arg.conjugate * arg)

    override fun bindSymbolOrNull(value: String): DoubleQuaternion? = when (value) {
        "i" -> i
        "j" -> j
        "k" -> k
        else -> null
    }

    override fun number(value: Number): DoubleQuaternion = DoubleQuaternion(value)

    override fun sinh(arg: DoubleQuaternion): DoubleQuaternion = (exp(arg) - exp(-arg)) / 2.0
    override fun cosh(arg: DoubleQuaternion): DoubleQuaternion = (exp(arg) + exp(-arg)) / 2.0
    override fun tanh(arg: DoubleQuaternion): DoubleQuaternion = (exp(arg) - exp(-arg)) / (exp(-arg) + exp(arg))
    override fun asinh(arg: DoubleQuaternion): DoubleQuaternion = ln(sqrt(arg * arg + one) + arg)
    override fun acosh(arg: DoubleQuaternion): DoubleQuaternion = ln(arg + sqrt((arg - one) * (arg + one)))
    override fun atanh(arg: DoubleQuaternion): DoubleQuaternion = (ln(arg + one) - ln(one - arg)) / 2.0
}

/**
 * Represents `double`-based quaternion.
 *
 * @property w The first component.
 * @property x The second component.
 * @property y The third component.
 * @property z The fourth component.
 */
@UnstableKMathAPI
public data class DoubleQuaternion(
    val w: Double, val x: Double, val y: Double, val z: Double,
) {
    public constructor(w: Number, x: Number, y: Number, z: Number) : this(
        w.toDouble(),
        x.toDouble(),
        y.toDouble(),
        z.toDouble(),
    )

    public constructor(w: Number, x: Number, y: Number) : this(w.toDouble(), x.toDouble(), y.toDouble(), 0.0)
    public constructor(w: Number, x: Number) : this(w.toDouble(), x.toDouble(), 0.0, 0.0)
    public constructor(w: Number) : this(w.toDouble(), 0.0, 0.0, 0.0)
    public constructor(wx: Complex<Number>, yz: Complex<Number>) : this(wx.re, wx.im, yz.re, yz.im)
    public constructor(wx: Complex<Number>) : this(wx.re, wx.im, 0, 0)

    init {
        require(!w.isNaN()) { "w-component of quaternion is not-a-number" }
        require(!x.isNaN()) { "x-component of quaternion is not-a-number" }
        require(!y.isNaN()) { "x-component of quaternion is not-a-number" }
        require(!z.isNaN()) { "x-component of quaternion is not-a-number" }
    }

    /**
     * Returns a string representation of this quaternion.
     */
    override fun toString(): String = "($w + $x * i + $y * j + $z * k)"

    public companion object : MemorySpec<DoubleQuaternion> {
        override val objectSize: Int
            get() = 32

        override fun MemoryReader.read(offset: Int): DoubleQuaternion =
            DoubleQuaternion(readDouble(offset),
                readDouble(offset + 8),
                readDouble(offset + 16),
                readDouble(offset + 24))

        override fun MemoryWriter.write(offset: Int, value: DoubleQuaternion) {
            writeDouble(offset, value.w)
            writeDouble(offset + 8, value.x)
            writeDouble(offset + 16, value.y)
            writeDouble(offset + 24, value.z)
        }
    }
}

/**
 * Creates a new buffer of quaternions with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
@UnstableKMathAPI
public inline fun Buffer.Companion.quaternion(size: Int, init: (Int) -> DoubleQuaternion): Buffer<DoubleQuaternion> =
    MemoryBuffer.create(DoubleQuaternion, size, init)

/**
 * Creates a new buffer of quaternions with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
@UnstableKMathAPI
public inline fun MutableBuffer.Companion.quaternion(
    size: Int,
    init: (Int) -> DoubleQuaternion,
): MutableBuffer<DoubleQuaternion> =
    MutableMemoryBuffer.create(DoubleQuaternion, size, init)


