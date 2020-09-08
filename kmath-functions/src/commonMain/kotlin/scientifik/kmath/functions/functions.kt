package scientifik.kmath.functions

import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.RealField

/**
 * A regular function that could be called only inside specific algebra context
 * @param T source type
 * @param C source algebra constraint
 * @param R result type
 */
public fun interface MathFunction<T, C : Algebra<T>, R> {
    public operator fun C.invoke(arg: T): R
}

public fun <R> MathFunction<Double, RealField, R>.invoke(arg: Double): R = RealField.invoke(arg)

/**
 * A suspendable function defined in algebraic context
 */
// TODO make fun interface, when the new JVM IR is enabled
public interface SuspendableMathFunction<T, C : Algebra<T>, R> {
    public suspend operator fun C.invoke(arg: T): R
}

public suspend fun <R> SuspendableMathFunction<Double, RealField, R>.invoke(arg: Double) = RealField.invoke(arg)

/**
 * A parametric function with parameter
 */
public fun interface ParametricFunction<T, P, C : Algebra<T>> {
    public operator fun C.invoke(arg: T, parameter: P): T
}
