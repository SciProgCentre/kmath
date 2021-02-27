package space.kscience.kmath.units

import space.kscience.kmath.operations.*
import kotlin.jvm.JvmName
import kotlin.math.PI
import kotlin.math.pow

/**
 * Represents base units of International System of Units.
 */
public enum class BaseUnits {
    /**
     * The base unit of time.
     */
    SECOND,

    /**
     * The base unit of length.
     */
    METER,

    /**
     * The base unit of mass.
     */
    KILOGRAM,

    /**
     * The base unit of electric current.
     */
    AMPERE,

    /**
     * The base unit of thermodynamic temperature.
     */
    KELVIN,

    /**
     * The base unit of amount of substance.
     */
    MOLE,

    /**
     * The base unit of luminous intensity.
     */
    CANDELA;
}

/**
 * Represents a unit.
 *
 * @property chain chain of multipliers consisting of [BaseUnits] unit and its power.
 * @property multiplier a scalar to multiply an element applied to this unit.
 */
public data class Measure internal constructor(
    val chain: Map<BaseUnits, Int> = emptyMap(),
    val multiplier: Double = 1.0,
)

internal fun Measure(vararg chain: Pair<BaseUnits, Int>, multiplier: Double = 1.0): Measure =
    Measure(mapOf(*chain), multiplier)

public data class Measurement<out T>(val measure: Measure, val value: T)

public object MeasureAlgebra : Algebra<Measure> {
    public const val MULTIPLY_OPERATION: String = "*"
    public const val DIVIDE_OPERATION: String = "/"

    public fun multiply(a: Measure, b: Measure): Measure {
        val newChain = mutableMapOf<BaseUnits, Int>()

        a.chain.forEach { (k, v) ->
            val cur = newChain[k]
            if (cur != null) newChain[k] = cur + v
            else newChain[k] = v
        }

        b.chain.forEach { (k, v) ->
            val cur = newChain[k]
            if (cur != null) newChain[k] = cur + v
            else newChain[k] = v
        }

        return Measure(newChain, a.multiplier * b.multiplier)
    }

    public fun divide(a: Measure, b: Measure): Measure =
        multiply(a, b.copy(chain = b.chain.mapValues { (_, v) -> -v }))

    public operator fun Measure.times(b: Measure): Measure = multiply(this, b)
    public operator fun Measure.div(b: Measure): Measure = divide(this, b)

    override fun binaryOperationFunction(operation: String): (left: Measure, right: Measure) -> Measure =
        when (operation) {
            MULTIPLY_OPERATION -> ::multiply
            DIVIDE_OPERATION -> ::divide
            else -> super.binaryOperationFunction(operation)
        }
}

/**
 * A measure for dimensionless quantities with multiplier `1.0`.
 */
public val pure: Measure = Measure()

/**
 * A measure for [BaseUnits.METER] of power `1.0` with multiplier `1.0`.
 */
public val m: Measure = Measure(BaseUnits.METER to 1)

/**
 * A measure for [BaseUnits.KILOGRAM] of power `1.0` with multiplier `1.0`.
 */
public val kg: Measure = Measure(BaseUnits.KILOGRAM to 1)

/**
 * A measure for [BaseUnits.SECOND] of power `1.0` with multiplier `1.0`.
 */
@get:JvmName("s-seconds")
public val s: Measure = Measure(BaseUnits.SECOND to 1)

/**
 * A measure for [BaseUnits.AMPERE] of power `1.0` with multiplier `1.0`.
 */
public val A: Measure = Measure(BaseUnits.AMPERE to 1)

/**
 * A measure for [BaseUnits.KELVIN] of power `1.0` with multiplier `1.0`.
 */
public val K: Measure = Measure(BaseUnits.KELVIN to 1)

/**
 * A measure for [BaseUnits.MOLE] of power `1.0` with multiplier `1.0`.
 */
public val mol: Measure = Measure(BaseUnits.MOLE to 1)

/**
 * A measure for [BaseUnits.CANDELA] of power `1.0` with multiplier `1.0`.
 */
public val cd: Measure = Measure(BaseUnits.CANDELA to 1)

public val rad: Measure = pure
public val sr: Measure = pure
public val degC: Measure = K
public val Hz: Measure = MeasureAlgebra { pure / s }
public val N: Measure = MeasureAlgebra { kg * m * (pure / (s * s)) }
public val J: Measure = MeasureAlgebra { N * m }
public val W: Measure = MeasureAlgebra { J / s }
public val Pa: Measure = MeasureAlgebra { N / (m * m) }
public val lm: Measure = MeasureAlgebra { cd * sr }
public val lx: Measure = MeasureAlgebra { lm / (m * m) }
public val C: Measure = MeasureAlgebra { A * s }
public val V: Measure = MeasureAlgebra { J / C }
public val Ohm: Measure = MeasureAlgebra { V / A }
public val F: Measure = MeasureAlgebra { C / V }
public val Wb: Measure = MeasureAlgebra { kg * m * m * (pure / (s * s)) * (pure / A) }

@get:JvmName("T-tesla")
public val T: Measure = MeasureAlgebra { Wb / (m * m) }

@get:JvmName("H-henry")
public val H: Measure = MeasureAlgebra { kg * m * m * (pure / (s * s)) * (pure / (A * A)) }

public val S: Measure = MeasureAlgebra { pure / Ohm }
public val Bq: Measure = MeasureAlgebra { pure / s }
public val Gy: Measure = MeasureAlgebra { J / kg }
public val Sv: Measure = MeasureAlgebra { J / kg }
public val kat: Measure = MeasureAlgebra { mol / s }

public val g: Measure = Measure(BaseUnits.KILOGRAM to 1, multiplier = 0.001)
public val min: Measure = Measure(BaseUnits.SECOND to 1, multiplier = 60.0)
public val h: Measure = Measure(BaseUnits.SECOND to 1, multiplier = 3600.0)
public val d: Measure = Measure(BaseUnits.SECOND to 1, multiplier = 86_400.0)
public val au: Measure = Measure(BaseUnits.METER to 1, multiplier = 149_597_870_700.0)
public val deg: Measure = Measure(multiplier = PI / 180.0)
public val arcMin: Measure = Measure(multiplier = PI / 10_800.0)
public val arcS: Measure = Measure(multiplier = PI / 648_000.0)
public val ha: Measure = Measure(BaseUnits.METER to 2, multiplier = 10000.0)
public val l: Measure = Measure(BaseUnits.METER to 3, multiplier = 0.001)
public val t: Measure = Measure(BaseUnits.KILOGRAM to 1, multiplier = 1000.0)
public val Da: Measure = Measure(BaseUnits.KILOGRAM to 1, multiplier = 1.660_539_040_202_020 * 10e-27)
public val eV: Measure = J.copy(multiplier = 1.602_176_634 * 10e-19)

public fun Y(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e24)
public fun Z(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e21)
public fun E(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e18)
public fun P(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e15)
public fun T(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e12)
public fun G(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e9)
public fun M(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e6)
public fun k(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e3)
public fun h(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e2)
public fun da(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e1)

public fun y(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-24)
public fun z(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-21)
public fun a(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-18)
public fun f(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-15)
public fun p(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-12)
public fun n(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-9)
public fun u(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-6)
public fun m(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-3)
public fun c(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-2)
public fun d(measure: Measure): Measure = measure.copy(multiplier = measure.multiplier * 1e-1)

public infix fun Measure.pow(power: Int): Measure =
    copy(chain = chain.mapValues { (_, v) -> IntRing.power(v, power.toUInt()) }, multiplier = multiplier.pow(power))

public open class MeasurementAlgebra<T>(public open val algebra: Algebra<T>) : Algebra<Measurement<T>> {
    public operator fun T.times(m: Measure): Measurement<T> = Measurement(m, this)
    public operator fun Measurement<T>.times(m: Measure): Measurement<T> =
        copy(measure = MeasureAlgebra { measure * m })

    public override fun bindSymbol(value: String): Measurement<T> = algebra.bindSymbol(value) * pure
}

public fun <T> Algebra<T>.measurement(): MeasurementAlgebra<T> = MeasurementAlgebra(this)

public open class MeasurementSpace<T, out A>(public override val algebra: A) : MeasurementAlgebra<T>(algebra),
    Group<Measurement<T>>, ScaleOperations<Measurement<T>> where A : Group<T>, A : ScaleOperations<T> {
    public override val zero: Measurement<T>
        get() = Measurement(pure, algebra.zero)

    public override fun add(a: Measurement<T>, b: Measurement<T>): Measurement<T> {
        require(a.measure.chain == b.measure.chain) {
            "The units are incompatible. The chains are ${a.measure.chain} and ${b.measure.chain}"
        }

        return a.copy(value = algebra { a.value * a.measure.multiplier + b.value * b.measure.multiplier })
    }

    public override fun scale(a: Measurement<T>, value: Double): Measurement<T> =
        Measurement(a.measure, algebra { a.value * value })

    override fun Measurement<T>.unaryMinus(): Measurement<T> = copy(value = algebra { -value })
}

public fun <T, A> A.measurement(): MeasurementSpace<T, A> where A : Group<T>, A : ScaleOperations<T> = MeasurementSpace(this)

public open class MeasurementRing<T, out A>(override val algebra: A) : MeasurementSpace<T, A>(algebra),
    Ring<Measurement<T>> where A : ScaleOperations<T>, A : Ring<T> {
    public override val one: Measurement<T>
        get() = Measurement(pure, algebra.one)

    public override fun multiply(a: Measurement<T>, b: Measurement<T>): Measurement<T> =
        Measurement(MeasureAlgebra { a.measure * b.measure }, algebra { a.value * b.value })
}

public fun <T, A> A.measurement(): MeasurementRing<T, A> where A : Ring<T>, A : ScaleOperations<T> = MeasurementRing(this)

public open class MeasurementField<T>(public override val algebra: Field<T>) : MeasurementRing<T, Field<T>>(algebra),
    Field<Measurement<T>> {
    public override fun divide(a: Measurement<T>, b: Measurement<T>): Measurement<T> =
        Measurement(MeasureAlgebra { a.measure / b.measure }, algebra { a.value / b.value })
}

public fun <T> Field<T>.measurement(): MeasurementField<T> = MeasurementField(this)

public open class MeasurementExtendedField<T>(public override val algebra: ExtendedField<T>) :
    MeasurementField<T>(algebra),
    ExtendedField<Measurement<T>> {
    public override fun number(value: Number): Measurement<T> = Measurement(pure, algebra.number(value))
    public override fun sin(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.sin(arg.value))
    public override fun cos(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.cos(arg.value))
    public override fun tan(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.tan(arg.value))
    public override fun asin(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.asin(arg.value))
    public override fun acos(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.acos(arg.value))
    public override fun atan(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.atan(arg.value))

    public override fun power(arg: Measurement<T>, pow: Number): Measurement<T> =
        (this as Field<Measurement<T>>).power(arg, pow.toInt())

    public override fun exp(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.exp(arg.value))
    public override fun ln(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.ln(arg.value))
}

public fun <T> ExtendedField<T>.measurement(): MeasurementExtendedField<T> = MeasurementExtendedField(this)
