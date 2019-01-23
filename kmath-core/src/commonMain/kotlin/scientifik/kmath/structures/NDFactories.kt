package scientifik.kmath.structures

import scientifik.kmath.operations.RealField.power
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.min
import kotlin.math.sign

/**
 * Numpy-like factories for [RealNDElement]
 */
object RealNDFactory {
    /**
     *  Get a [RealNDElement] filled with [RealNDField.one]. Due to caching all instances with the same shape point to the same object
     */
    fun ones(vararg shape: Int) = NDField.real(shape).one

    /**
     * Create a 2D NDArray, with ones on the diagonal and zeros elsewhere.
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun eye(dim1: Int, dim2: Int, offset: Int = 0) =
        NDElement.real2D(dim1, dim2) { i, j -> if (i == j + offset) 1.0 else 0.0 }

    /**
     * An array with ones at and below the given diagonal and zeros elsewhere.
     * T[i,j] == 1 for i <= j + offset
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun triangle(dim1: Int, dim2: Int, offset: Int = 0) =
        NDElement.real2D(dim1, dim2) { i, j -> if (i <= j + offset) 1.0 else 0.0 }

    /**
     * Return evenly spaced values within a given interval.
     *
     * Values are generated within the half-open interval [start, stop) (in other words, the interval including start but excluding stop).
     */
    fun range(range: ClosedFloatingPointRange<Double>, step: Double = 1.0) =
        NDElement.real1D(ceil((range.endInclusive - range.start) / step).toInt()) { i ->
            range.start + i * step
        }

    /**
     *  Return evenly spaced numbers over a specified interval.
     *  @param range start is starting value, final value depend from endPoint parameter
     *  @param endPoint If True, right boundary of range is the last sample. Otherwise, it is not included.
     */
    fun linspace(
        range: ClosedFloatingPointRange<Double>,
        num: Int = 100,
        endPoint: Boolean = true
    ): RealNDElement {
        val div = if (endPoint) (num - 1) else num
        val delta = range.start - range.endInclusive
        return if (num > 1) {
            val step = delta / div
            if (step == 0.0) {
                error("Bad ranges: step = $step")
            }
            NDElement.real1D(num) {
                if (endPoint and (it == num - 1)) {
                    range.endInclusive
                }
                range.start + it * step
            }
        } else {
            NDElement.real1D(1) { range.start }
        }

    }

    /**
     * Return numbers spaced evenly on a log scale.
     * @param range use it like:
     *          (start..stop) to number
     *          power(base,start) is starting value, endvalue depend from endPoint parameter
     * @param endPoint If True, power(base,stop) is the last sample. Otherwise, it is not included.
     * @param base - The base of the log space.
     */
    fun logspace(
        range: ClosedFloatingPointRange<Double>,
        num: Int = 100,
        endPoint: Boolean = true,
        base: Double = 10.0
    ) = linspace(range, num, endPoint).map { power(base, it) }

    /**
     *  Return numbers spaced evenly on a log scale (a geometric progression).
     *
     *  This is similar to [logspace], but with endpoints specified directly. Each output sample is a constant multiple of the previous.
     *  @param range use it like:
     *           (start..stop) to number
     *          start is starting value, finaly value depend from endPoint parameter
     *  @param endPoint If True, right boundary of range is the last sample. Otherwise, it is not included.
     */
    fun geomspace(range: ClosedFloatingPointRange<Double>, num : Int = 100, endPoint: Boolean = true): RealNDElement {
        var start = range.start
        var stop = range.endInclusive
        if (start == 0.0 || stop == 0.0) {
            error("Geometric sequence cannot include zero")
        }
        var outSign = 1.0
        if (sign(start) == -1.0 && sign(stop) == -1.0) {
            start = -start
            stop = -stop
            outSign = -outSign
        }

        return logspace(log(start, 10.0)..log(stop, 10.0), num, endPoint = endPoint).map {
            outSign * it
        }
    }

    /**
     * Return specified diagonals of 2D NDArray.
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun extractDiagonal(array: RealNDElement, offset: Int = 0): RealNDElement {
        if (array.dimension != 2) {
            error("Input must be 2D NDArray")
        }
        val size = min(array.shape[0], array.shape[0])
        return if (offset >= 0) {
            NDElement.real1D(size) { i -> array[i, i + offset] }
        } else {
            NDElement.real1D(size) { i -> array[i - offset, i] }
        }
    }

    /**
     *   Return a 2-D array with [array] on the [offset] diagonal.
     *
     *   @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun fromDiagonal(array: RealNDElement, offset: Int = 0): RealNDElement {
        if (array.dimension != 1) {
            error("Input must be 1D NDArray")
        }
        val size = array.shape[0]
        return if (offset < 0) {
            NDElement.real2D(size - offset, size) { i, j ->
                if (i - offset == j) array[j] else 0.0
            }
        } else {
            NDElement.real2D(size, size + offset) { i, j ->
                if (i == j + offset) array[i] else 0.0
            }
        }
    }

    /**
     * Generate a [Vandermonde matrix](https://en.wikipedia.org/wiki/Vandermonde_matrix).
     *
     *  @param nCols --- number of columns, as default using length of [array]
     *  @param increasing --- Order of the powers of the columns. If True, the powers increase from left to right, if False (the default) they are reversed. FIXME: Default order like numpy
     */
    fun vandermonde(array: RealNDElement, nCols: Int = 0, increasing: Boolean = false): RealNDElement {
        if (array.dimension != 1) {
            error("Input must be 1D NDArray")
        }
        val size = if (nCols == 0) array.shape[0] else nCols
        return if (increasing) {
            NDElement.real2D(array.shape[0], size) { i, j ->
                power(array[i], j)
            }
        } else {
            NDElement.real2D(array.shape[0], size) { i, j ->
                power(array[i], size - j - 1)
            }
        }

    }

}


