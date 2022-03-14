package math.polynomials

import math.ringsAndFields.*
import space.kscience.kmath.functions.LabeledRationalFunction


fun <T: Ring<T>> T.toLabeledRationalFunction() = LabeledRationalFunction(this.toLabeledPolynomial())

// region Operator extensions

// region Field case

fun <T: Field<T>> LabeledRationalFunction<T>.reduced(): LabeledRationalFunction<T> {
    val greatestCommonDivider = polynomialGCD(numerator, denominator)
    return LabeledRationalFunction(
        numerator / greatestCommonDivider,
        denominator / greatestCommonDivider
    )
}

// endregion

// region Constants

operator fun <T: Ring<T>> T.plus(other: LabeledRationalFunction<T>) = other + this
operator fun <T: Ring<T>> Integer.plus(other: LabeledRationalFunction<T>) = other + this
operator fun <T: Ring<T>> Int.plus(other: LabeledRationalFunction<T>) = other + this
operator fun <T: Ring<T>> Long.plus(other: LabeledRationalFunction<T>) = other + this

operator fun <T: Ring<T>> T.minus(other: LabeledRationalFunction<T>) = -other + this
operator fun <T: Ring<T>> Integer.minus(other: LabeledRationalFunction<T>) = -other + this
operator fun <T: Ring<T>> Int.minus(other: LabeledRationalFunction<T>) = -other + this
operator fun <T: Ring<T>> Long.minus(other: LabeledRationalFunction<T>) = -other + this

operator fun <T: Ring<T>> T.times(other: LabeledRationalFunction<T>) = other * this
operator fun <T: Ring<T>> Integer.times(other: LabeledRationalFunction<T>) = other * this
operator fun <T: Ring<T>> Int.times(other: LabeledRationalFunction<T>) = other * this
operator fun <T: Ring<T>> Long.times(other: LabeledRationalFunction<T>) = other * this

// endregion

// region Polynomials

operator fun <T: Ring<T>> LabeledRationalFunction<T>.plus(other: UnivariatePolynomial<T>) =
    LabeledRationalFunction(
        numerator + denominator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.plus(other: UnivariateRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial() + denominator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.plus(other: Polynomial<T>) =
    LabeledRationalFunction(
        numerator + denominator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.plus(other: NumberedRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial() + denominator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )

operator fun <T: Ring<T>> LabeledRationalFunction<T>.minus(other: UnivariatePolynomial<T>) =
    LabeledRationalFunction(
        numerator - denominator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.minus(other: UnivariateRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial() - denominator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.minus(other: Polynomial<T>) =
    LabeledRationalFunction(
        numerator - denominator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.minus(other: NumberedRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial() - denominator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )

operator fun <T: Ring<T>> LabeledRationalFunction<T>.times(other: UnivariatePolynomial<T>) =
    LabeledRationalFunction(
        numerator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.times(other: UnivariateRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.times(other: Polynomial<T>) =
    LabeledRationalFunction(
        numerator * other.toLabeledPolynomial(),
        denominator
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.times(other: NumberedRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.numerator.toLabeledPolynomial(),
        denominator * other.denominator.toLabeledPolynomial()
    )

operator fun <T: Ring<T>> LabeledRationalFunction<T>.div(other: UnivariatePolynomial<T>) =
    LabeledRationalFunction(
        numerator,
        denominator * other.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.div(other: UnivariateRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial(),
        denominator * other.numerator.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.div(other: Polynomial<T>) =
    LabeledRationalFunction(
        numerator,
        denominator * other.toLabeledPolynomial()
    )
operator fun <T: Ring<T>> LabeledRationalFunction<T>.div(other: NumberedRationalFunction<T>) =
    LabeledRationalFunction(
        numerator * other.denominator.toLabeledPolynomial(),
        denominator * other.numerator.toLabeledPolynomial()
    )

// endregion

// endregion