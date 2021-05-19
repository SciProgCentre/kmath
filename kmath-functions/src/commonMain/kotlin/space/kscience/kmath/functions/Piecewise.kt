/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring

/**
 * Represents piecewise-defined function.
 *
 * @param T the piece key type.
 * @param R the sub-function type.
 */
public fun interface Piecewise<T, R> {
    /**
     * Returns the appropriate sub-function for given piece key.
     */
    public fun findPiece(arg: T): R?
}

/**
 * Represents piecewise-defined function where all the sub-functions are polynomials.
 * @param pieces An ordered list of range-polynomial pairs. The list does not in general guarantee that there are no "holes" in it.
 */
public class PiecewisePolynomial<T : Comparable<T>>(
    public val pieces: List<Pair<ClosedRange<T>, Polynomial<T>>>,
) : Piecewise<T, Polynomial<T>> {

    public override fun findPiece(arg: T): Polynomial<T>? {
        return if (arg < pieces.first().first.start || arg >= pieces.last().first.endInclusive)
            null
        else {
            pieces.firstOrNull { arg in it.first }?.second
        }
    }
}

/**
 * A [Piecewise]  builder where all the pieces are ordered by the [Comparable] type instances.
 *
 * @param T the comparable piece key type.
 * @param delimiter the initial piecewise separator
 */
public class PiecewiseBuilder<T : Comparable<T>>(delimiter: T) {
    private val delimiters: MutableList<T> = arrayListOf(delimiter)
    private val pieces: MutableList<Polynomial<T>> = arrayListOf()

    /**
     * Dynamically adds a piece to the right side (beyond maximum argument value of previous piece)
     *
     * @param right new rightmost position. If is less then current rightmost position, an error is thrown.
     * @param piece the sub-function.
     */
    public fun putRight(right: T, piece: Polynomial<T>) {
        require(right > delimiters.last()) { "New delimiter should be to the right of old one" }
        delimiters += right
        pieces += piece
    }

    /**
     * Dynamically adds a piece to the left side (beyond maximum argument value of previous piece)
     *
     * @param left the new leftmost position. If is less then current rightmost position, an error is thrown.
     * @param piece the sub-function.
     */
    public fun putLeft(left: T, piece: Polynomial<T>) {
        require(left < delimiters.first()) { "New delimiter should be to the left of old one" }
        delimiters.add(0, left)
        pieces.add(0, piece)
    }

    public fun build(): PiecewisePolynomial<T> {
        return PiecewisePolynomial(delimiters.zipWithNext { l, r -> l..r }.zip(pieces))
    }
}

/**
 * A builder for [PiecewisePolynomial]
 */
public fun <T : Comparable<T>> PiecewisePolynomial(
    startingPoint: T,
    builder: PiecewiseBuilder<T>.() -> Unit,
): PiecewisePolynomial<T> = PiecewiseBuilder(startingPoint).apply(builder).build()

/**
 * Return a value of polynomial function with given [ring] an given [arg] or null if argument is outside of piecewise
 * definition.
 */
public fun <T : Comparable<T>, C : Ring<T>> PiecewisePolynomial<T>.value(ring: C, arg: T): T? =
    findPiece(arg)?.value(ring, arg)

/**
 * Convert this polynomial to a function returning nullable value (null if argument is outside piecewise range).
 */
public fun <T : Comparable<T>, C : Ring<T>> PiecewisePolynomial<T>.asFunction(ring: C): (T) -> T? = { value(ring, it) }

/**
 * Convert this polynomial to a function using [defaultValue] for arguments outside the piecewise range.
 */
public fun <T : Comparable<T>, C : Ring<T>> PiecewisePolynomial<T>.asFunction(ring: C, defaultValue: T): (T) -> T =
    { value(ring, it) ?: defaultValue }
