package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min


public data class RationalFunction<C> internal constructor (
    public override val numerator: Polynomial<C>,
    public override val denominator: Polynomial<C>
) : AbstractRationalFunction<C, Polynomial<C>> {
    override fun toString(): String = "RationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// region Internal utilities

/**
 * Represents internal [RationalFunction] errors.
 */
internal class RationalFunctionError : Error {
    constructor(): super()
    constructor(message: String): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}

/**
 * Throws an [RationalFunction] with the given [message].
 */
internal fun rationalFunctionError(message: Any): Nothing = throw RationalFunctionError(message.toString())

// endregion

// region Constructors and converters
// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>, denominator: Polynomial<C>): RationalFunction<C> =
//    if (denominator.isZero()) throw ArithmeticException("/ by zero")
//    else RationalFunction<C>(numerator, denominator)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction<C>(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
//        Polynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } ).also { if (it.isZero()) }
//    )
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>): RationalFunction<C> =
//    RationalFunction(numerator, onePolynomial)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } )
//    )

// endregion

public class RationalFunctionSpace<C, A : Ring<C>> (
    public val ring: A,
) : AbstractRationalFunctionalSpaceOverPolynomialSpace<C, Polynomial<C>, RationalFunction<C>, A> {

    override val polynomialRing : PolynomialSpace<C, A> = PolynomialSpace(ring)

    // region Rational-integer relation
    /**
     * Returns sum of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to adding [other] copies of unit polynomial to [this].
     */
    public override operator fun RationalFunction<C>.plus(other: Int): RationalFunction<C> =
        RationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to subtraction [other] copies of unit polynomial from [this].
     */
    public override operator fun RationalFunction<C>.minus(other: Int): RationalFunction<C> =
        RationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the rational function and the integer represented as rational function.
     *
     * The operation is equivalent to sum of [other] copies of [this].
     */
    public override operator fun RationalFunction<C>.times(other: Int): RationalFunction<C> =
        RationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Integer-Rational relation
    /**
     * Returns sum of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to adding [this] copies of unit polynomial to [other].
     */
    public override operator fun Int.plus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns difference between the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to subtraction [this] copies of unit polynomial from [other].
     */
    public override operator fun Int.minus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns product of the integer represented as rational function and the rational function.
     *
     * The operation is equivalent to sum of [this] copies of [other].
     */
    public override operator fun Int.times(other: RationalFunction<C>): RationalFunction<C> = TODO()
    // endregion

    // region Constant-rational relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun C.plus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns difference between the constant represented as polynomial and the rational function.
     */
    public override operator fun C.minus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns product of the constant represented as polynomial and the rational function.
     */
    public override operator fun C.times(other: RationalFunction<C>): RationalFunction<C> = TODO()
    // endregion

    // region Rational-constant relation
    /**
     * Returns sum of the constant represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.plus(other: C): RationalFunction<C> =
        RationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the constant represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.minus(other: C): RationalFunction<C> =
        RationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the constant represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.times(other: C): RationalFunction<C> =
        RationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Polynomial-rational relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun Polynomial<C>.plus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns difference between the polynomial represented as polynomial and the rational function.
     */
    public override operator fun Polynomial<C>.minus(other: RationalFunction<C>): RationalFunction<C> = TODO()
    /**
     * Returns product of the polynomial represented as polynomial and the rational function.
     */
    public override operator fun Polynomial<C>.times(other: RationalFunction<C>): RationalFunction<C> = TODO()
    // endregion

    // region Rational-polynomial relation
    /**
     * Returns sum of the polynomial represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.plus(other: Polynomial<C>): RationalFunction<C> =
        RationalFunction(
            numerator + denominator * other,
            denominator
        )
    /**
     * Returns difference between the polynomial represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.minus(other: Polynomial<C>): RationalFunction<C> =
        RationalFunction(
            numerator - denominator * other,
            denominator
        )
    /**
     * Returns product of the polynomial represented as rational function and the rational function.
     */
    public override operator fun RationalFunction<C>.times(other: Polynomial<C>): RationalFunction<C> =
        RationalFunction(
            numerator * other,
            denominator
        )
    // endregion

    // region Rational-rational relation
    /**
     * Returns negation of the rational function.
     */
    public override operator fun RationalFunction<C>.unaryMinus(): RationalFunction<C> = RationalFunction(-numerator, denominator)
    /**
     * Returns sum of the rational functions.
     */
    public override operator fun RationalFunction<C>.plus(other: RationalFunction<C>): RationalFunction<C> =
        RationalFunction(
            numerator * other.denominator + denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns difference of the rational functions.
     */
    public override operator fun RationalFunction<C>.minus(other: RationalFunction<C>): RationalFunction<C> =
        RationalFunction(
            numerator * other.denominator - denominator * other.numerator,
            denominator * other.denominator
        )
    /**
     * Returns product of the rational functions.
     */
    public override operator fun RationalFunction<C>.times(other: RationalFunction<C>): RationalFunction<C> =
        RationalFunction(
            numerator * other.numerator,
            denominator * other.denominator
        )

    /**
     * Check if the instant is zero rational function.
     */
    public override fun RationalFunction<C>.isZero(): Boolean = numerator.isZero()
    /**
     * Check if the instant is unit rational function.
     */
    public override fun RationalFunction<C>.isOne(): Boolean = numerator.equalsTo(denominator)
    /**
     * Check if the instant is minus unit rational function.
     */
    public override fun RationalFunction<C>.isMinusOne(): Boolean = (numerator + denominator).isZero()

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: RationalFunction<C> = RationalFunction(polynomialZero, polynomialOne)
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: RationalFunction<C> = RationalFunction(polynomialOne, polynomialOne)

    /**
     * Checks equality of the rational functions.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER", "CovariantEquals")
    public override infix fun RationalFunction<C>.equalsTo(other: RationalFunction<C>): Boolean =
        when {
            this === other -> true
            numeratorDegree - denominatorDegree != with(other) { numeratorDegree - denominatorDegree } -> false
            else -> numerator * other.denominator equalsTo other.numerator * denominator
        }
    // endregion

    // region REST TODO: Разобрать

    public operator fun RationalFunction<C>.div(other: RationalFunction<C>): RationalFunction<C> =
        RationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )

    public operator fun RationalFunction<C>.div(other: Polynomial<C>): RationalFunction<C> =
        RationalFunction(
            numerator,
            denominator * other
        )

    public operator fun RationalFunction<C>.div(other: C): RationalFunction<C> =
        RationalFunction(
            numerator,
            denominator * other
        )

    public operator fun RationalFunction<C>.div(other: Int): RationalFunction<C> =
        RationalFunction(
            numerator,
            denominator * other
        )

//    operator fun invoke(arg: UnivariatePolynomial<T>): RationalFunction<T> =
//        RationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    operator fun invoke(arg: RationalFunction<T>): RationalFunction<T> {
//        val num = numerator invokeRFTakeNumerator arg
//        val den = denominator invokeRFTakeNumerator arg
//        val degreeDif = numeratorDegree - denominatorDegree
//        return if (degreeDif > 0)
//            RationalFunction(
//                num,
//                multiplyByPower(den, arg.denominator, degreeDif)
//            )
//        else
//            RationalFunction(
//                multiplyByPower(num, arg.denominator, -degreeDif),
//                den
//            )
//    }
//
//    override fun toString(): String = toString(UnivariatePolynomial.variableName)
//
//    fun toString(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(withVariableName)
//            else -> "${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)}"
//        }
//
//    fun toStringWithBrackets(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(withVariableName)
//            else -> "(${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)})"
//        }
//
//    fun toReversedString(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(withVariableName)
//            else -> "${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)}"
//        }
//
//    fun toReversedStringWithBrackets(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(withVariableName)
//            else -> "(${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)})"
//        }
//
//    fun removeZeros() =
//        RationalFunction(
//            numerator.removeZeros(),
//            denominator.removeZeros()
//        )
    // endregion
}