package space.kscience.kmath.operations

/**
 * Returns the sum of all elements in the iterable in this [Group].
 *
 * @receiver the algebra that provides addition.
 * @param data the iterable to sum up.
 * @return the sum.
 */
public fun <T> Group<T>.sum(data: Iterable<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns the sum of all elements in the sequence in this [Group].
 *
 * @receiver the algebra that provides addition.
 * @param data the sequence to sum up.
 * @return the sum.
 */
public fun <T> Group<T>.sum(data: Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }

/**
 * Returns an average value of elements in the iterable in this [Group].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the iterable to find average.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> S.average(data: Iterable<T>): T where S : Group<T>, S : ScaleOperations<T> =
    sum(data) / data.count()

/**
 * Returns an average value of elements in the sequence in this [Group].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the sequence to find average.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> S.average(data: Sequence<T>): T where S : Group<T>, S : ScaleOperations<T> =
    sum(data) / data.count()

/**
 * Absolute of the comparable [value]
 */
public fun <T : Comparable<T>> Group<T>.abs(value: T): T = if (value > zero) value else -value

/**
 * Returns the sum of all elements in the iterable in provided space.
 *
 * @receiver the collection to sum up.
 * @param group the algebra that provides addition.
 * @return the sum.
 */
public fun <T> Iterable<T>.sumWith(group: Group<T>): T = group.sum(this)

/**
 * Returns the sum of all elements in the sequence in provided space.
 *
 * @receiver the collection to sum up.
 * @param group the algebra that provides addition.
 * @return the sum.
 */
public fun <T> Sequence<T>.sumWith(group: Group<T>): T = group.sum(this)

/**
 * Returns an average value of elements in the iterable in this [Group].
 *
 * @receiver the iterable to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> Iterable<T>.averageWith(space: S): T where S : Group<T>, S : ScaleOperations<T> =
    space.average(this)

/**
 * Returns an average value of elements in the sequence in this [Group].
 *
 * @receiver the sequence to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> Sequence<T>.averageWith(space: S): T where S : Group<T>, S : ScaleOperations<T> =
    space.average(this)

//TODO optimized power operation

/**
 * Raises [arg] to the natural power [power].
 *
 * @receiver the algebra to provide multiplication.
 * @param arg the base.
 * @param power the exponent.
 * @return the base raised to the power.
 */
public fun <T> Ring<T>.power(arg: T, power: Int): T {
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
 * @author Iaroslav Postovalov
 */
public fun <T> Field<T>.power(arg: T, power: Int): T {
    require(power != 0 || arg != zero) { "The $zero raised to $power is not defined." }
    if (power == 0) return one
    if (power < 0) return one / (this as Ring<T>).power(arg, -power)
    return (this as Ring<T>).power(arg, power)
}
