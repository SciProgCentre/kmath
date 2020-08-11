package scientifik.kmath.operations

/**
 * Returns the sum of all elements in the iterable in this [Space].
 *
 * @receiver the algebra that provides addition.
 * @param data the iterable to sum up.
 * @return the sum.
 */
fun <T> Space<T>.sum(data: Iterable<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns the sum of all elements in the sequence in this [Space].
 *
 * @receiver the algebra that provides addition.
 * @param data the sequence to sum up.
 * @return the sum.
 */
fun <T> Space<T>.sum(data: Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns an average value of elements in the iterable in this [Space].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the iterable to find average.
 * @return the average value.
 */
fun <T> Space<T>.average(data: Iterable<T>): T = sum(data) / data.count()

/**
 * Returns an average value of elements in the sequence in this [Space].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the sequence to find average.
 * @return the average value.
 */
fun <T> Space<T>.average(data: Sequence<T>): T = sum(data) / data.count()

/**
 * Returns the sum of all elements in the iterable in provided space.
 *
 * @receiver the collection to sum up.
 * @param space the algebra that provides addition.
 * @return the sum.
 */
fun <T> Iterable<T>.sumWith(space: Space<T>): T = space.sum(this)

/**
 * Returns the sum of all elements in the sequence in provided space.
 *
 * @receiver the collection to sum up.
 * @param space the algebra that provides addition.
 * @return the sum.
 */
fun <T> Sequence<T>.sumWith(space: Space<T>): T = space.sum(this)

/**
 * Returns an average value of elements in the iterable in this [Space].
 *
 * @receiver the iterable to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 */
fun <T> Iterable<T>.averageWith(space: Space<T>): T = space.average(this)

/**
 * Returns an average value of elements in the sequence in this [Space].
 *
 * @receiver the sequence to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 */
fun <T> Sequence<T>.averageWith(space: Space<T>): T = space.average(this)

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
