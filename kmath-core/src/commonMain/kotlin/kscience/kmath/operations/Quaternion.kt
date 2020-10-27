package kscience.kmath.operations

import kscience.kmath.memory.MemoryReader
import kscience.kmath.memory.MemorySpec
import kscience.kmath.memory.MemoryWriter
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.MemoryBuffer
import kscience.kmath.structures.MutableBuffer
import kscience.kmath.structures.MutableMemoryBuffer
import kotlin.math.*

/**
 * This quaternion's conjugate.
 */
public val Quaternion.conjugate: Quaternion
    get() = QuaternionField { z - x * i - y * j - z * k }

/**
 * This quaternion's reciprocal.
 */
public val Quaternion.reciprocal: Quaternion
    get() {
        val n = QuaternionField { norm(this@reciprocal) }
        return conjugate / (n * n)
    }

/**
 * Absolute value of the quaternion.
 */
public val Quaternion.r: Double
    get() = sqrt(w * w + x * x + y * y + z * z)

/**
 * A field of [Quaternion].
 */
public object QuaternionField : Field<Quaternion>, Norm<Quaternion, Quaternion>, PowerOperations<Quaternion>,
    ExponentialOperations<Quaternion> {
    override val zero: Quaternion by lazy { 0.toQuaternion() }
    override val one: Quaternion by lazy { 1.toQuaternion() }

    /**
     * The `i` quaternion unit.
     */
    public val i: Quaternion by lazy { Quaternion(0, 1, 0, 0) }

    /**
     * The `j` quaternion unit.
     */
    public val j: Quaternion by lazy { Quaternion(0, 0, 1, 0) }

    /**
     * The `k` quaternion unit.
     */
    public val k: Quaternion by lazy { Quaternion(0, 0, 0, 1) }

    public override fun add(a: Quaternion, b: Quaternion): Quaternion =
        Quaternion(a.w + b.w, a.x + b.x, a.y + b.y, a.z + b.z)

    public override fun multiply(a: Quaternion, k: Number): Quaternion {
        val d = k.toDouble()
        return Quaternion(a.w * d, a.x * d, a.y * d, a.z * d)
    }

    public override fun multiply(a: Quaternion, b: Quaternion): Quaternion = Quaternion(
        a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z,
        a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
        a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
        a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w,
    )

    override fun divide(a: Quaternion, b: Quaternion): Quaternion {
        val s = b.w * b.w + b.x * b.x + b.y * b.y + b.z * b.z

        return Quaternion(
            (b.w * a.w + b.x * a.x + b.y * a.y + b.z * a.z) / s,
            (b.w * a.x - b.x * a.w - b.y * a.z + b.z * a.y) / s,
            (b.w * a.y + b.x * a.z - b.y * a.w - b.z * a.x) / s,
            (b.w * a.z - b.x * a.y + b.y * a.x - b.z * a.w) / s,
        )
    }

    public override fun power(arg: Quaternion, pow: Number): Quaternion {
        if (pow is Int) return pwr(arg, pow)
        if (floor(pow.toDouble()) == pow.toDouble()) return pwr(arg, pow.toInt())
        return exp(pow * ln(arg))
    }

    private fun pwr(x: Quaternion, a: Int): Quaternion {
        if (a < 0) return -(pwr(x, -a))
        if (a == 0) return one
        if (a == 1) return x
        if (a == 2) return pwr2(x)
        if (a == 3) return pwr3(x)
        if (a == 4) return pwr4(x)
        val x4 = pwr4(x)
        var y = x4
        repeat((1 until a / 4).count()) { y *= x4 }
        if (a % 4 == 3) y *= pwr3(x)
        if (a % 4 == 2) y *= pwr2(x)
        if (a % 4 == 1) y *= x
        return y
    }

    private inline fun pwr2(x: Quaternion): Quaternion {
        val aa = 2 * x.w

        return Quaternion(
            x.w * x.w - (x.x * x.x + x.y * x.y + x.z * x.z),
            aa * x.x,
            aa * x.y,
            aa * x.z
        )
    }

    private inline fun pwr3(x: Quaternion): Quaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 3.0 * a2 - n1

        return Quaternion(
            x.w * (a2 - 3 * n1),
            x.x * n2,
            x.y * n2,
            x.z * n2
        )
    }

    private inline fun pwr4(x: Quaternion): Quaternion {
        val a2 = x.w * x.w
        val n1 = x.x * x.x + x.y * x.y + x.z * x.z
        val n2 = 4 * x.w * (a2 - n1)

        return Quaternion(
            a2 * a2 - 6 * a2 * n1 + n1 * n1,
            x.x * n2,
            x.y * n2,
            x.z * n2
        )
    }

    public override fun exp(arg: Quaternion): Quaternion {
        val un = arg.x * arg.x + arg.y * arg.y + arg.z * arg.z
        if (un == 0.0) return exp(arg.w).toQuaternion()
        val n1 = sqrt(un)
        val ea = exp(arg.w)
        val n2 = ea * sin(n1) / n1
        return Quaternion(ea * cos(n1), n2 * arg.x, n2 * arg.y, n2 * arg.z)
    }

    public override fun ln(arg: Quaternion): Quaternion {
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

    /**
     * Adds quaternion to real one.
     *
     * @receiver the addend.
     * @param b the augend.
     * @return the sum.
     */
    public override operator fun Number.plus(b: Quaternion): Quaternion = Quaternion(toDouble() + b.w, b.x, b.y, b.z)

    /**
     * Subtracts quaternion from real one.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @return the difference.
     */
    public override operator fun Number.minus(b: Quaternion): Quaternion =
        Quaternion(toDouble() - b.w, -b.x, -b.y, -b.z)

    /**
     * Adds real number to quaternion.
     *
     * @receiver the addend.
     * @param b the augend.
     * @return the sum.
     */
    public override operator fun Quaternion.plus(b: Number): Quaternion = Quaternion(w + b.toDouble(), x, y, z)

    /**
     * Subtracts real number from quaternion.
     *
     * @receiver the minuend.
     * @param b the subtrahend.
     * @return the difference.
     */
    public override operator fun Quaternion.minus(b: Number): Quaternion = Quaternion(w - b.toDouble(), x, y, z)

    /**
     * Multiplies real number by quaternion.
     *
     * @receiver the multiplier.
     * @param b the multiplicand.
     * @receiver the product.
     */
    public override operator fun Number.times(b: Quaternion): Quaternion =
        Quaternion(toDouble() * b.w, toDouble() * b.x, toDouble() * b.y, toDouble() * b.z)

    public override fun Quaternion.unaryMinus(): Quaternion = Quaternion(-w, -x, -y, -z)
    public override fun norm(arg: Quaternion): Quaternion = sqrt(arg.conjugate * arg)

    public override fun symbol(value: String): Quaternion = when (value) {
        "i" -> i
        "j" -> j
        "k" -> k
        else -> super<Field>.symbol(value)
    }
}

/**
 * Represents `double`-based quaternion.
 *
 * @property w The first component.
 * @property x The second component.
 * @property y The third component.
 * @property z The fourth component.
 */
public data class Quaternion(val w: Double, val x: Double, val y: Double, val z: Double) :
    FieldElement<Quaternion, Quaternion, QuaternionField>,
    Comparable<Quaternion> {
    public constructor(w: Number, x: Number, y: Number, z: Number) : this(
        w.toDouble(),
        x.toDouble(),
        y.toDouble(),
        z.toDouble()
    )

    public constructor(wx: Complex, yz: Complex) : this(wx.re, wx.im, yz.re, yz.im)

    public override val context: QuaternionField
        get() = QuaternionField

    public override fun div(k: Number): Quaternion {
        val d = k.toDouble()
        return Quaternion(w / d, x / d, y / d, z / d)
    }

    public override fun unwrap(): Quaternion = this
    public override fun Quaternion.wrap(): Quaternion = this
    public override fun compareTo(other: Quaternion): Int = r.compareTo(other.r)
    public override fun toString(): String = "($w + $x * i + $y * j + $z * k)"

    public companion object : MemorySpec<Quaternion> {
        public override val objectSize: Int
            get() = 32

        public override fun MemoryReader.read(offset: Int): Quaternion =
            Quaternion(readDouble(offset), readDouble(offset + 8), readDouble(offset + 16), readDouble(offset + 24))

        public override fun MemoryWriter.write(offset: Int, value: Quaternion) {
            writeDouble(offset, value.w)
            writeDouble(offset + 8, value.x)
            writeDouble(offset + 16, value.y)
            writeDouble(offset + 24, value.z)
        }
    }
}

/**
 * Creates a quaternion with real part equal to this real.
 *
 * @receiver the real part.
 * @return the new quaternion.
 */
public fun Number.toQuaternion(): Quaternion = Quaternion(this, 0, 0, 0)

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
