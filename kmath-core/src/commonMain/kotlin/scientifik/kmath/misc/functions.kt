package scientifik.kmath.coroutines

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.SpaceOperations
import kotlin.jvm.JvmName

/**
 * A suspendable univariate function defined in algebraic context
 */
interface UFunction<T, C : SpaceOperations<T>> {
    suspend operator fun C.invoke(arg: T): T
}

suspend fun UFunction<Double, RealField>.invoke(arg: Double) = RealField.invoke(arg)

/**
 * A suspendable multivariate (N->1) function defined on algebraic context
 */
interface MFunction<T, C : SpaceOperations<T>> {
    /**
     * The input dimension of the function
     */
    val dimension: UInt

    suspend operator fun C.invoke(vararg args: T): T
}

suspend fun MFunction<Double, RealField>.invoke(args: DoubleArray) = RealField.invoke(*args.toTypedArray())
@JvmName("varargInvoke")
suspend fun MFunction<Double, RealField>.invoke(vararg args: Double) = RealField.invoke(*args.toTypedArray())

/**
 * A suspendable univariate function with parameter
 */
interface ParametricUFunction<T, P, C : SpaceOperations<T>> {
    suspend operator fun C.invoke(arg: T, parameter: P): T
}
