package kscience.kmath.interpolation
//
//import kscience.kmath.functions.PiecewisePolynomial
//import kscience.kmath.operations.Ring
//import kscience.kmath.structures.Buffer
//import kotlin.math.abs
//import kotlin.math.sqrt
//
//
///**
// * Original code: https://github.com/apache/commons-math/blob/eb57d6d457002a0bb5336d789a3381a24599affe/src/main/java/org/apache/commons/math4/analysis/interpolation/LoessInterpolator.java
// */
//class LoessInterpolator<T : Comparable<T>>(override val algebra: Ring<T>) : PolynomialInterpolator<T> {
//    /**
//     * The bandwidth parameter: when computing the loess fit at
//     * a particular point, this fraction of source points closest
//     * to the current point is taken into account for computing
//     * a least-squares regression.
//     *
//     *
//     * A sensible value is usually 0.25 to 0.5.
//     */
//    private var bandwidth = 0.0
//
//    /**
//     * The number of robustness iterations parameter: this many
//     * robustness iterations are done.
//     *
//     *
//     * A sensible value is usually 0 (just the initial fit without any
//     * robustness iterations) to 4.
//     */
//    private var robustnessIters = 0
//
//    /**
//     * If the median residual at a certain robustness iteration
//     * is less than this amount, no more iterations are done.
//     */
//    private var accuracy = 0.0
//
//    /**
//     * Constructs a new [LoessInterpolator]
//     * with a bandwidth of [.DEFAULT_BANDWIDTH],
//     * [.DEFAULT_ROBUSTNESS_ITERS] robustness iterations
//     * and an accuracy of {#link #DEFAULT_ACCURACY}.
//     * See [.LoessInterpolator] for an explanation of
//     * the parameters.
//     */
//    fun LoessInterpolator() {
//        bandwidth = DEFAULT_BANDWIDTH
//        robustnessIters = DEFAULT_ROBUSTNESS_ITERS
//        accuracy = DEFAULT_ACCURACY
//    }
//
//    fun LoessInterpolator(bandwidth: Double, robustnessIters: Int) {
//        this(bandwidth, robustnessIters, DEFAULT_ACCURACY)
//    }
//
//    fun LoessInterpolator(bandwidth: Double, robustnessIters: Int, accuracy: Double) {
//        if (bandwidth < 0 ||
//            bandwidth > 1
//        ) {
//            throw OutOfRangeException(LocalizedFormats.BANDWIDTH, bandwidth, 0, 1)
//        }
//        this.bandwidth = bandwidth
//        if (robustnessIters < 0) {
//            throw NotPositiveException(LocalizedFormats.ROBUSTNESS_ITERATIONS, robustnessIters)
//        }
//        this.robustnessIters = robustnessIters
//        this.accuracy = accuracy
//    }
//
//    fun interpolate(
//        xval: DoubleArray,
//        yval: DoubleArray
//    ): PolynomialSplineFunction {
//        return SplineInterpolator().interpolate(xval, smooth(xval, yval))
//    }
//
//    fun XYZPointSet<Double, Double, Double>.smooth(): XYPointSet<Double, Double> {
//        checkAllFiniteReal(x)
//        checkAllFiniteReal(y)
//        checkAllFiniteReal(z)
//        MathArrays.checkOrder(xval)
//        if (size == 1) {
//            return doubleArrayOf(y[0])
//        }
//        if (size == 2) {
//            return doubleArrayOf(y[0], y[1])
//        }
//        val bandwidthInPoints = (bandwidth * size).toInt()
//        if (bandwidthInPoints < 2) {
//            throw NumberIsTooSmallException(
//                LocalizedFormats.BANDWIDTH,
//                bandwidthInPoints, 2, true
//            )
//        }
//        val res = DoubleArray(size)
//        val residuals = DoubleArray(size)
//        val sortedResiduals = DoubleArray(size)
//        val robustnessWeights = DoubleArray(size)
//        // Do an initial fit and 'robustnessIters' robustness iterations.
//        // This is equivalent to doing 'robustnessIters+1' robustness iterations
//        // starting with all robustness weights set to 1.
//        Arrays.fill(robustnessWeights, 1.0)
//        for (iter in 0..robustnessIters) {
//            val bandwidthInterval = intArrayOf(0, bandwidthInPoints - 1)
//            // At each x, compute a local weighted linear regression
//            for (i in 0 until size) {
////                val x = x[i]
//                // Find out the interval of source points on which
//                // a regression is to be made.
//                if (i > 0) {
//                    updateBandwidthInterval(x, z, i, bandwidthInterval)
//                }
//                val ileft = bandwidthInterval[0]
//                val iright = bandwidthInterval[1]
//                // Compute the point of the bandwidth interval that is
//                // farthest from x
//                val edge: Int
//                edge = if (x[i] - x[ileft] > x[iright] - x[i]) {
//                    ileft
//                } else {
//                    iright
//                }
//                // Compute a least-squares linear fit weighted by
//                // the product of robustness weights and the tricube
//                // weight function.
//                // See http://en.wikipedia.org/wiki/Linear_regression
//                // (section "Univariate linear case")
//                // and http://en.wikipedia.org/wiki/Weighted_least_squares
//                // (section "Weighted least squares")
//                var sumWeights = 0.0
//                var sumX = 0.0
//                var sumXSquared = 0.0
//                var sumY = 0.0
//                var sumXY = 0.0
//                val denom: Double = abs(1.0 / (x[edge] - x[i]))
//                for (k in ileft..iright) {
//                    val xk = x[k]
//                    val yk = y[k]
//                    val dist = if (k < i) x - xk else xk - x[i]
//                    val w = tricube(dist * denom) * robustnessWeights[k] * z[k]
//                    val xkw = xk * w
//                    sumWeights += w
//                    sumX += xkw
//                    sumXSquared += xk * xkw
//                    sumY += yk * w
//                    sumXY += yk * xkw
//                }
//                val meanX = sumX / sumWeights
//                val meanY = sumY / sumWeights
//                val meanXY = sumXY / sumWeights
//                val meanXSquared = sumXSquared / sumWeights
//                val beta: Double
//                beta = if (sqrt(abs(meanXSquared - meanX * meanX)) < accuracy) {
//                    0.0
//                } else {
//                    (meanXY - meanX * meanY) / (meanXSquared - meanX * meanX)
//                }
//                val alpha = meanY - beta * meanX
//                res[i] = beta * x[i] + alpha
//                residuals[i] = abs(y[i] - res[i])
//            }
//            // No need to recompute the robustness weights at the last
//            // iteration, they won't be needed anymore
//            if (iter == robustnessIters) {
//                break
//            }
//            // Recompute the robustness weights.
//            // Find the median residual.
//            // An arraycopy and a sort are completely tractable here,
//            // because the preceding loop is a lot more expensive
//            java.lang.System.arraycopy(residuals, 0, sortedResiduals, 0, size)
//            Arrays.sort(sortedResiduals)
//            val medianResidual = sortedResiduals[size / 2]
//            if (abs(medianResidual) < accuracy) {
//                break
//            }
//            for (i in 0 until size) {
//                val arg = residuals[i] / (6 * medianResidual)
//                if (arg >= 1) {
//                    robustnessWeights[i] = 0.0
//                } else {
//                    val w = 1 - arg * arg
//                    robustnessWeights[i] = w * w
//                }
//            }
//        }
//        return res
//    }
//
//    fun smooth(xval: DoubleArray, yval: DoubleArray): DoubleArray {
//        if (xval.size != yval.size) {
//            throw DimensionMismatchException(xval.size, yval.size)
//        }
//        val unitWeights = DoubleArray(xval.size)
//        Arrays.fill(unitWeights, 1.0)
//        return smooth(xval, yval, unitWeights)
//    }
//
//    /**
//     * Given an index interval into xval that embraces a certain number of
//     * points closest to `xval[i-1]`, update the interval so that it
//     * embraces the same number of points closest to `xval[i]`,
//     * ignoring zero weights.
//     *
//     * @param xval Arguments array.
//     * @param weights Weights array.
//     * @param i Index around which the new interval should be computed.
//     * @param bandwidthInterval a two-element array {left, right} such that:
//     * `(left==0 or xval[i] - xval[left-1] > xval[right] - xval[i])`
//     * and
//     * `(right==xval.length-1 or xval[right+1] - xval[i] > xval[i] - xval[left])`.
//     * The array will be updated.
//     */
//    private fun updateBandwidthInterval(
//        xval: Buffer<Double>, weights: Buffer<Double>,
//        i: Int,
//        bandwidthInterval: IntArray
//    ) {
//        val left = bandwidthInterval[0]
//        val right = bandwidthInterval[1]
//        // The right edge should be adjusted if the next point to the right
//        // is closer to xval[i] than the leftmost point of the current interval
//        val nextRight = nextNonzero(weights, right)
//        if (nextRight < xval.size && xval[nextRight] - xval[i] < xval[i] - xval[left]) {
//            val nextLeft = nextNonzero(weights, bandwidthInterval[0])
//            bandwidthInterval[0] = nextLeft
//            bandwidthInterval[1] = nextRight
//        }
//    }
//
//    /**
//     * Return the smallest index `j` such that
//     * `j > i && (j == weights.length || weights[j] != 0)`.
//     *
//     * @param weights Weights array.
//     * @param i Index from which to start search.
//     * @return the smallest compliant index.
//     */
//    private fun nextNonzero(weights: Buffer<Double>, i: Int): Int {
//        var j = i + 1
//        while (j < weights.size && weights[j] == 0.0) {
//            ++j
//        }
//        return j
//    }
//
//    /**
//     * Compute the
//     * [tricube](http://en.wikipedia.org/wiki/Local_regression#Weight_function)
//     * weight function
//     *
//     * @param x Argument.
//     * @return `(1 - |x|<sup>3</sup>)<sup>3</sup>` for |x| &lt; 1, 0 otherwise.
//     */
//    private fun tricube(x: Double): Double {
//        val absX: Double = FastMath.abs(x)
//        if (absX >= 1.0) {
//            return 0.0
//        }
//        val tmp = 1 - absX * absX * absX
//        return tmp * tmp * tmp
//    }
//
//    /**
//     * Check that all elements of an array are finite real numbers.
//     *
//     * @param values Values array.
//     * @throws org.apache.commons.math4.exception.NotFiniteNumberException
//     * if one of the values is not a finite real number.
//     */
//    private fun checkAllFiniteReal(values: DoubleArray) {
//        for (i in values.indices) {
//            MathUtils.checkFinite(values[i])
//        }
//    }
//
//    override fun interpolatePolynomials(points: Collection<Pair<T, T>>): PiecewisePolynomial<T> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    companion object {
//        /** Default value of the bandwidth parameter.  */
//        const val DEFAULT_BANDWIDTH = 0.3
//
//        /** Default value of the number of robustness iterations.  */
//        const val DEFAULT_ROBUSTNESS_ITERS = 2
//
//        /**
//         * Default value for accuracy.
//         */
//        const val DEFAULT_ACCURACY = 1e-12
//    }
//}