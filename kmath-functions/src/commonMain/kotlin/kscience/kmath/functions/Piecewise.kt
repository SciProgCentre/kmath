package kscience.kmath.functions

import kscience.kmath.operations.Ring

public fun interface Piecewise<T, R> {
    public fun findPiece(arg: T): R?
}

public fun interface PiecewisePolynomial<T : Any> :
    Piecewise<T, Polynomial<T>>

/**
 * Ordered list of pieces in piecewise function
 */
public class OrderedPiecewisePolynomial<T : Comparable<T>>(delimiter: T) :
    PiecewisePolynomial<T> {
    private val delimiters: MutableList<T> = arrayListOf(delimiter)
    private val pieces: MutableList<Polynomial<T>> = arrayListOf()

    /**
     * Dynamically add a piece to the "right" side (beyond maximum argument value of previous piece)
     * @param right new rightmost position. If is less then current rightmost position, a error is thrown.
     */
    public fun putRight(right: T, piece: Polynomial<T>) {
        require(right > delimiters.last()) { "New delimiter should be to the right of old one" }
        delimiters.add(right)
        pieces.add(piece)
    }

    public fun putLeft(left: T, piece: Polynomial<T>) {
        require(left < delimiters.first()) { "New delimiter should be to the left of old one" }
        delimiters.add(0, left)
        pieces.add(0, piece)
    }

    override fun findPiece(arg: T): Polynomial<T>? {
        if (arg < delimiters.first() || arg >= delimiters.last())
            return null
        else {
            for (index in 1 until delimiters.size)
                if (arg < delimiters[index])
                    return pieces[index - 1]

            error("Piece not found")
        }
    }
}

/**
 * Return a value of polynomial function with given [ring] an given [arg] or null if argument is outside of piecewise definition.
 */
public fun <T : Comparable<T>, C : Ring<T>> PiecewisePolynomial<T>.value(ring: C, arg: T): T? =
    findPiece(arg)?.value(ring, arg)

public fun <T : Comparable<T>, C : Ring<T>> PiecewisePolynomial<T>.asFunction(ring: C): (T) -> T? = { value(ring, it) }