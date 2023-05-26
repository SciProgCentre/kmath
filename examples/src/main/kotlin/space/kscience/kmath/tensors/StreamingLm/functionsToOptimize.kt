/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.StreamingLm

import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.component1
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.max
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.plus
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.pow
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.times
import space.kscience.kmath.tensors.core.internal.LMSettings

public data class StartDataLm (
    var lm_matx_y_dat: MutableStructure2D<Double>,
    var example_number: Int,
    var p_init: MutableStructure2D<Double>,
    var t: MutableStructure2D<Double>,
    var y_dat: MutableStructure2D<Double>,
    var weight: MutableStructure2D<Double>,
    var dp: MutableStructure2D<Double>,
    var p_min: MutableStructure2D<Double>,
    var p_max: MutableStructure2D<Double>,
    var consts: MutableStructure2D<Double>,
    var opts: DoubleArray
)

fun func1ForLm(t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, settings: LMSettings): MutableStructure2D<Double> {
    val m = t.shape.component1()
    var y_hat = DoubleTensorAlgebra.zeros(ShapeND(intArrayOf (m, 1)))

    if (settings.example_number == 1) {
        y_hat = DoubleTensorAlgebra.exp((t.times(-1.0 / p[1, 0]))).times(p[0, 0]) + t.times(p[2, 0]).times(
            DoubleTensorAlgebra.exp((t.times(-1.0 / p[3, 0])))
        )
    }
    else if (settings.example_number == 2) {
        val mt = t.max()
        y_hat = (t.times(1.0 / mt)).times(p[0, 0]) +
                (t.times(1.0 / mt)).pow(2).times(p[1, 0]) +
                (t.times(1.0 / mt)).pow(3).times(p[2, 0]) +
                (t.times(1.0 / mt)).pow(4).times(p[3, 0])
    }
    else if (settings.example_number == 3) {
        y_hat = DoubleTensorAlgebra.exp((t.times(-1.0 / p[1, 0])))
            .times(p[0, 0]) + DoubleTensorAlgebra.sin((t.times(1.0 / p[3, 0]))).times(p[2, 0])
    }

    return y_hat.as2D()
}

fun getStartDataForFunc1(): StartDataLm {
    val lm_matx_y_dat = doubleArrayOf(
        19.6594, 18.6096, 17.6792, 17.2747, 16.3065, 17.1458, 16.0467, 16.7023, 15.7809, 15.9807,
        14.7620, 15.1128, 16.0973, 15.1934, 15.8636, 15.4763, 15.6860, 15.1895, 15.3495, 16.6054,
        16.2247, 15.9854, 16.1421, 17.0960, 16.7769, 17.1997, 17.2767, 17.5882, 17.5378, 16.7894,
        17.7648, 18.2512, 18.1581, 16.7037, 17.8475, 17.9081, 18.3067, 17.9632, 18.2817, 19.1427,
        18.8130, 18.5658, 18.0056, 18.4607, 18.5918, 18.2544, 18.3731, 18.7511, 19.3181, 17.3066,
        17.9632, 19.0513, 18.7528, 18.2928, 18.5967, 17.8567, 17.7859, 18.4016, 18.9423, 18.4959,
        17.8000, 18.4251, 17.7829, 17.4645, 17.5221, 17.3517, 17.4637, 17.7563, 16.8471, 17.4558,
        17.7447, 17.1487, 17.3183, 16.8312, 17.7551, 17.0942, 15.6093, 16.4163, 15.3755, 16.6725,
        16.2332, 16.2316, 16.2236, 16.5361, 15.3721, 15.3347, 15.5815, 15.6319, 14.4538, 14.6044,
        14.7665, 13.3718, 15.0587, 13.8320, 14.7873, 13.6824, 14.2579, 14.2154, 13.5818, 13.8157
    )

    var example_number = 1
    val p_init = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(4, 1)), doubleArrayOf(5.0, 2.0, 0.2, 10.0)
    ).as2D()

    var t = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(100, 1))).as2D()
    for (i in 0 until 100) {
        t[i, 0] = t[i, 0] * (i + 1)
    }

    val y_dat = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(100, 1)), lm_matx_y_dat
    ).as2D()

    val weight = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), DoubleArray(1) { 4.0 }
    ).as2D()

    val dp = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
    ).as2D()

    val p_min = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(4, 1)), doubleArrayOf(-50.0, -20.0, -2.0, -100.0)
    ).as2D()

    val p_max = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(4, 1)), doubleArrayOf(50.0, 20.0, 2.0, 100.0)
    ).as2D()

    val consts = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), doubleArrayOf(0.0)
    ).as2D()

    val opts = doubleArrayOf(3.0, 100.0, 1e-3, 1e-3, 1e-1, 1e-1, 1e-2, 11.0, 9.0, 1.0)

    return StartDataLm(y_dat, example_number, p_init, t, y_dat, weight, dp, p_min, p_max, consts, opts)
}