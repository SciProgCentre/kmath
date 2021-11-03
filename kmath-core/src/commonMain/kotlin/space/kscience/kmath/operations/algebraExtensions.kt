/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

/**
 * Returns the sum of all elements in the iterable in this [Ring].
 *
 * @receiver the algebra that provides addition.
 * @param data the iterable to sum up.
 * @return the sum.
 */
public fun <T> Ring<T>.sum(data: Iterable<T>): T = data.fold(zero) { left, right ->
    add(left, right)
}

//TODO replace by sumOf with multi-receivers

/**
 * Returns the sum of all elements in the sequence in this [Ring].
 *
 * @receiver the algebra that provides addition.
 * @param data the sequence to sum up.
 * @return the sum.
 */
public fun <T> Ring<T>.sum(data: Sequence<T>): T = data.fold(zero) { left, right ->
    add(left, right)
}

/**
 * Returns an average value of elements in the iterable in this [Ring].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the iterable to find average.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> S.average(data: Iterable<T>): T where S : Ring<T>, S : ScaleOperations<T> =
    sum(data) / data.count()

/**
 * Returns an average value of elements in the sequence in this [Ring].
 *
 * @receiver the algebra that provides addition and division.
 * @param data the sequence to find average.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> S.average(data: Sequence<T>): T where S : Ring<T>, S : ScaleOperations<T> =
    sum(data) / data.count()

/**
 * Absolute of the comparable [value]
 */
public fun <T : Comparable<T>> Ring<T>.abs(value: T): T = if (value > zero) value else -value

/**
 * Returns the sum of all elements in the iterable in provided space.
 *
 * @receiver the collection to sum up.
 * @param group the algebra that provides addition.
 * @return the sum.
 */
public fun <T> Iterable<T>.sumWith(group: Ring<T>): T = group.sum(this)

/**
 * Returns the sum of all elements in the sequence in provided space.
 *
 * @receiver the collection to sum up.
 * @param group the algebra that provides addition.
 * @return the sum.
 */
public fun <T> Sequence<T>.sumWith(group: Ring<T>): T = group.sum(this)

/**
 * Returns an average value of elements in the iterable in this [Ring].
 *
 * @receiver the iterable to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> Iterable<T>.averageWith(space: S): T where S : Ring<T>, S : ScaleOperations<T> =
    space.average(this)

/**
 * Returns an average value of elements in the sequence in this [Ring].
 *
 * @receiver the sequence to find average.
 * @param space the algebra that provides addition and division.
 * @return the average value.
 * @author Iaroslav Postovalov
 */
public fun <T, S> Sequence<T>.averageWith(space: S): T where S : Ring<T>, S : ScaleOperations<T> =
    space.average(this)

