package scientifik.kmath.misc

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.SpaceOperations
import kotlin.jvm.JvmName

/**
 * A regular function that could be called only inside specific algebra context
 */
interface UFunction<T, C : SpaceOperations<T>>  {
    operator fun C.invoke(arg: T): T
}

/**
 * A suspendable univariate function defined in algebraic context
 */
interface USFunction<T, C : SpaceOperations<T>> {
    suspend operator fun C.invoke(arg: T): T
}

suspend fun USFunction<Double, RealField>.invoke(arg: Double) = RealField.invoke(arg)


interface MFunction<T, C : SpaceOperations<T>> {
    /**
     * The input dimension of the function
     */
    val dimension: UInt

    operator fun C.invoke(vararg args: T): T
}

/**
 * A suspendable multivariate (N->1) function defined on algebraic context
 */
interface MSFunction<T, C : SpaceOperations<T>> {
    /**
     * The input dimension of the function
     */
    val dimension: UInt

    suspend operator fun C.invoke(vararg args: T): T
}

suspend fun MSFunction<Double, RealField>.invoke(args: DoubleArray) = RealField.invoke(*args.toTypedArray())
@JvmName("varargInvoke")
suspend fun MSFunction<Double, RealField>.invoke(vararg args: Double) = RealField.invoke(*args.toTypedArray())

/**
 * A suspendable parametric function with parameter
 */
interface PSFunction<T, P, C : SpaceOperations<T>> {
    suspend operator fun C.invoke(arg: T, parameter: P): T
}
