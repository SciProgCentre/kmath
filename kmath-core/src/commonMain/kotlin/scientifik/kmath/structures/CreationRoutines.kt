package scientifik.kmath.structures

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.RealField.power
import kotlin.math.*


object RealFactories{
    /**
     *  Create a NDArray filled with ones
     */
    fun ones(vararg shape: Int) = NDElement.real(shape){1.0}

    /**
     * Create a 2D NDArray, with ones on the diagonal and zeros elsewhere.
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun eye(dim1: Int, dim2: Int, offset : Int = 0) = NDElement.real2D(dim1, dim2){i, j -> if (i == j + offset) 1.0 else 0.0}

    /**
     * An array with ones at and below the given diagonal and zeros elsewhere.
     * T[i,j] == 1 for i <= j + offset
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun triangle(dim1: Int, dim2: Int, offset : Int = 0) = NDElement.real2D(dim1, dim2){i, j -> if (i <= j + offset) 1.0 else 0.0}

    /**
     * Return evenly spaced values within a given interval.
     *
     * Values are generated within the half-open interval [start, stop) (in other words, the interval including start but excluding stop).
     *  @param range use it like:
     *           (start..stop) to step
     */
    fun range(range : Pair<ClosedFloatingPointRange<Double>,Double>) = NDElement.real1D(ceil((range.first.endInclusive - range.first.start)/range.second).toInt()){i-> range.first.start + i*range.second}

    /**
     *  Return evenly spaced numbers over a specified interval.
     *  @param range use it like:
     *           (start..stop) to number
     *          start is starting value, finaly value depend from endPoint parameter
     *  @param endPoint If True, right boundary of range is the last sample. Otherwise, it is not included.
     */
    fun linspace(range : Pair<ClosedFloatingPointRange<Double>,Int>, endPoint: Boolean = true): Pair<RealNDElement, Double> {
        val div = if (endPoint) (range.second - 1) else range.second
        val delta = range.first.start - range.first.endInclusive
        if (range.second > 1){
            val step = delta/div
            if (step == 0.0){ error("Bad ranges: step = $step")}
            val result = NDElement.real1D(range.second){
                if ( endPoint and (it == range.second - 1) ){ range.first.endInclusive}
                range.first.start + it*step
            }
            return  result to step
        }
        else{
            val step = Double.NaN
            return NDElement.real1D(1){range.first.start} to step
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
    fun logspace(range : Pair<ClosedFloatingPointRange<Double>,Int>, endPoint: Boolean = true, base : Double = 10.0)  : RealNDElement {
        val lin = linspace(range, endPoint).first
        val fun_ = {x: Double -> power(base, x)}
        return fun_(lin) // FIXME: RealNDElement.map return not suitable type ( `linspace(range, endPoint).first.map{power(base, it}`)
    }
    /**
     *  Return numbers spaced evenly on a log scale (a geometric progression).
     *
     *  This is similar to [logspace], but with endpoints specified directly. Each output sample is a constant multiple of the previous.
     *  @param range use it like:
     *           (start..stop) to number
     *          start is starting value, finaly value depend from endPoint parameter
     *  @param endPoint If True, right boundary of range is the last sample. Otherwise, it is not included.
     */
    fun geomspace(range : Pair<ClosedFloatingPointRange<Double>,Int>, endPoint: Boolean = true) : RealNDElement{
        var start = range.first.start
        var stop = range.first.endInclusive
        val num = range.second
        if ( start == 0.0 || stop == 0.0){
            error("Geometric sequence cannot include zero")
        }
        var outSign = 1.0
        if (sign(start) == -1.0 && sign(stop) == -1.0){
            start = -start
            stop = -stop
            outSign = -outSign
        }

        val log_ = logspace((log(start, 10.0)..log(stop, 10.0) to num), endPoint=endPoint)
        val fun_ = {x:Double -> outSign*x}
        return fun_(log_) // FIXME: `outSign*log_`  --- don't define times operator

    }

    /**
     * Return specified diagonals of 2D NDArray.
     *
     * @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun extractDiagonal(array : RealNDElement, offset: Int = 0): RealNDElement{
        if (array.dimension != 2){
            error("Input must be 2D NDArray")}
        val size = min(array.shape[0], array.shape[0])
        if (offset>=0){
            return NDElement.real1D(size){i -> array[i, i+offset]}
        }
        else{
            return NDElement.real1D(size){i -> array[i-offset, i]}
        }

    }

    /**
     *   Return a 2-D array with [array] on the [offset] diagonal.
     *
     *   @param offset Index of the diagonal: 0 (the default) refers to the main diagonal, a positive value refers to an upper diagonal, and a negative value to a lower diagonal.
     */
    fun fromDiagonal(array : RealNDElement, offset: Int = 0): RealNDElement{
        if (array.dimension != 1){
            error("Input must be 1D NDArray")}
        val size = array.shape[0]
        if (offset>=0){
            return NDElement.real2D(size, size+offset){
                i, j -> if (i == j+offset) array[i] else 0.0
            }
        }
        else{
            return NDElement.real2D(size-offset, size){
                i, j -> if (i-offset == j) array[j] else 0.0
            }
        }
    }

    /**
     * Generate a [Vandermonde matrix](https://en.wikipedia.org/wiki/Vandermonde_matrix).
     *
     *  @param nCols --- number of columns, as default using length of [array]
     *  @param increasing --- Order of the powers of the columns. If True, the powers increase from left to right, if False (the default) they are reversed. FIXME: Default order like numpy
     */
    fun vandermonde(array : RealNDElement, nCols: Int = 0, increasing: Boolean =false): RealNDElement{
        if (array.dimension != 1){
            error("Input must be 1D NDArray")}
        var size = if (nCols ==0) array.shape[0] else nCols
        if (increasing){
            return NDElement.real2D(array.shape[0], size){
                i, j -> power(array[i], j)
            }
        }else{
            return NDElement.real2D(array.shape[0], size){
                i, j -> power(array[i], size - j - 1)
            }
        }

    }

}


