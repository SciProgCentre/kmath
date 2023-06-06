/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.linear.transpose
import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.div
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.dot
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.minus
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.times
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.transposed
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.plus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.reflect.KFunction3

public enum class TypeOfConvergence{
    inRHS_JtWdy,
    inParameters,
    inReducedChi_square,
    noConvergence
}

public data class LMResultInfo (
    var iterations:Int,
    var func_calls: Int,
    var example_number: Int,
    var result_chi_sq: Double,
    var result_lambda: Double,
    var result_parameters: MutableStructure2D<Double>,
    var typeOfConvergence: TypeOfConvergence,
    var epsilon: Double
)

public fun DoubleTensorAlgebra.lm(
    func: KFunction3<MutableStructure2D<Double>, MutableStructure2D<Double>, LMSettings, MutableStructure2D<Double>>,
    p_input: MutableStructure2D<Double>, t_input: MutableStructure2D<Double>, y_dat_input: MutableStructure2D<Double>,
    weight_input: MutableStructure2D<Double>, dp_input: MutableStructure2D<Double>, p_min_input: MutableStructure2D<Double>, p_max_input: MutableStructure2D<Double>,
    c_input: MutableStructure2D<Double>, opts_input: DoubleArray, nargin: Int, example_number: Int): LMResultInfo {

    val resultInfo = LMResultInfo(0, 0, example_number, 0.0,
        0.0, p_input, TypeOfConvergence.noConvergence, 0.0)

    val eps:Double = 2.2204e-16

    val settings = LMSettings(0, 0, example_number)
    settings.func_calls = 0 // running count of function evaluations

    var p = p_input
    val y_dat = y_dat_input
    val t = t_input

    val Npar   = length(p)                                    // number of parameters
    val Npnt   = length(y_dat)                                // number of data points
    var p_old = zeros(ShapeND(intArrayOf(Npar, 1))).as2D()    // previous set of parameters
    var y_old = zeros(ShapeND(intArrayOf(Npnt, 1))).as2D()    // previous model, y_old = y_hat(t;p_old)
    var X2 = 1e-3 / eps                                       // a really big initial Chi-sq value
    var X2_old = 1e-3 / eps                                   // a really big initial Chi-sq value
    var J = zeros(ShapeND(intArrayOf(Npnt, Npar))).as2D()     // Jacobian matrix
    val DoF = Npnt - Npar                                     // statistical degrees of freedom

    var corr_p = 0
    var sigma_p = 0
    var sigma_y = 0
    var R_sq = 0
    var cvg_hist = 0

    if (length(t) != length(y_dat)) {
        // println("lm.m error: the length of t must equal the length of y_dat")
        val length_t = length(t)
        val length_y_dat = length(y_dat)
        X2 = 0.0

        corr_p = 0
        sigma_p = 0
        sigma_y = 0
        R_sq = 0
        cvg_hist = 0
    }

    var weight = weight_input
    if (nargin <  5) {
        weight = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf((y_dat.transpose().dot(y_dat)).as1D()[0])).as2D()
    }

    var dp = dp_input
    if (nargin < 6) {
        dp = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf(0.001)).as2D()
    }

    var p_min = p_min_input
    if (nargin < 7) {
        p_min = p
        p_min.abs()
        p_min = p_min.div(-100.0).as2D()
    }

    var p_max = p_max_input
    if (nargin < 8) {
        p_max = p
        p_max.abs()
        p_max = p_max.div(100.0).as2D()
    }

    var c = c_input
    if (nargin < 9) {
        c = fromArray(ShapeND(intArrayOf(1, 1)), doubleArrayOf(1.0)).as2D()
    }

    var opts = opts_input
    if (nargin < 10) {
        opts = doubleArrayOf(3.0, 10.0 * Npar, 1e-3, 1e-3, 1e-1, 1e-1, 1e-2, 11.0, 9.0, 1.0)
    }

    val prnt          = opts[0]                // >1 intermediate results; >2 plots
    val MaxIter       = opts[1].toInt()        // maximum number of iterations
    val epsilon_1     = opts[2]                // convergence tolerance for gradient
    val epsilon_2     = opts[3]                // convergence tolerance for parameters
    val epsilon_3     = opts[4]                // convergence tolerance for Chi-square
    val epsilon_4     = opts[5]                // determines acceptance of a L-M step
    val lambda_0      = opts[6]                // initial value of damping paramter, lambda
    val lambda_UP_fac = opts[7]                // factor for increasing lambda
    val lambda_DN_fac = opts[8]                // factor for decreasing lambda
    val Update_Type   = opts[9].toInt()        // 1: Levenberg-Marquardt lambda update
    // 2: Quadratic update
    // 3: Nielsen's lambda update equations

    p_min = make_column(p_min)
    p_max = make_column(p_max)

    if (length(make_column(dp)) == 1) {
        dp = ones(ShapeND(intArrayOf(Npar, 1))).div(1 / dp[0, 0]).as2D()
    }

    val idx = get_zero_indices(dp)                 // indices of the parameters to be fit
    val Nfit = idx?.shape?.component1()            // number of parameters to fit
    var stop = false                               // termination flag
    val y_init = feval(func, t, p, settings)       // residual error using p_try

    if (weight.shape.component1() == 1 || variance(weight) == 0.0) { // identical weights vector
        weight = ones(ShapeND(intArrayOf(Npnt, 1))).div(1 / kotlin.math.abs(weight[0, 0])).as2D()
        // println("using uniform weights for error analysis")
    }
    else {
        weight = make_column(weight)
        weight.abs()
    }

    // initialize Jacobian with finite difference calculation
    var lm_matx_ans = lm_matx(func, t, p_old, y_old,1, J, p, y_dat, weight, dp, settings)
    var JtWJ = lm_matx_ans[0]
    var JtWdy = lm_matx_ans[1]
    X2 = lm_matx_ans[2][0, 0]
    var y_hat = lm_matx_ans[3]
    J = lm_matx_ans[4]

    if ( abs(JtWdy).max()!! < epsilon_1 ) {
//            println(" *** Your Initial Guess is Extremely Close to Optimal ***\n")
//            println(" *** epsilon_1 = %e\n$epsilon_1")
        stop = true
    }

    var lambda = 1.0
    var nu = 1
    when (Update_Type) {
        1 -> lambda  = lambda_0                       // Marquardt: init'l lambda
        else -> {                                     // Quadratic and Nielsen
            lambda  = lambda_0 * (diag(JtWJ)).max()!!
            nu = 2
        }
    }

    X2_old = X2 // previous value of X2
    var cvg_hst = ones(ShapeND(intArrayOf(MaxIter, Npar + 3)))         // initialize convergence history

    var h: DoubleTensor
    var dX2 = X2
    while (!stop && settings.iteration <= MaxIter) {                   //--- Start Main Loop
        settings.iteration += 1

        // incremental change in parameters
        h = when (Update_Type) {
            1 -> {                // Marquardt
                val solve = solve(JtWJ.plus(make_matrx_with_diagonal(diag(JtWJ)).div(1 / lambda)).as2D(), JtWdy)
                solve.asDoubleTensor()
            }

            else -> {             // Quadratic and Nielsen
                val solve = solve(JtWJ.plus(lm_eye(Npar).div(1 / lambda)).as2D(), JtWdy)
                solve.asDoubleTensor()
            }
        }

        var p_try = (p + h).as2D()  // update the [idx] elements
        p_try = smallest_element_comparison(largest_element_comparison(p_min, p_try.as2D()), p_max)  // apply constraints

        var delta_y = y_dat.minus(feval(func, t, p_try, settings))   // residual error using p_try

        for (i in 0 until delta_y.shape.component1()) {  // floating point error; break
            for (j in 0 until delta_y.shape.component2()) {
                if (delta_y[i, j] == Double.POSITIVE_INFINITY || delta_y[i, j] == Double.NEGATIVE_INFINITY) {
                    stop = true
                    break
                }
            }
        }

        settings.func_calls += 1

        val tmp = delta_y.times(weight)
        var X2_try = delta_y.as2D().transpose().dot(tmp)     // Chi-squared error criteria

        val alpha = 1.0
        if (Update_Type == 2) { // Quadratic
            // One step of quadratic line update in the h direction for minimum X2

            val alpha = JtWdy.transpose().dot(h) / ( (X2_try.minus(X2)).div(2.0).plus(2 * JtWdy.transpose().dot(h)) )
            h = h.dot(alpha)
            p_try = p.plus(h).as2D() // update only [idx] elements
            p_try = smallest_element_comparison(largest_element_comparison(p_min, p_try), p_max) // apply constraints

            var delta_y = y_dat.minus(feval(func, t, p_try, settings))   // residual error using p_try
            settings.func_calls += 1

            val tmp = delta_y.times(weight)
            X2_try = delta_y.as2D().transpose().dot(tmp)     // Chi-squared error criteria
        }

        val rho = when (Update_Type) { // Nielsen
            1 -> {
                val tmp = h.transposed().dot(make_matrx_with_diagonal(diag(JtWJ)).div(1 / lambda).dot(h).plus(JtWdy))
                X2.minus(X2_try).as2D()[0, 0] / abs(tmp.as2D()).as2D()[0, 0]
            }
            else -> {
                val tmp = h.transposed().dot(h.div(1 / lambda).plus(JtWdy))
                X2.minus(X2_try).as2D()[0, 0] / abs(tmp.as2D()).as2D()[0, 0]
            }
        }

        if (rho > epsilon_4) { // it IS significantly better
            val dX2 = X2.minus(X2_old)
            X2_old = X2
            p_old = p.copyToTensor().as2D()
            y_old = y_hat.copyToTensor().as2D()
            p = make_column(p_try) // accept p_try

            lm_matx_ans = lm_matx(func, t, p_old, y_old, dX2.toInt(), J, p, y_dat, weight, dp, settings)
            // decrease lambda ==> Gauss-Newton method

            JtWJ = lm_matx_ans[0]
            JtWdy = lm_matx_ans[1]
            X2 = lm_matx_ans[2][0, 0]
            y_hat = lm_matx_ans[3]
            J = lm_matx_ans[4]

            lambda = when (Update_Type) {
                1 -> { // Levenberg
                    max(lambda / lambda_DN_fac, 1e-7);
                }
                2 -> { // Quadratic
                    max( lambda / (1 + alpha) , 1e-7 );
                }
                else -> { // Nielsen
                    nu = 2
                    lambda * max( 1.0 / 3, 1 - (2 * rho - 1).pow(3) )
                }
            }
        }
        else { // it IS NOT better
            X2 = X2_old // do not accept p_try
            if (settings.iteration % (2 * Npar) == 0 ) { // rank-1 update of Jacobian
                lm_matx_ans = lm_matx(func, t, p_old, y_old,-1, J, p, y_dat, weight, dp, settings)
                JtWJ = lm_matx_ans[0]
                JtWdy = lm_matx_ans[1]
                dX2 = lm_matx_ans[2][0, 0]
                y_hat = lm_matx_ans[3]
                J = lm_matx_ans[4]
            }

            // increase lambda  ==> gradient descent method
            lambda = when (Update_Type) {
                1 -> { // Levenberg
                    min(lambda * lambda_UP_fac, 1e7)
                }
                2 -> { // Quadratic
                    lambda + kotlin.math.abs(((X2_try.as2D()[0, 0] - X2) / 2) / alpha)
                }
                else -> { // Nielsen
                    nu *= 2
                    lambda * (nu / 2)
                }
            }
        }

        if (prnt > 1) {
            val chi_sq = X2 / DoF
//                println("Iteration $settings | chi_sq=$chi_sq | lambda=$lambda")
//                print("param: ")
//                for (pn in 0 until Npar) {
//                    print(p[pn, 0].toString() + " ")
//                }
//                print("\ndp/p: ")
//                for (pn in 0 until Npar) {
//                    print((h.as2D()[pn, 0] / p[pn, 0]).toString() + " ")
//                }
            resultInfo.iterations = settings.iteration
            resultInfo.func_calls = settings.func_calls
            resultInfo.result_chi_sq = chi_sq
            resultInfo.result_lambda = lambda
            resultInfo.result_parameters = p
        }

        // update convergence history ... save _reduced_ Chi-square
        // cvg_hst(iteration,:) = [ func_calls  p'  X2/DoF lambda ];

        if (abs(JtWdy).max()!! < epsilon_1 && settings.iteration > 2) {
//                println(" **** Convergence in r.h.s. (\"JtWdy\")  ****")
//                println(" **** epsilon_1 = $epsilon_1")
            resultInfo.typeOfConvergence = TypeOfConvergence.inRHS_JtWdy
            resultInfo.epsilon = epsilon_1
            stop = true
        }
        if ((abs(h.as2D()).div(abs(p) + 1e-12)).max() < epsilon_2  &&  settings.iteration > 2) {
//                println(" **** Convergence in Parameters ****")
//                println(" **** epsilon_2 = $epsilon_2")
            resultInfo.typeOfConvergence = TypeOfConvergence.inParameters
            resultInfo.epsilon = epsilon_2
            stop = true
        }
        if (X2 / DoF < epsilon_3 && settings.iteration > 2) {
//                println(" **** Convergence in reduced Chi-square  **** ")
//                println(" **** epsilon_3 = $epsilon_3")
            resultInfo.typeOfConvergence = TypeOfConvergence.inReducedChi_square
            resultInfo.epsilon = epsilon_3
            stop = true
        }
        if (settings.iteration == MaxIter) {
//                println(" !! Maximum Number of Iterations Reached Without Convergence !!")
            resultInfo.typeOfConvergence = TypeOfConvergence.noConvergence
            resultInfo.epsilon = 0.0
            stop = true
        }
    }  // --- End of Main Loop
    return resultInfo
}

public data class LMSettings (
    var iteration:Int,
    var func_calls: Int,
    var example_number:Int
)

/* matrix -> column of all elemnets */
public fun make_column(tensor: MutableStructure2D<Double>) : MutableStructure2D<Double> {
    val shape = intArrayOf(tensor.shape.component1() * tensor.shape.component2(), 1)
    val buffer = DoubleArray(tensor.shape.component1() * tensor.shape.component2())
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            buffer[i * tensor.shape.component2() + j] = tensor[i, j]
        }
    }
    val column = BroadcastDoubleTensorAlgebra.fromArray(ShapeND(shape), buffer).as2D()
    return column
}

/* column length */
public fun length(column: MutableStructure2D<Double>) : Int {
    return column.shape.component1()
}

public fun MutableStructure2D<Double>.abs() {
    for (i in 0 until this.shape.component1()) {
        for (j in 0 until this.shape.component2()) {
            this[i, j] = kotlin.math.abs(this[i, j])
        }
    }
}

public fun abs(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(
        ShapeND(
            intArrayOf(
                input.shape.component1(),
                input.shape.component2()
            )
        )
    ).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            tensor[i, j] = kotlin.math.abs(input[i, j])
        }
    }
    return tensor
}

public fun diag(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(input.shape.component1(), 1))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        tensor[i, 0] = input[i, i]
    }
    return tensor
}

public fun make_matrx_with_diagonal(column: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val size = column.shape.component1()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(size, size))).as2D()
    for (i in 0 until size) {
        tensor[i, i] = column[i, 0]
    }
    return tensor
}

public fun lm_eye(size: Int): MutableStructure2D<Double> {
    val column = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(size, 1))).as2D()
    return make_matrx_with_diagonal(column)
}

public fun largest_element_comparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val a_sizeX = a.shape.component1()
    val a_sizeY = a.shape.component2()
    val b_sizeX = b.shape.component1()
    val b_sizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(a_sizeX, b_sizeX), max(a_sizeY, b_sizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < a_sizeX && i < b_sizeX && j < a_sizeY && j < b_sizeY) {
                tensor[i, j] = max(a[i, j], b[i, j])
            }
            else if (i < a_sizeX && j < a_sizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

public fun smallest_element_comparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val a_sizeX = a.shape.component1()
    val a_sizeY = a.shape.component2()
    val b_sizeX = b.shape.component1()
    val b_sizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(a_sizeX, b_sizeX), max(a_sizeY, b_sizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < a_sizeX && i < b_sizeX && j < a_sizeY && j < b_sizeY) {
                tensor[i, j] = min(a[i, j], b[i, j])
            }
            else if (i < a_sizeX && j < a_sizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

public fun get_zero_indices(column: MutableStructure2D<Double>, epsilon: Double = 0.000001): MutableStructure2D<Double>? {
    var idx = emptyArray<Double>()
    for (i in 0 until column.shape.component1()) {
        if (kotlin.math.abs(column[i, 0]) > epsilon) {
            idx += (i + 1.0)
        }
    }
    if (idx.size > 0) {
        return BroadcastDoubleTensorAlgebra.fromArray(ShapeND(intArrayOf(idx.size, 1)), idx.toDoubleArray()).as2D()
    }
    return null
}

public fun feval(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, LMSettings) ->  MutableStructure2D<Double>,
                 t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, settings: LMSettings)
        : MutableStructure2D<Double>
{
    return func(t, p, settings)
}

public fun lm_matx(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, LMSettings) -> MutableStructure2D<Double>,
                   t: MutableStructure2D<Double>, p_old: MutableStructure2D<Double>, y_old: MutableStructure2D<Double>,
                   dX2: Int, J_input: MutableStructure2D<Double>, p: MutableStructure2D<Double>,
                   y_dat: MutableStructure2D<Double>, weight: MutableStructure2D<Double>, dp:MutableStructure2D<Double>, settings:LMSettings) : Array<MutableStructure2D<Double>>
{
    // default: dp = 0.001

    val Npnt = length(y_dat)               // number of data points
    val Npar = length(p)                   // number of parameters

    val y_hat = feval(func, t, p, settings)          // evaluate model using parameters 'p'
    settings.func_calls += 1

    var J = J_input

    if (settings.iteration % (2 * Npar) == 0 || dX2 > 0) {
        J = lm_FD_J(func, t, p, y_hat, dp, settings).as2D() // finite difference
    }
    else {
        J = lm_Broyden_J(p_old, y_old, J, p, y_hat).as2D() // rank-1 update
    }

    val delta_y = y_dat.minus(y_hat)

    val Chi_sq = delta_y.transposed().dot( delta_y.times(weight) ).as2D()
    val JtWJ = J.transposed().dot ( J.times( weight.dot(BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(1, Npar)))) ) ).as2D()
    val JtWdy = J.transposed().dot( weight.times(delta_y) ).as2D()

    return arrayOf(JtWJ,JtWdy,Chi_sq,y_hat,J)
}

public fun lm_Broyden_J(p_old: MutableStructure2D<Double>, y_old: MutableStructure2D<Double>, J_input: MutableStructure2D<Double>,
                        p: MutableStructure2D<Double>, y: MutableStructure2D<Double>): MutableStructure2D<Double> {
    var J = J_input.copyToTensor()

    val h = p.minus(p_old)
    val increase = y.minus(y_old).minus( J.dot(h) ).dot(h.transposed()).div( (h.transposed().dot(h)).as2D()[0, 0] )
    J = J.plus(increase)

    return J.as2D()
}

public fun lm_FD_J(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, settings: LMSettings) -> MutableStructure2D<Double>,
                   t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, y: MutableStructure2D<Double>,
                   dp: MutableStructure2D<Double>, settings: LMSettings): MutableStructure2D<Double> {
    // default: dp = 0.001 * ones(1,n)

    val m = length(y)              // number of data points
    val n = length(p)              // number of parameters

    val ps = p.copyToTensor().as2D()
    val J = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(m, n))).as2D()  // initialize Jacobian to Zero
    val del = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(n, 1))).as2D()

    for (j in 0 until n) {

        del[j, 0] = dp[j, 0] * (1 + kotlin.math.abs(p[j, 0])) // parameter perturbation
        p[j, 0] = ps[j, 0] + del[j, 0]            // perturb parameter p(j)

        val epsilon = 0.0000001
        if (kotlin.math.abs(del[j, 0]) > epsilon) {
            val y1 = feval(func, t, p, settings)
            settings.func_calls += 1

            if (dp[j, 0] < 0) { // backwards difference
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(y).as2D())[i, 0] / del[j, 0]
                }
            }
            else {
                // Do tests for it
                println("Potential mistake")
                p[j, 0] = ps[j, 0] - del[j, 0] // central difference, additional func call
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(feval(func, t, p, settings)).as2D())[i, 0] / (2 * del[j, 0])
                }
                settings.func_calls += 1
            }
        }

        p[j, 0] = ps[j, 0] // restore p(j)
    }

    return J.as2D()
}
