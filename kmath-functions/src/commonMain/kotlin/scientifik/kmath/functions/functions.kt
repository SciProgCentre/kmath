package scientifik.kmath.functions

import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.RealField

/**
 * A regular function that could be called only inside specific algebra context
 * @param T source type
 * @param C source algebra constraint
 * @param R result type
 */
interface MathFunction<T, C : Algebra<T>, R> {
    operator fun C.invoke(arg: T): R
}

fun <R> MathFunction<Double, RealField, R>.invoke(arg: Double): R = RealField.invoke(arg)

/**
 * A suspendable function defined in algebraic context
 */
interface SuspendableMathFunction<T, C : Algebra<T>, R> {
    suspend operator fun C.invoke(arg: T): R
}

suspend fun <R> SuspendableMathFunction<Double, RealField, R>.invoke(arg: Double) = RealField.invoke(arg)


/**
 * A parametric function with parameter
 */
interface ParametricFunction<T, P, C : Algebra<T>> {
    operator fun C.invoke(arg: T, parameter: P): T
}
