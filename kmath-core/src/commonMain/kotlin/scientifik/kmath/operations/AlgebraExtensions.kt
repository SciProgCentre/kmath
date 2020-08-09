package scientifik.kmath.operations

/**
 * Returns the sum of all elements in the iterable in this [Space].
 *
 * @receiver the algebra that provides addition.
 * @param data the collection to sum up.
 * @return the sum.
 */
fun <T> Space<T>.sum(data: Iterable<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns the sum of all elements in the sequence in this [Space].
 *
 * @receiver the algebra that provides addition.
 * @param data the collection to sum up.
 * @return the sum.
 */
fun <T> Space<T>.sum(data: Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns the sum of all elements in the iterable in provided space.
 *
 * @receiver the collection to sum up.
 * @param space the algebra that provides addition.
 * @return the sum.
 */
fun <T : Any, S : Space<T>> Iterable<T>.sumWith(space: S): T = space.sum(this)

//TODO optimized power operation

/**
 * Raises [arg] to the natural power [power].
 *
 * @receiver the algebra to provide multiplication.
 * @param arg the base.
 * @param power the exponent.
 * @return the base raised to the power.
 */
fun <T> Ring<T>.power(arg: T, power: Int): T {
    require(power >= 0) { "The power can't be negative." }
    require(power != 0 || arg != zero) { "The $zero raised to $power is not defined." }
    if (power == 0) return one
    var res = arg
    repeat(power - 1) { res *= arg }
    return res
}

/**
 * Raises [arg] to the integer power [power].
 *
 * @receiver the algebra to provide multiplication and division.
 * @param arg the base.
 * @param power the exponent.
 * @return the base raised to the power.
 */
fun <T> Field<T>.power(arg: T, power: Int): T {
    require(power != 0 || arg != zero) { "The $zero raised to $power is not defined." }
    if (power == 0) return one
    if (power < 0) return one / (this as Ring<T>).power(arg, -power)
    return (this as Ring<T>).power(arg, power)
}
