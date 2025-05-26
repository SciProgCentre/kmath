/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.misc.sortedWith
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import kotlin.math.floor

/**
 * Compute the quantile of a data [Buffer] at a specified probability [p] on the interval [0,1].
 * The class argument [sorted] indicates whether data [Buffer] can be assumed to be sorted;
 * if false (the default), then the elements of data [Buffer] will be partially sorted in-place using [comparator].
 *
 * Samples quantile are defined by `Q(p) = (1-γ)*x[j] + γ*x[j+1]`,
 * where `x[j]` is the j-th order statistic of `v`, `j = floor(n*p + m)`,
 * `m = alpha + p*(1 - alpha - beta)` and `γ = n*p + m - j`.
 *
 * By default (`alpha = beta = 1`), quantiles are computed via linear interpolation between the points
 * `((k-1)/(n-1), x[k])`, for `k = 1:n` where `n = length(v)`. This corresponds to Definition 7
 * of Hyndman and Fan (1996), and is the same as the R and NumPy default.
 *
 * The keyword arguments `alpha` and `beta` correspond to the same parameters in Hyndman and Fan,
 * setting them to different values allows to calculate quantiles with any of the methods 4-9
 * defined in this paper:
 * - Def. 4: `alpha=0`, `beta=1`
 * - Def. 5: `alpha=0.5`, `beta=0.5` (MATLAB default)
 * - Def. 6: `alpha=0`, `beta=0` (Excel `PERCENTILE.EXC`, Python default, Stata `altdef`)
 * - Def. 7: `alpha=1`, `beta=1` (Julia, R and NumPy default, Excel `PERCENTILE` and `PERCENTILE.INC`, Python `'inclusive'`)
 * - Def. 8: `alpha=1/3`, `beta=1/3`
 * - Def. 9: `alpha=3/8`, `beta=3/8`
 *
 * # References
 * - Hyndman, R.J and Fan, Y. (1996) "Sample Quantiles in Statistical Packages",
 *   *The American Statistician*, Vol. 50, No. 4, pp. 361-365
 *
 * - [Quantile on Wikipedia](https://en.wikipedia.org/wiki/Quantile) details the different quantile definitions
 *
 * # Notes
 * - Performance can be improved by using a selection algorithm instead of a complete sort.
 * - As further improvement, API can be redesigned support "a batch mode" - scenarios when several different quantiles are desired.
 *
 */
public class Quantile<T>(
    private val field: Field<T>,
    private val p: Float64,
    private val sorted: Boolean = false,
    private val comparator: Comparator<T>, //todo provide default?
    private val alpha: Float64 = 1.0,
    private val beta: Float64 = alpha,
) : BlockingStatistic<T, T> {


    init {
        require(p in 0.0..1.0) { "Quantile value must be in [0.0, 1.0] range. Got $p." }
        require(alpha in 0.0..1.0) { "alpha parameter must be in [0.0, 1.0] range. Got $alpha." }
        require(beta in 0.0..1.0) { "beta parameter must be in [0.0, 1.0] range. Got $beta." }
    }

    override fun evaluateBlocking(data: Buffer<T>): T {

        // adapted from https://github.com/JuliaStats/Statistics.jl/blob/master/src/Statistics.jl#L1045

        require(data.size > 0) { "Can't compute percentile of an empty buffer" }

        if (data.size == 1) {
            return data[0]
        }
        val n = data.size;
        val m = alpha + p * (1 - alpha - beta)
        val j = (floor(n*p + m).toInt()).coerceIn(1, n-1)
        val aleph = n*p + m;// todo use Math.fma in case of double or float for better accuracy
        val gamma = (aleph -j).coerceIn(0.0, 1.0)

        var sortedData = data
        if (!sorted) {
            sortedData = data.sortedWith(comparator)
        }

        with(field) {
             return sortedData[j-1] + gamma*(sortedData[j]-sortedData[j-1])
        }
    }

    public companion object {
        public fun evaluate(p: Float64, buffer: Buffer<Float64>): Double = Float64Field.quantile(p).evaluateBlocking(buffer)
    }

}

public fun Float64Field.quantile(p: Float64): Quantile<Float64> = Quantile(Float64Field, p, false, naturalOrder())


