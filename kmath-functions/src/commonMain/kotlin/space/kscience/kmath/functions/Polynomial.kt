package space.kscience.kmath.functions

import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.pow

/**
 * Polynomial coefficients model without fixation on specific context they are applied to.
 *
 * @param coefficients constant is the leftmost coefficient.
 */
public class Polynomial<T : Any>(public val coefficients: List<T>)

/**
 * Returns a [Polynomial] instance with given [coefficients].
 */
@Suppress("FunctionName")
public fun <T : Any> Polynomial(vararg coefficients: T): Polynomial<T> = Polynomial(coefficients.toList())

/**
 * Evaluates the value of the given double polynomial for given double argument.
 */
public fun Polynomial<Double>.value(): Double = coefficients.reduceIndexed { index, acc, d -> acc + d.pow(index) }

/**
 * Evaluates the value of the given polynomial for given argument.
 */
public fun <T : Any, C : Ring<T>> Polynomial<T>.value(ring: C, arg: T): T = ring {
    if (coefficients.isEmpty()) return@ring zero
    var res = coefficients.first()
    var powerArg = arg

    for (index in 1 until coefficients.size) {
        res += coefficients[index] * powerArg
        // recalculating power on each step to avoid power costs on long polynomials
        powerArg *= arg
    }

    res
}

/**
 * Represent the polynomial as a regular context-less function.
 */
public fun <T : Any, C : Ring<T>> Polynomial<T>.asFunction(ring: C): (T) -> T = { value(ring, it) }

/**
 * Space of polynomials.
 *
 * @param T the type of operated polynomials.
 * @param C the intersection of [Ring] of [T] and [ScaleOperations] of [T].
 * @param ring the [C] instance.
 */
public class PolynomialSpace<T : Any, C>(
    private val ring: C,
) : Group<Polynomial<T>>, ScaleOperations<Polynomial<T>> where C : Ring<T>, C : ScaleOperations<T> {
    public override val zero: Polynomial<T> = Polynomial(emptyList())

    override fun Polynomial<T>.unaryMinus(): Polynomial<T> = ring {
        Polynomial(coefficients.map { -it })
    }

    public override fun add(a: Polynomial<T>, b: Polynomial<T>): Polynomial<T> {
        val dim = max(a.coefficients.size, b.coefficients.size)

        return ring {
            Polynomial(List(dim) { index ->
                a.coefficients.getOrElse(index) { zero } + b.coefficients.getOrElse(index) { zero }
            })
        }
    }

    public override fun scale(a: Polynomial<T>, value: Double): Polynomial<T> =
        ring { Polynomial(List(a.coefficients.size) { index -> a.coefficients[index] * value }) }

    /**
     * Evaluates the polynomial for the given value [arg].
     */
    public operator fun Polynomial<T>.invoke(arg: T): T = value(ring, arg)
}

public inline fun <T : Any, C, R> C.polynomial(block: PolynomialSpace<T, C>.() -> R): R where C : Ring<T>, C : ScaleOperations<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}
