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
 * Polynomial coefficients without fixation on specific context they are applied to
 * @param coefficients constant is the leftmost coefficient
 */
public inline class Polynomial<T : Any>(public val coefficients: List<T>)

@Suppress("FunctionName")
public fun <T : Any> Polynomial(vararg coefficients: T): Polynomial<T> = Polynomial(coefficients.toList())

public fun Polynomial<Double>.value(): Double = coefficients.reduceIndexed { index, acc, d -> acc + d.pow(index) }

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
 * Represent the polynomial as a regular context-less function
 */
public fun <T : Any, C : Ring<T>> Polynomial<T>.asFunction(ring: C): (T) -> T = { value(ring, it) }

/**
 * An algebra for polynomials
 */
public class PolynomialSpace<T : Any, C>(
    private val ring: C,
) : Group<Polynomial<T>>, ScaleOperations<Polynomial<T>> where C : Ring<T>, C : ScaleOperations<T> {
    public override val zero: Polynomial<T> = Polynomial(emptyList())

    override fun Polynomial<T>.unaryMinus(): Polynomial<T> = with(ring) {
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

    public operator fun Polynomial<T>.invoke(arg: T): T = value(ring, arg)
}

public inline fun <T : Any, C, R> C.polynomial(block: PolynomialSpace<T, C>.() -> R): R where C : Ring<T>, C : ScaleOperations<T> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return PolynomialSpace(this).block()
}
